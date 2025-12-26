package com.florist.io;

import com.florist.model.Flower;
import com.florist.model.Sale;
import com.florist.model.Supplier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for importing data from CSV files.
 * Demonstrates File I/O functionality (Phase 5 requirement).
 */
public class FileImportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Import flowers from CSV file.
     */
    public List<Flower> importFlowersFromCsv(String filePath) throws IOException {
        List<Flower> flowers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip header
            String line = reader.readLine();

            // Read data
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length >= 9) {
                    Flower flower = new Flower();
                    flower.setId(Integer.parseInt(parts[0]));
                    flower.setName(parts[1]);
                    flower.setColor(parts[2]);
                    flower.setCategory(parts[3]);
                    flower.setPrice(Double.parseDouble(parts[4]));
                    flower.setQuantity(Integer.parseInt(parts[5]));

                    if (!parts[6].isEmpty()) {
                        flower.setArrivalDate(LocalDate.parse(parts[6], DATE_FORMATTER));
                    }

                    flower.setFreshnessDays(Integer.parseInt(parts[7]));
                    flower.setSupplierId(Integer.parseInt(parts[8]));

                    flowers.add(flower);
                }
            }
        }

        return flowers;
    }

    /**
     * Import sales from CSV file.
     */
    public List<Sale> importSalesFromCsv(String filePath) throws IOException {
        List<Sale> sales = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip header
            String line = reader.readLine();

            // Read data
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length >= 6) {
                    Sale sale = new Sale();
                    sale.setId(Integer.parseInt(parts[0]));

                    if (!parts[1].isEmpty()) {
                        sale.setSaleDate(LocalDate.parse(parts[1], DATE_FORMATTER));
                    }

                    sale.setFlowerId(Integer.parseInt(parts[2]));
                    sale.setQuantitySold(Integer.parseInt(parts[3]));
                    sale.setTotalPrice(Double.parseDouble(parts[4]));
                    sale.setCustomerName(parts[5]);

                    sales.add(sale);
                }
            }
        }

        return sales;
    }

    /**
     * Import suppliers from CSV file.
     */
    public List<Supplier> importSuppliersFromCsv(String filePath) throws IOException {
        List<Supplier> suppliers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip header
            String line = reader.readLine();

            // Read data
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length >= 4) {
                    Supplier supplier = new Supplier();
                    supplier.setId(Integer.parseInt(parts[0]));
                    supplier.setName(parts[1]);
                    supplier.setPhone(parts[2]);
                    supplier.setEmail(parts[3]);

                    suppliers.add(supplier);
                }
            }
        }

        return suppliers;
    }

    /**
     * Parse CSV line handling quoted fields.
     */
    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());

        return result.toArray(new String[0]);
    }
}
