package com.florist.controller;

import com.florist.dao.SupplierDao;
import com.florist.model.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller for the Supplier Management view.
 * Handles CRUD operations for suppliers.
 */
public class SupplierManagementController {

    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, Integer> idColumn;
    @FXML private TableColumn<Supplier, String> nameColumn;
    @FXML private TableColumn<Supplier, String> phoneColumn;
    @FXML private TableColumn<Supplier, String> emailColumn;

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    private SupplierDao supplierDao;
    private ObservableList<Supplier> supplierList;
    private Supplier selectedSupplier;

    @FXML
    public void initialize() {
        supplierDao = new SupplierDao();
        supplierList = FXCollections.observableArrayList();

        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Load data
        loadSuppliers();

        // Add selection listener
        supplierTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedSupplier = newSelection;
                    populateForm(newSelection);
                }
            }
        );
    }

    /**
     * Loads suppliers from database into table.
     */
    private void loadSuppliers() {
        List<Supplier> suppliers = supplierDao.findAll();
        supplierList.clear();
        supplierList.addAll(suppliers);
        supplierTable.setItems(supplierList);
    }

    /**
     * Populates form with selected supplier data.
     */
    private void populateForm(Supplier supplier) {
        nameField.setText(supplier.getName());
        phoneField.setText(supplier.getPhone());
        emailField.setText(supplier.getEmail());
    }

    @FXML
    private void handleAdd() {
        try {
            Supplier supplier = createSupplierFromForm();
            int id = supplierDao.insert(supplier);
            
            if (id > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur ajouté avec succès!");
                loadSuppliers();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout du fournisseur.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedSupplier == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un fournisseur à modifier.");
            return;
        }

        try {
            Supplier supplier = createSupplierFromForm();
            supplier.setId(selectedSupplier.getId());
            
            if (supplierDao.update(supplier)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur modifié avec succès!");
                loadSuppliers();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la modification.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Supplier supplier = supplierTable.getSelectionModel().getSelectedItem();
        if (supplier == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un fournisseur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le fournisseur?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer '" + supplier.getName() + "'?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (supplierDao.delete(supplier.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur supprimé avec succès!");
                loadSuppliers();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression.");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadSuppliers();
        handleClear();
        showAlert(Alert.AlertType.INFORMATION, "Actualisation", "Données actualisées!");
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        supplierTable.getSelectionModel().clearSelection();
        selectedSupplier = null;
    }

    @FXML
    private void handleBack() {
        loadMainView();
    }

    /**
     * Creates a Supplier object from the form fields.
     */
    private Supplier createSupplierFromForm() {
        if (nameField.getText().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire!");
        }

        Supplier supplier = new Supplier();
        supplier.setName(nameField.getText());
        supplier.setPhone(phoneField.getText());
        supplier.setEmail(emailField.getText());

        return supplier;
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
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Menu Principal - Florist Management");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
