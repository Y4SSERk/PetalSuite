package com.florist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button dashboardBtn;
    @FXML
    private Button inventoryBtn;
    @FXML
    private Button salesBtn;
    @FXML
    private Button suppliersBtn;
    @FXML
    private Button alertsBtn;

    private Button activeBtn;

    @FXML
    public void initialize() {
        activeBtn = dashboardBtn;
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadView("/fxml/Dashboard.fxml", dashboardBtn);
    }

    @FXML
    private void showInventory() {
        loadView("/fxml/FlowerManagement.fxml", inventoryBtn);
    }

    @FXML
    private void showSales() {
        loadView("/fxml/SaleForm.fxml", salesBtn);
    }

    @FXML
    private void showSuppliers() {
        loadView("/fxml/SupplierManagement.fxml", suppliersBtn);
    }

    @FXML
    private void showAlerts() {
        loadView("/fxml/AlertView.fxml", alertsBtn);
    }

    private void loadView(String fxmlPath, Button triggerBtn) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);

            // Update UI state
            if (activeBtn != null) {
                activeBtn.getStyleClass().remove("nav-button-active");
            }
            triggerBtn.getStyleClass().add("nav-button-active");
            activeBtn = triggerBtn;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Logic for logout if needed, for now just exit or show info
        System.out.println("Logout requested");
    }
}
