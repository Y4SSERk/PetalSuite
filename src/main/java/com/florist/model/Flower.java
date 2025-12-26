package com.florist.model;

import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.florist.application.service.FreshnessService;

/**
 * Flower entity representing a flower in the inventory.
 * Contains details such as name, color, quantity, price, and calculated
 * freshness.
 */
public class Flower {
    private int id;
    private String name;
    private String color;
    private String category;
    private double price;
    private int quantity;
    private LocalDate arrivalDate;
    private int freshnessDays;
    private int supplierId;

    // UI State
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    // For display purposes when joining with supplier
    private String supplierName;

    public Flower() {
    }

    public Flower(int id, String name, String color, String category, double price,
            int quantity, LocalDate arrivalDate, int freshnessDays, int supplierId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.arrivalDate = arrivalDate;
        this.freshnessDays = freshnessDays;
        this.supplierId = supplierId;
    }

    // UI Selection Property
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    /**
     * Calculates the expiry date based on arrival date and freshness days.
     * 
     * @return the date when the flower will expire
     */
    public LocalDate getExpiryDate() {
        if (arrivalDate == null) {
            return null;
        }
        return arrivalDate.plusDays(freshnessDays);
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public int getFreshnessDays() {
        return freshnessDays;
    }

    public void setFreshnessDays(int freshnessDays) {
        this.freshnessDays = freshnessDays;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getFreshnessLabel() {
        if (arrivalDate != null) {
            // Use FreshnessService to get the label
            return FreshnessService.getFreshnessLabel(arrivalDate, freshnessDays);
        }
        return "Unknown";
    }

    // Helper to get raw percentage for progress bars if needed
    public double getFreshnessPercentage() {
        if (arrivalDate != null) {
            return FreshnessService.calculateFreshnessPercentage(arrivalDate, freshnessDays);
        }
        return 0.0;
    }

    public void reduceStock(int amount) {
        if (amount > 0 && this.quantity >= amount) {
            this.quantity -= amount;
        }
    }

    public boolean isLowStock(int threshold) {
        return this.quantity < threshold;
    }

    @Override
    public String toString() {
        return name + " (" + color + ")";
    }
}
