package com.florist.controller;

import com.florist.application.service.FlowerService;
import com.florist.application.service.SupplierService;
import com.florist.config.ServiceFactory;
import com.florist.model.Flower;
import com.florist.model.Supplier;
import com.florist.application.service.FreshnessService;
import com.florist.util.NotificationService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Flower Management view.
 * REFACTORED: Now uses FlowerService for business logic.
 * Responsibilities: UI handling only.
 */
public class FlowerManagementController {

    // FXML Components
    @FXML
    private TableView<Flower> flowerTable;
    @FXML
    private TableColumn<Flower, Integer> idColumn;
    @FXML
    private TableColumn<Flower, String> nameColumn;
    @FXML
    private TableColumn<Flower, String> colorColumn;
    @FXML
    private TableColumn<Flower, String> categoryColumn;
    @FXML
    private TableColumn<Flower, Double> priceColumn;
    @FXML
    private TableColumn<Flower, Integer> quantityColumn;
    @FXML
    private TableColumn<Flower, LocalDate> arrivalDateColumn;
    @FXML
    private TableColumn<Flower, String> freshnessDaysColumn;
    @FXML
    private TableColumn<Flower, String> supplierColumn;

    @FXML
    private ComboBox<String> nameComboBox;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField priceField;
    @FXML
    private TextField quantityField;
    @FXML
    private DatePicker arrivalDatePicker;
    @FXML
    private TextField freshnessDaysField;
    @FXML
    private ComboBox<Supplier> supplierComboBox;

    @FXML
    private TableColumn<Flower, Boolean> selectColumn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button selectAllBtn;

    // Services (injected via ServiceFactory)
    private FlowerService flowerService;
    private SupplierService supplierService;

    // State
    private ObservableList<Flower> flowerList;
    private Flower selectedFlower;
    private boolean isDeleteMode = false;

    @FXML
    public void initialize() {
        // Initialize services
        ServiceFactory factory = ServiceFactory.getInstance();
        this.flowerService = factory.getFlowerService();
        this.supplierService = factory.getSupplierService();

        this.flowerList = FXCollections.observableArrayList();

        setupTable();
        setupFormControls();
        loadData();
    }

    private void setupTable() {
        // Setup select column for delete mode
        selectColumn = new TableColumn<>("");
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(javafx.scene.control.cell.CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setPrefWidth(40);
        selectColumn.setVisible(false);
        flowerTable.getColumns().add(0, selectColumn);
        flowerTable.setEditable(true);

        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        arrivalDateColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        freshnessDaysColumn.setCellValueFactory(new PropertyValueFactory<>("freshnessLabel"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        // Format price column
        priceColumn.setCellFactory(column -> new TableCell<Flower, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : String.format("%.2f MAD", price));
            }
        });

        // Color column with visual indicator
        colorColumn.setCellFactory(column -> new TableCell<Flower, String>() {
            @Override
            protected void updateItem(String colorStr, boolean empty) {
                super.updateItem(colorStr, empty);
                if (empty || colorStr == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(colorStr);
                    try {
                        Color color = Color.web(colorStr);
                        javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(8);
                        circle.setFill(color);
                        setGraphic(circle);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        // Row factory for alerts (red/yellow highlighting)
        flowerTable.setRowFactory(tv -> new TableRow<Flower>() {
            @Override
            protected void updateItem(Flower flower, boolean empty) {
                super.updateItem(flower, empty);
                getStyleClass().removeAll("row-alert-danger", "row-alert-warning");

                if (flower == null || empty)
                    return;

                int freshness = FreshnessService.calculateFreshnessPercentage(
                        flower.getArrivalDate(), flower.getFreshnessDays());

                if (flower.getQuantity() == 0 || freshness == 0) {
                    getStyleClass().add("row-alert-danger");
                } else if (flower.getQuantity() == 1 || freshness < 25) {
                    getStyleClass().add("row-alert-warning");
                }
            }
        });

        // Selection listener
        flowerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedFlower = newSelection;
                        populateForm(newSelection);
                    }
                });
    }

    private void setupFormControls() {
        nameComboBox.setItems(FXCollections.observableArrayList(
                "Rose", "Tulip", "Lily", "Orchid", "Sunflower",
                "Daisy", "Peony", "Carnation", "Lavender", "Hydrangea"));

        categoryComboBox.setItems(FXCollections.observableArrayList(
                "Cut Flower", "Potted Plant", "Arrangement", "Dried", "Exotic"));

        supplierComboBox.setConverter(new javafx.util.StringConverter<Supplier>() {
            @Override
            public String toString(Supplier supplier) {
                return supplier == null ? "" : supplier.getName();
            }

            @Override
            public Supplier fromString(String string) {
                return null;
            }
        });
    }

    private void loadData() {
        loadFlowers();
        loadSuppliers();
    }

    private void loadFlowers() {
        try {
            List<Flower> flowers = flowerService.getAllFlowersWithSuppliers();
            flowerList.clear();
            flowerList.addAll(flowers);
            flowerTable.setItems(flowerList);
        } catch (Exception e) {
            NotificationService.showError(flowerTable.getScene().getWindow(),
                    "Failed to load flowers: " + e.getMessage());
        }
    }

    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            supplierComboBox.setItems(FXCollections.observableArrayList(suppliers));
        } catch (Exception e) {
            NotificationService.showError(flowerTable.getScene().getWindow(),
                    "Failed to load suppliers: " + e.getMessage());
        }
    }

    private void populateForm(Flower flower) {
        nameComboBox.setValue(flower.getName());
        colorPicker.setValue(Color.web(flower.getColor()));
        categoryComboBox.setValue(flower.getCategory());
        priceField.setText(String.valueOf(flower.getPrice()));
        quantityField.setText(String.valueOf(flower.getQuantity()));
        arrivalDatePicker.setValue(flower.getArrivalDate());
        freshnessDaysField.setText(String.valueOf(flower.getFreshnessDays()));

        Supplier supplier = supplierComboBox.getItems().stream()
                .filter(s -> s.getId() == flower.getSupplierId())
                .findFirst()
                .orElse(null);
        supplierComboBox.setValue(supplier);
    }

    @FXML
    private void handleAdd() {
        try {
            Flower flower = createFlowerFromForm();
            flowerService.createFlower(flower);

            NotificationService.showSuccess(flowerTable.getScene().getWindow(),
                    "Flower added successfully!");
            loadFlowers();
            clearForm();

        } catch (IllegalArgumentException e) {
            NotificationService.showError(flowerTable.getScene().getWindow(), e.getMessage());
        } catch (Exception e) {
            NotificationService.showError(flowerTable.getScene().getWindow(),
                    "Failed to add flower: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedFlower == null) {
            NotificationService.showWarning(flowerTable.getScene().getWindow(),
                    "Please select a flower to update");
            return;
        }

        try {
            Flower flower = createFlowerFromForm();
            flower.setId(selectedFlower.getId());

            flowerService.updateFlower(flower);

            NotificationService.showSuccess(flowerTable.getScene().getWindow(),
                    "Flower updated successfully!");
            loadFlowers();
            clearForm();

        } catch (IllegalArgumentException e) {
            NotificationService.showError(flowerTable.getScene().getWindow(), e.getMessage());
        } catch (Exception e) {
            NotificationService.showError(flowerTable.getScene().getWindow(),
                    "Failed to update flower: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (!isDeleteMode) {
            // Enter delete mode
            isDeleteMode = true;
            selectColumn.setVisible(true);
            deleteBtn.setText("Delete Selected");
            selectAllBtn.setVisible(true);
            return;
        }

        // Delete selected flowers
        List<Integer> idsToDelete = flowerList.stream()
                .filter(Flower::isSelected)
                .map(Flower::getId)
                .collect(Collectors.toList());

        if (idsToDelete.isEmpty()) {
            NotificationService.showWarning(flowerTable.getScene().getWindow(),
                    "No flowers selected for deletion");
            return;
        }

        try {
            int deleted = flowerService.deleteFlowers(idsToDelete);

            NotificationService.showSuccess(flowerTable.getScene().getWindow(),
                    deleted + " flower(s) deleted successfully!");
            exitDeleteMode();
            loadFlowers();

        } catch (Exception e) {
            NotificationService.showError(flowerTable.getScene().getWindow(),
                    "Failed to delete flowers: " + e.getMessage());
        }
    }

    @FXML
    private void handleSelectAll() {
        boolean allSelected = flowerList.stream().allMatch(Flower::isSelected);
        flowerList.forEach(f -> f.setSelected(!allSelected));
        flowerTable.refresh();
    }

    private void exitDeleteMode() {
        isDeleteMode = false;
        selectColumn.setVisible(false);
        deleteBtn.setText("Delete");
        selectAllBtn.setVisible(false);
        flowerList.forEach(f -> f.setSelected(false));
        flowerTable.refresh();
    }

    @FXML
    private void handleRefresh() {
        loadFlowers();
        NotificationService.showInfo(flowerTable.getScene().getWindow(),
                "Flower list refreshed!");
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private Flower createFlowerFromForm() {
        Flower flower = new Flower();
        flower.setName(nameComboBox.getValue());
        flower.setColor(String.format("#%02X%02X%02X",
                (int) (colorPicker.getValue().getRed() * 255),
                (int) (colorPicker.getValue().getGreen() * 255),
                (int) (colorPicker.getValue().getBlue() * 255)));
        flower.setCategory(categoryComboBox.getValue());
        flower.setPrice(Double.parseDouble(priceField.getText()));
        flower.setQuantity(Integer.parseInt(quantityField.getText()));
        flower.setArrivalDate(arrivalDatePicker.getValue());
        flower.setFreshnessDays(Integer.parseInt(freshnessDaysField.getText()));
        flower.setSupplierId(supplierComboBox.getValue().getId());
        return flower;
    }

    private void clearForm() {
        nameComboBox.setValue(null);
        colorPicker.setValue(Color.WHITE);
        categoryComboBox.setValue(null);
        priceField.clear();
        quantityField.clear();
        arrivalDatePicker.setValue(LocalDate.now());
        freshnessDaysField.clear();
        supplierComboBox.setValue(null);
        selectedFlower = null;
    }
}
