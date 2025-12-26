package com.florist.controller;

import com.florist.application.service.AlertService;
import com.florist.application.service.FlowerService;
import com.florist.application.service.SaleService;
import com.florist.application.service.SupplierService;
import com.florist.config.ServiceFactory;
import com.florist.model.Sale;
import com.florist.application.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;

/**
 * Controller for the Dashboard view.
 * Displays summary statistics and provides quick actions.
 */
public class DashboardController {

    @FXML
    private Label totalFlowersLabel;
    @FXML
    private Label totalSuppliersLabel;
    @FXML
    private Label todaySalesLabel;
    @FXML
    private Label activeAlertsLabel;

    // Sales Table
    @FXML
    private TableView<Sale> salesTableView;
    @FXML
    private TableColumn<Sale, LocalDate> saleDateColumn;
    @FXML
    private TableColumn<Sale, String> saleFlowerNameColumn;
    @FXML
    private TableColumn<Sale, String> saleFlowerCategoryColumn;
    @FXML
    private TableColumn<Sale, Integer> saleQuantityColumn;
    @FXML
    private TableColumn<Sale, Double> salePriceColumn;
    @FXML
    private TableColumn<Sale, String> saleCustomerColumn;

    private FlowerService flowerService;
    private SupplierService supplierService;
    private SaleService saleService;
    private AlertService alertService;
    private InventoryService inventoryService;

    @FXML
    public void initialize() {
        ServiceFactory factory = ServiceFactory.getInstance();
        this.flowerService = factory.getFlowerService();
        this.supplierService = factory.getSupplierService();
        this.saleService = factory.getSaleService();
        this.alertService = factory.getAlertService();
        this.inventoryService = factory.getInventoryService();

        setupSalesTable();
        loadStatistics();
        loadSales();
    }

    private void setupSalesTable() {
        saleDateColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSaleDate()));

        saleFlowerNameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFlowerName()));

        saleFlowerCategoryColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFlowerCategory()));

        saleQuantityColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQuantitySold()));

        salePriceColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTotalPrice()));

        saleCustomerColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomerName()));

        // Format price column with MAD
        salePriceColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Sale, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f MAD", price));
                }
            }
        });
    }

    private void loadSales() {
        try {
            var sales = saleService.getAllSales();
            var recentSales = sales.size() > 10 ? sales.subList(sales.size() - 10, sales.size()) : sales;
            // Reverse order for recent sales
            java.util.Collections.reverse(recentSales);
            salesTableView.setItems(FXCollections.observableArrayList(recentSales));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStatistics() {
        int flowerCount = flowerService.getAllFlowers().size();
        totalFlowersLabel.setText(String.valueOf(flowerCount));

        int supplierCount = supplierService.getAllSuppliers().size();
        totalSuppliersLabel.setText(String.valueOf(supplierCount));

        int todaySalesCount = saleService.getTodaySalesCount();
        todaySalesLabel.setText(String.valueOf(todaySalesCount));

        int activeAlerts = alertService.getUnresolvedCount();
        activeAlertsLabel.setText(String.valueOf(activeAlerts));
    }

    @FXML
    private void handleCheckExpiration() {
        int alertsGenerated = inventoryService.checkAllAlerts();
        com.florist.util.NotificationService.showInfo(
                totalFlowersLabel.getScene().getWindow(),
                String.format("Check complete. %d new alert(s).", alertsGenerated));
        loadStatistics();
    }

    @FXML
    private void handleRefresh() {
        loadStatistics();
        loadSales();
        com.florist.util.NotificationService.showInfo(
                totalFlowersLabel.getScene().getWindow(), "Statistics refreshed!");
    }

    @FXML
    private void handleExport() {
        try {
            com.florist.io.FileExportService export = new com.florist.io.FileExportService();
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String dir = "exports";
            new java.io.File(dir).mkdirs();

            export.exportFlowersToCsv(flowerService.getAllFlowers(), dir + "/flowers_" + timestamp + ".csv");
            export.exportSalesToCsv(saleService.getAllSales(), dir + "/sales_" + timestamp + ".csv");
            export.exportSuppliersToCsv(supplierService.getAllSuppliers(), dir + "/suppliers_" + timestamp + ".csv");

            com.florist.util.NotificationService.showSuccess(
                    totalFlowersLabel.getScene().getWindow(),
                    "Data exported successfully to '" + dir + "' folder!");

        } catch (Exception e) {
            com.florist.util.NotificationService.showError(
                    totalFlowersLabel.getScene().getWindow(),
                    "Error during export: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
