package com.florist.controller;

import com.florist.dao.FlowerDao;
import com.florist.dao.SaleDao;
import com.florist.model.Flower;
import com.florist.model.Sale;
import com.florist.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Sale Form view.
 * Handles recording sales and updating stock.
 */
public class SaleFormController {

    @FXML
    private ComboBox<Flower> flowerComboBox;
    @FXML
    private Label unitPriceLabel;
    @FXML
    private Label availableStockLabel;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField customerNameField;
    @FXML
    private Label totalPriceLabel;

    private FlowerDao flowerDao;
    private SaleDao saleDao;
    private InventoryService inventoryService;
    private Flower selectedFlower;
    private static final int LOW_STOCK_THRESHOLD = 20;

    @FXML
    public void initialize() {
        flowerDao = new FlowerDao();
        saleDao = new SaleDao();
        inventoryService = new InventoryService();

        loadFlowers();
        updateTotalPrice();
    }

    /**
     * Loads flowers into ComboBox.
     */
    private void loadFlowers() {
        List<Flower> flowers = flowerDao.findAll();
        flowerComboBox.setItems(FXCollections.observableArrayList(flowers));
    }

    @FXML
    private void handleFlowerSelection() {
        selectedFlower = flowerComboBox.getValue();
        if (selectedFlower != null) {
            unitPriceLabel.setText(String.format("%.2f MAD", selectedFlower.getPrice()));
            availableStockLabel.setText(String.valueOf(selectedFlower.getQuantity()));
            updateTotalPrice();
        }
    }

    @FXML
    private void handleQuantityChange() {
        updateTotalPrice();
    }

    /**
     * Updates the total price based on quantity and unit price.
     */
    private void updateTotalPrice() {
        if (selectedFlower != null && !quantityField.getText().isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                double total = quantity * selectedFlower.getPrice();
                totalPriceLabel.setText(String.format("%.2f MAD", total));
            } catch (NumberFormatException e) {
                totalPriceLabel.setText("0.00 MAD");
            }
        } else {
            totalPriceLabel.setText("0.00 MAD");
        }
    }

    @FXML
    private void handleSaveSale() {
        if (selectedFlower == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une fleur.");
            return;
        }

        if (quantityField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez entrer une quantité.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity <= 0) {
                showAlert(Alert.AlertType.WARNING, "Attention", "La quantité doit être positive.");
                return;
            }

            if (quantity > selectedFlower.getQuantity()) {
                showAlert(Alert.AlertType.WARNING, "Stock insuffisant",
                        "Stock disponible: " + selectedFlower.getQuantity());
                return;
            }

            // Create sale
            Sale sale = new Sale();
            sale.setSaleDate(LocalDate.now());
            sale.setFlowerId(selectedFlower.getId());
            sale.setQuantitySold(quantity);
            sale.calculateTotal(selectedFlower.getPrice());
            sale.setCustomerName(customerNameField.getText());

            // Save sale
            int saleId = saleDao.insert(sale);

            if (saleId > 0) {
                // Update stock
                selectedFlower.reduceStock(quantity);
                flowerDao.updateStock(selectedFlower.getId(), selectedFlower.getQuantity());

                // Check for low stock and create alert if needed
                inventoryService.checkAndAlertAfterSale(selectedFlower, LOW_STOCK_THRESHOLD);

                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        String.format("Vente enregistrée!\nTotal: %.2f MAD\nStock restant: %d",
                                sale.getTotalPrice(), selectedFlower.getQuantity()));

                handleClear();
                loadFlowers(); // Refresh flower list
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'enregistrement de la vente.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Quantité invalide.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        flowerComboBox.setValue(null);
        quantityField.clear();
        customerNameField.clear();
        unitPriceLabel.setText("0.00 MAD");
        availableStockLabel.setText("0");
        totalPriceLabel.setText("0.00 MAD");
        selectedFlower = null;
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
            Stage stage = (Stage) quantityField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Menu Principal - Florist Management");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
