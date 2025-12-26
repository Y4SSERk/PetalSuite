package com.florist.controller;

import com.florist.application.service.AlertService;
import com.florist.config.ServiceFactory;
import com.florist.model.StockAlert;
import com.florist.application.service.InventoryService;
import com.florist.util.NotificationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Alert View.
 * REFACTORED: Now uses AlertService.
 */
public class AlertViewController {

    @FXML
    private TableView<StockAlert> alertTable;
    @FXML
    private TableColumn<StockAlert, Integer> idColumn;
    @FXML
    private TableColumn<StockAlert, String> flowerNameColumn;
    @FXML
    private TableColumn<StockAlert, String> alertTypeColumn;
    @FXML
    private TableColumn<StockAlert, String> messageColumn;
    @FXML
    private TableColumn<StockAlert, LocalDate> generatedDateColumn;
    @FXML
    private TableColumn<StockAlert, Boolean> resolvedColumn;
    @FXML
    private Label alertCountLabel;

    private AlertService alertService;
    private InventoryService inventoryService;
    private ObservableList<StockAlert> alertList;

    @FXML
    public void initialize() {
        ServiceFactory factory = ServiceFactory.getInstance();
        this.alertService = factory.getAlertService();
        this.inventoryService = factory.getInventoryService();
        this.alertList = FXCollections.observableArrayList();

        setupTable();
        loadAlerts();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        flowerNameColumn.setCellValueFactory(new PropertyValueFactory<>("flowerName"));
        alertTypeColumn.setCellValueFactory(new PropertyValueFactory<>("alertType"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        generatedDateColumn.setCellValueFactory(new PropertyValueFactory<>("generatedDate"));

        // Custom cell factory for resolved column
        resolvedColumn.setCellValueFactory(new PropertyValueFactory<>("resolved"));
        resolvedColumn.setCellFactory(col -> new TableCell<StockAlert, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item ? "Resolved" : "Active");
                    if (item) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e63946; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // ROW COLORING based on severity
        alertTable.setRowFactory(tv -> new TableRow<StockAlert>() {
            @Override
            protected void updateItem(StockAlert alert, boolean empty) {
                super.updateItem(alert, empty);
                getStyleClass().removeAll("row-alert-danger", "row-alert-warning");

                if (alert == null || empty) {
                    return;
                }

                String severity = alert.getSeverity();
                if ("DANGER".equals(severity)) {
                    getStyleClass().add("row-alert-danger");
                } else if ("WARNING".equals(severity)) {
                    getStyleClass().add("row-alert-warning");
                }
            }
        });
    }

    private void loadAlerts() {
        List<StockAlert> alerts = alertService.getUnresolvedAlerts();
        alertList.clear();
        alertList.addAll(alerts);
        alertTable.setItems(alertList);

        alertCountLabel.setText(alerts.size() + " active notification(s)");
    }

    @FXML
    private void handleResolve() {
        StockAlert alert = alertTable.getSelectionModel().getSelectedItem();
        if (alert == null) {
            NotificationService.showWarning(alertTable.getScene().getWindow(), "Please select an alert to resolve.");
            return;
        }

        if (alert.isResolved()) {
            NotificationService.showInfo(alertTable.getScene().getWindow(), "This alert is already resolved.");
            return;
        }

        if (alertService.resolveAlert(alert.getId())) {
            NotificationService.showSuccess(alertTable.getScene().getWindow(), "Alert marked as resolved!");
            loadAlerts();
        } else {
            NotificationService.showError(alertTable.getScene().getWindow(), "Failed to resolve alert.");
        }
    }

    @FXML
    private void handleRefresh() {
        // Trigger re-calculation before loading
        inventoryService.checkAllAlerts();
        loadAlerts();
        NotificationService.showInfo(alertTable.getScene().getWindow(), "Alerts re-synchronized and refreshed!");
    }
}
