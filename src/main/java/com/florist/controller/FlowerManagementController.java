package com.florist.controller;

import com.florist.dao.FlowerDao;
import com.florist.dao.SupplierDao;
import com.florist.model.Flower;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Flower Management view.
 * Handles CRUD operations for flowers.
 */
public class FlowerManagementController {

    @FXML private TableView<Flower> flowerTable;
    @FXML private TableColumn<Flower, Integer> idColumn;
    @FXML private TableColumn<Flower, String> nameColumn;
    @FXML private TableColumn<Flower, String> colorColumn;
    @FXML private TableColumn<Flower, String> categoryColumn;
    @FXML private TableColumn<Flower, Double> priceColumn;
    @FXML private TableColumn<Flower, Integer> quantityColumn;
    @FXML private TableColumn<Flower, LocalDate> arrivalDateColumn;
    @FXML private TableColumn<Flower, Integer> freshnessDaysColumn;
    @FXML private TableColumn<Flower, String> supplierColumn;

    @FXML private TextField nameField;
    @FXML private TextField colorField;
    @FXML private TextField categoryField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private DatePicker arrivalDatePicker;
    @FXML private TextField freshnessDaysField;
    @FXML private ComboBox<Supplier> supplierComboBox;

    private FlowerDao flowerDao;
    private SupplierDao supplierDao;
    private ObservableList<Flower> flowerList;
    private Flower selectedFlower;

    @FXML
    public void initialize() {
        flowerDao = new FlowerDao();
        supplierDao = new SupplierDao();
        flowerList = FXCollections.observableArrayList();

        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        arrivalDateColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        freshnessDaysColumn.setCellValueFactory(new PropertyValueFactory<>("freshnessDays"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        // Load data
        loadFlowers();
        loadSuppliers();

        // Add selection listener
        flowerTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedFlower = newSelection;
                    populateForm(newSelection);
                }
            }
        );
    }

    /**
     * Loads flowers from database into table.
     */
    private void loadFlowers() {
        List<Flower> flowers = flowerDao.findAllWithSupplierNames();
        flowerList.clear();
        flowerList.addAll(flowers);
        flowerTable.setItems(flowerList);
    }

    /**
     * Loads suppliers into ComboBox.
     */
    private void loadSuppliers() {
        List<Supplier> suppliers = supplierDao.findAll();
        supplierComboBox.setItems(FXCollections.observableArrayList(suppliers));
    }

    /**
     * Populates form with selected flower data.
     */
    private void populateForm(Flower flower) {
        nameField.setText(flower.getName());
        colorField.setText(flower.getColor());
        categoryField.setText(flower.getCategory());
        priceField.setText(String.valueOf(flower.getPrice()));
        quantityField.setText(String.valueOf(flower.getQuantity()));
        arrivalDatePicker.setValue(flower.getArrivalDate());
        freshnessDaysField.setText(String.valueOf(flower.getFreshnessDays()));

        // Select supplier in ComboBox
        for (Supplier supplier : supplierComboBox.getItems()) {
            if (supplier.getId() == flower.getSupplierId()) {
                supplierComboBox.setValue(supplier);
                break;
            }
        }
    }

    @FXML
    private void handleAdd() {
        try {
            Flower flower = createFlowerFromForm();
            int id = flowerDao.insert(flower);
            
            if (id > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Fleur ajoutée avec succès!");
                loadFlowers();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout de la fleur.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedFlower == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une fleur à modifier.");
            return;
        }

        try {
            Flower flower = createFlowerFromForm();
            flower.setId(selectedFlower.getId());
            
            if (flowerDao.update(flower)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Fleur modifiée avec succès!");
                loadFlowers();
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
        Flower flower = flowerTable.getSelectionModel().getSelectedItem();
        if (flower == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une fleur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la fleur?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer '" + flower.getName() + "'?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (flowerDao.delete(flower.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Fleur supprimée avec succès!");
                loadFlowers();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression.");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadFlowers();
        loadSuppliers();
        handleClear();
        showAlert(Alert.AlertType.INFORMATION, "Actualisation", "Données actualisées!");
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        colorField.clear();
        categoryField.clear();
        priceField.clear();
        quantityField.clear();
        arrivalDatePicker.setValue(LocalDate.now());
        freshnessDaysField.clear();
        supplierComboBox.setValue(null);
        flowerTable.getSelectionModel().clearSelection();
        selectedFlower = null;
    }

    @FXML
    private void handleBack() {
        loadMainView();
    }

    /**
     * Creates a Flower object from the form fields.
     */
    private Flower createFlowerFromForm() {
        if (nameField.getText().isEmpty() || priceField.getText().isEmpty() ||
            quantityField.getText().isEmpty() || freshnessDaysField.getText().isEmpty() ||
            arrivalDatePicker.getValue() == null || supplierComboBox.getValue() == null) {
            throw new IllegalArgumentException("Tous les champs sont obligatoires!");
        }

        Flower flower = new Flower();
        flower.setName(nameField.getText());
        flower.setColor(colorField.getText());
        flower.setCategory(categoryField.getText());
        flower.setPrice(Double.parseDouble(priceField.getText()));
        flower.setQuantity(Integer.parseInt(quantityField.getText()));
        flower.setArrivalDate(arrivalDatePicker.getValue());
        flower.setFreshnessDays(Integer.parseInt(freshnessDaysField.getText()));
        flower.setSupplierId(supplierComboBox.getValue().getId());

        return flower;
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
