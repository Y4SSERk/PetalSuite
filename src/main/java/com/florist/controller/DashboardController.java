package com.florist.controller;

import com.florist.dao.FlowerDao;
import com.florist.dao.SaleDao;
import com.florist.dao.StockAlertDao;
import com.florist.dao.SupplierDao;
import com.florist.model.Sale;
import com.florist.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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

    private FlowerDao flowerDao;
    private SupplierDao supplierDao;
    private SaleDao saleDao;
    private StockAlertDao alertDao;
    private InventoryService inventoryService;

    @FXML
    public void initialize() {
        flowerDao = new FlowerDao();
        supplierDao = new SupplierDao();
        saleDao = new SaleDao();
        alertDao = new StockAlertDao();
        inventoryService = new InventoryService();

        setupSalesTable();
        loadStatistics();
        loadSales();
    }

    /**
     * Sets up the sales table columns.
     */
    private void setupSalesTable() {
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        saleFlowerNameColumn.setCellValueFactory(new PropertyValueFactory<>("flowerName"));
        saleFlowerCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("flowerCategory"));
        saleQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        salePriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        saleCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));

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

    /**
     * Loads recent sales (last 10) into the table.
     */
    private void loadSales() {
        try {
            System.out.println("DEBUG: Loading sales data...");
            var sales = saleDao.findAll();
            System.out.println("DEBUG: Found " + sales.size() + " sales in database");

            // Limit to 10 most recent
            var recentSales = sales.size() > 10 ? sales.subList(0, 10) : sales;
            System.out.println("DEBUG: Displaying " + recentSales.size() + " sales");

            if (!recentSales.isEmpty()) {
                System.out.println("DEBUG: First sale - Flower: " + recentSales.get(0).getFlowerName() +
                        ", Category: " + recentSales.get(0).getFlowerCategory() +
                        ", Price: " + recentSales.get(0).getTotalPrice());
            }

            salesTableView.setItems(FXCollections.observableArrayList(recentSales));
            System.out.println("DEBUG: Sales loaded into table");
        } catch (Exception e) {
            System.err.println("ERROR loading sales: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads and displays all dashboard statistics.
     */
    private void loadStatistics() {
        // Count flowers
        int flowerCount = flowerDao.findAll().size();
        totalFlowersLabel.setText(String.valueOf(flowerCount));

        // Count suppliers
        int supplierCount = supplierDao.findAll().size();
        totalSuppliersLabel.setText(String.valueOf(supplierCount));

        // Count today's sales
        int todaySalesCount = saleDao.countTodaySales();
        todaySalesLabel.setText(String.valueOf(todaySalesCount));

        // Count active alerts
        int activeAlerts = alertDao.countUnresolved();
        activeAlertsLabel.setText(String.valueOf(activeAlerts));
    }

    @FXML
    private void handleCheckExpiration() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Vérification d'Expiration");
        confirm.setHeaderText("Vérifier les expirations?");
        confirm.setContentText("Cela va scanner toutes les fleurs et générer des alertes pour celles qui ont expiré.");

        if (confirm.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            int alertsGenerated = inventoryService.checkAllAlerts();

            showAlert(Alert.AlertType.INFORMATION, "Vérification Terminée",
                    String.format("Vérification terminée!\n%d nouvelle(s) alerte(s) générée(s).", alertsGenerated));

            loadStatistics(); // Refresh statistics
        }
    }

    @FXML
    private void handleRefresh() {
        loadStatistics();
        loadSales();
        showAlert(Alert.AlertType.INFORMATION, "Actualisation", "Statistiques actualisées!");
    }

    @FXML
    private void handleBack() {
        loadMainView();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            Stage stage = (Stage) totalFlowersLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Menu Principal - Florist Management");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
