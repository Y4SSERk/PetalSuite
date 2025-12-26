package com.florist.controller;

import com.florist.application.service.FlowerService;
import com.florist.application.service.SaleService;
import com.florist.config.ServiceFactory;
import com.florist.model.Flower;
import com.florist.model.Sale;
import com.florist.util.NotificationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Sale Form view.
 * REFACTORED: Now uses SaleService and FlowerService.
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
    @FXML
    private Button saveSaleBtn;
    @FXML
    private Button clearBtn;

    private FlowerService flowerService;
    private SaleService saleService;

    @FXML
    public void initialize() {
        ServiceFactory factory = ServiceFactory.getInstance();
        this.flowerService = factory.getFlowerService();
        this.saleService = factory.getSaleService();

        loadFlowers();

        // Listeners for auto-calculation
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
    }

    private void loadFlowers() {
        List<Flower> flowers = flowerService.getAllFlowers();
        flowerComboBox.setItems(FXCollections.observableArrayList(flowers));
    }

    @FXML
    private void handleFlowerSelection() {
        Flower selected = flowerComboBox.getValue();
        if (selected != null) {
            unitPriceLabel.setText(String.format("%.2f MAD", selected.getPrice()));
            availableStockLabel.setText(String.valueOf(selected.getQuantity()));
            calculateTotal();
        }
    }

    @FXML
    private void handleQuantityChange() {
        calculateTotal();
    }

    private void calculateTotal() {
        try {
            Flower flower = flowerComboBox.getValue();
            String qtyStr = quantityField.getText();

            if (flower != null && qtyStr != null && !qtyStr.isEmpty()) {
                int qty = Integer.parseInt(qtyStr);
                double total = flower.getPrice() * qty;
                totalPriceLabel.setText(String.format("%.2f MAD", total));
            } else {
                totalPriceLabel.setText("0.00 MAD");
            }
        } catch (NumberFormatException e) {
            totalPriceLabel.setText("0.00 MAD");
        }
    }

    @FXML
    private void handleSaveSale() {
        Flower selectedFlower = flowerComboBox.getValue();
        String qtyStr = quantityField.getText();

        if (selectedFlower == null || qtyStr == null || qtyStr.isEmpty()) {
            NotificationService.showWarning(saveSaleBtn.getScene().getWindow(), "Please fill in all required fields.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);

            Sale sale = new Sale();
            sale.setFlowerId(selectedFlower.getId());
            sale.setFlowerName(selectedFlower.getName());
            sale.setFlowerCategory(selectedFlower.getCategory());
            sale.setQuantitySold(qty);
            sale.setUnitPrice(selectedFlower.getPrice());
            sale.setTotalPrice(selectedFlower.getPrice() * qty);
            sale.setCustomerName(customerNameField.getText());
            sale.setSaleDate(LocalDate.now());

            saleService.processSale(sale);

            // Fetch refreshed flower data for the message
            Flower updatedFlower = flowerService.getFlowerById(selectedFlower.getId());
            int remaining = updatedFlower.getQuantity();

            String successMsg = "Sale completed successfully!";
            if (remaining == 0) {
                successMsg += " STATUS: OUT OF STOCK";
            } else if (remaining == 1) {
                successMsg += " STATUS: LOW STOCK (1 unit left)";
            }

            NotificationService.showSuccess(saveSaleBtn.getScene().getWindow(), successMsg);

            handleClear();
            loadFlowers(); // Refresh list to update stock counts in dropdown

        } catch (NumberFormatException e) {
            NotificationService.showError(saveSaleBtn.getScene().getWindow(), "Invalid quantity format.");
        } catch (Exception e) {
            NotificationService.showError(saveSaleBtn.getScene().getWindow(), e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        flowerComboBox.getSelectionModel().clearSelection();
        unitPriceLabel.setText("0.00 MAD");
        availableStockLabel.setText("0");
        quantityField.clear();
        customerNameField.clear();
        totalPriceLabel.setText("0.00 MAD");
    }
}
