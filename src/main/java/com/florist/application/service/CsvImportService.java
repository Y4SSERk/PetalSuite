package com.florist.application.service;

import com.florist.model.Flower;
import com.florist.model.Sale;
import com.florist.model.Supplier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Service for importing data from CSV files.
 */
public class CsvImportService {
    private final FlowerService flowerService;
    private final SaleService saleService;
    private final SupplierService supplierService;

    public CsvImportService(FlowerService flowerService, SaleService saleService, SupplierService supplierService) {
        this.flowerService = flowerService;
        this.saleService = saleService;
        this.supplierService = supplierService;
    }

    /**
     * Imports flowers from a CSV file.
     * Expected format:
     * ID,Name,Color,Category,Price,Quantity,ArrivalDate,FreshnessDays,SupplierID
     */
    public int importFlowers(File file) throws Exception {
        List<String[]> rows = readCsv(file);
        int count = 0;
        for (String[] row : rows) {
            if (row.length < 9)
                continue;

            Flower flower = new Flower();
            // We ignore ID and let the DB generate it
            flower.setName(row[1]);
            flower.setColor(row[2]);
            flower.setCategory(row[3]);
            flower.setPrice(Double.parseDouble(row[4]));
            flower.setQuantity(Integer.parseInt(row[5]));
            flower.setArrivalDate(LocalDate.parse(row[6]));
            flower.setFreshnessDays(Integer.parseInt(row[7]));
            flower.setSupplierId(Integer.parseInt(row[8]));

            flowerService.createFlower(flower);
            count++;
        }
        return count;
    }

    /**
     * Imports sales from a CSV file.
     * Expected format: ID,SaleDate,FlowerID,QuantitySold,TotalPrice,CustomerName
     */
    public int importSales(File file) throws Exception {
        List<String[]> rows = readCsv(file);
        int count = 0;
        for (String[] row : rows) {
            if (row.length < 6)
                continue;

            Sale sale = new Sale();
            // We ignore ID
            sale.setSaleDate(LocalDate.parse(row[1]));
            sale.setFlowerId(Integer.parseInt(row[2]));
            sale.setQuantitySold(Integer.parseInt(row[3]));
            sale.setTotalPrice(Double.parseDouble(row[4]));
            sale.setCustomerName(row[5]);

            // Calculate unit price for compatibility
            if (sale.getQuantitySold() > 0) {
                sale.setUnitPrice(sale.getTotalPrice() / sale.getQuantitySold());
            }

            saleService.processSale(sale);
            count++;
        }
        return count;
    }

    /**
     * Imports suppliers from a CSV file.
     * Expected format: ID,Name,Phone,Email
     */
    public int importSuppliers(File file) throws Exception {
        List<String[]> rows = readCsv(file);
        int count = 0;
        for (String[] row : rows) {
            if (row.length < 4)
                continue;

            Supplier supplier = new Supplier();
            // We ignore ID
            supplier.setName(row[1]);
            supplier.setPhone(row[2]);
            supplier.setEmail(row[3]);

            supplierService.createSupplier(supplier);
            count++;
        }
        return count;
    }

    private List<String[]> readCsv(File file) throws Exception {
        List<String[]> rows = new ArrayList<>();
        System.out.println("[CSV-IMPORT] Reading file: " + file.getAbsolutePath());

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                if (firstLine) {
                    System.out.println("[CSV-IMPORT] Skipping header: " + line);
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty())
                    continue;

                // Handle both comma and semicolon, and trim values
                String[] values;
                if (line.contains(";")) {
                    values = line.split(";");
                } else {
                    values = line.split(",");
                }

                // Trim each value and remove quotes if present
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                    if (values[i].startsWith("\"") && values[i].endsWith("\"")) {
                        values[i] = values[i].substring(1, values[i].length() - 1);
                    }
                }

                System.out.println("[CSV-IMPORT] Line " + lineNum + " parsed: " + Arrays.toString(values));
                rows.add(values);
            }
        }
        System.out.println("[CSV-IMPORT] Total rows read (excluding header): " + rows.size());
        return rows;
    }
}
