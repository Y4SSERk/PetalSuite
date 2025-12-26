package com.florist.io;

import com.florist.model.Flower;
import com.florist.model.Sale;
import com.florist.model.Supplier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for exporting data to CSV files.
 * Demonstrates File I/O functionality (Phase 5 requirement).
 */
public class FileExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String CSV_SEPARATOR = ",";

    /**
     * Export flowers to CSV file.
     */
    public void exportFlowersToCsv(List<Flower> flowers, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("ID,Name,Color,Category,Price,Quantity,ArrivalDate,FreshnessDays,SupplierID");
            writer.newLine();

            // Write data
            for (Flower flower : flowers) {
                String line = String.join(CSV_SEPARATOR,
                        String.valueOf(flower.getId()),
                        escapeCSV(flower.getName()),
                        escapeCSV(flower.getColor()),
                        escapeCSV(flower.getCategory()),
                        String.valueOf(flower.getPrice()),
                        String.valueOf(flower.getQuantity()),
                        flower.getArrivalDate() != null ? flower.getArrivalDate().format(DATE_FORMATTER) : "",
                        String.valueOf(flower.getFreshnessDays()),
                        String.valueOf(flower.getSupplierId()));
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Export sales to CSV file.
     */
    public void exportSalesToCsv(List<Sale> sales, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("ID,SaleDate,FlowerID,QuantitySold,TotalPrice,CustomerName");
            writer.newLine();

            // Write data
            for (Sale sale : sales) {
                String line = String.join(CSV_SEPARATOR,
                        String.valueOf(sale.getId()),
                        sale.getSaleDate() != null ? sale.getSaleDate().format(DATE_FORMATTER) : "",
                        String.valueOf(sale.getFlowerId()),
                        String.valueOf(sale.getQuantitySold()),
                        String.valueOf(sale.getTotalPrice()),
                        escapeCSV(sale.getCustomerName()));
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Export suppliers to CSV file.
     */
    public void exportSuppliersToCsv(List<Supplier> suppliers, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("ID,Name,Phone,Email");
            writer.newLine();

            // Write data
            for (Supplier supplier : suppliers) {
                String line = String.join(CSV_SEPARATOR,
                        String.valueOf(supplier.getId()),
                        escapeCSV(supplier.getName()),
                        escapeCSV(supplier.getPhone()),
                        escapeCSV(supplier.getEmail()));
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Export statistics report to text file.
     */
    public void exportStatisticsReport(String filePath,
            double totalRevenue,
            double todayRevenue,
            long totalFlowers,
            long totalSales) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("=== Florist Management System - Statistics Report ===");
            writer.newLine();
            writer.write("Generated on: " + LocalDate.now().format(DATE_FORMATTER));
            writer.newLine();
            writer.newLine();

            writer.write("Total Revenue: " + String.format("%.2f MAD", totalRevenue));
            writer.newLine();
            writer.write("Today's Revenue: " + String.format("%.2f MAD", todayRevenue));
            writer.newLine();
            writer.write("Total Flowers in Stock: " + totalFlowers);
            writer.newLine();
            writer.write("Total Sales: " + totalSales);
            writer.newLine();
        }
    }

    /**
     * Escape CSV special characters.
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
