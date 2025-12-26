package com.florist.controller;

import com.florist.application.service.SupplierService;
import com.florist.config.ServiceFactory;
import com.florist.model.Supplier;
import com.florist.util.NotificationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Controller for the Supplier Management view.
 * REFACTORED: Now uses SupplierService.
 */
public class SupplierManagementController {

    @FXML
    private TableView<Supplier> supplierTable;
    @FXML
    private TableColumn<Supplier, Integer> idColumn;
    @FXML
    private TableColumn<Supplier, String> nameColumn;
    @FXML
    private TableColumn<Supplier, String> phoneColumn;
    @FXML
    private TableColumn<Supplier, String> emailColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;

    @FXML
    private Button addBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button deleteBtn;

    private SupplierService supplierService;
    private ObservableList<Supplier> supplierList;
    private Supplier selectedSupplier;

    @FXML
    public void initialize() {
        this.supplierService = ServiceFactory.getInstance().getSupplierService();
        this.supplierList = FXCollections.observableArrayList();

        setupTable();
        loadData();

        // Bind selection
        supplierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedSupplier = newVal;
                populateForm(newVal);
            }
        });
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void loadData() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        supplierList.clear();
        supplierList.addAll(suppliers);
        supplierTable.setItems(supplierList);
    }

    private void populateForm(Supplier supplier) {
        nameField.setText(supplier.getName());
        phoneField.setText(supplier.getPhone());
        emailField.setText(supplier.getEmail());
    }

    @FXML
    private void handleAdd() {
        try {
            Supplier supplier = new Supplier(0, nameField.getText(), phoneField.getText(), emailField.getText());
            supplierService.createSupplier(supplier);
            NotificationService.showSuccess(supplierTable.getScene().getWindow(), "Supplier added successfully!");
            loadData();
            handleClear();
        } catch (Exception e) {
            NotificationService.showError(supplierTable.getScene().getWindow(), "Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedSupplier == null) {
            NotificationService.showWarning(supplierTable.getScene().getWindow(),
                    "Please select a supplier to update.");
            return;
        }

        try {
            selectedSupplier.setName(nameField.getText());
            selectedSupplier.setPhone(phoneField.getText());
            selectedSupplier.setEmail(emailField.getText());

            supplierService.updateSupplier(selectedSupplier);
            NotificationService.showSuccess(supplierTable.getScene().getWindow(), "Supplier updated successfully!");
            loadData();
            supplierTable.refresh();
        } catch (Exception e) {
            NotificationService.showError(supplierTable.getScene().getWindow(), "Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            NotificationService.showWarning(supplierTable.getScene().getWindow(),
                    "Please select a supplier to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Supplier");
        alert.setHeaderText("Delete '" + selected.getName() + "'?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (supplierService.deleteSupplier(selected.getId())) {
                NotificationService.showSuccess(supplierTable.getScene().getWindow(), "Supplier deleted!");
                loadData();
                handleClear();
            } else {
                NotificationService.showError(supplierTable.getScene().getWindow(), "Failed to delete supplier.");
            }
        }
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        selectedSupplier = null;
        supplierTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        loadData();
        NotificationService.showInfo(supplierTable.getScene().getWindow(), "Data refreshed!");
    }

    @FXML
    private void handleSelectAll() {
        // Not implemented in simple mode
    }
}
