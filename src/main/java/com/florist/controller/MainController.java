package com.florist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the main menu view.
 * Handles navigation to different sections of the application.
 */
public class MainController {

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        statusLabel.setText("Bienvenue dans le système de gestion fleuriste");
    }

    @FXML
    private void openFlowerManagement() {
        loadView("/fxml/FlowerManagement.fxml", "Gestion des Fleurs");
    }

    @FXML
    private void openSupplierManagement() {
        loadView("/fxml/SupplierManagement.fxml", "Gestion des Fournisseurs");
    }

    @FXML
    private void openSaleForm() {
        loadView("/fxml/SaleForm.fxml", "Enregistrer une Vente");
    }

    @FXML
    private void openDashboard() {
        loadView("/fxml/Dashboard.fxml", "Tableau de Bord");
    }

    @FXML
    private void openAlerts() {
        loadView("/fxml/AlertView.fxml", "Alertes de Stock");
    }

    /**
     * Loads a new view in the current window.
     * @param fxmlPath path to the FXML file
     * @param title window title
     */
    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title + " - Florist Management");
        } catch (Exception e) {
            System.err.println("Error loading view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
