package com.florist.controller;

import com.florist.dao.StockAlertDao;
import com.florist.model.StockAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Alert View.
 * Displays and manages stock alerts.
 */
public class AlertViewController {

    @FXML private TableView<StockAlert> alertTable;
    @FXML private TableColumn<StockAlert, Integer> idColumn;
    @FXML private TableColumn<StockAlert, String> flowerNameColumn;
    @FXML private TableColumn<StockAlert, String> alertTypeColumn;
    @FXML private TableColumn<StockAlert, String> messageColumn;
    @FXML private TableColumn<StockAlert, LocalDate> generatedDateColumn;
    @FXML private TableColumn<StockAlert, Boolean> resolvedColumn;
    @FXML private Label alertCountLabel;

    private StockAlertDao alertDao;
    private ObservableList<StockAlert> alertList;

    @FXML
    public void initialize() {
        alertDao = new StockAlertDao();
        alertList = FXCollections.observableArrayList();

        // Setup table columns
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
                } else {
                    setText(item ? "✓ Résolu" : "⚠ Actif");
                    setStyle(item ? "-fx-text-fill: green;" : "-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });

        // Load data
        loadAlerts();
    }

    /**
     * Loads unresolved alerts from database.
     */
    private void loadAlerts() {
        List<StockAlert> alerts = alertDao.findUnresolved();
        alertList.clear();
        alertList.addAll(alerts);
        alertTable.setItems(alertList);
        
        alertCountLabel.setText(alerts.size() + " alerte(s) active(s)");
    }

    @FXML
    private void handleResolve() {
        StockAlert alert = alertTable.getSelectionModel().getSelectedItem();
        if (alert == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", 
                "Veuillez sélectionner une alerte à résoudre.");
            return;
        }

        if (alert.isResolved()) {
            showAlert(Alert.AlertType.INFORMATION, "Information", 
                "Cette alerte est déjà résolue.");
            return;
        }

        if (alertDao.markResolved(alert.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                "Alerte marquée comme résolue!");
            loadAlerts();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                "Échec de la résolution de l'alerte.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadAlerts();
        showAlert(Alert.AlertType.INFORMATION, "Actualisation", "Alertes actualisées!");
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
            Stage stage = (Stage) alertTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Menu Principal - Florist Management");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
