package com.florist.model;

import java.time.LocalDate;

/**
 * Flower entity representing a flower in the inventory.
 * Contains information about the flower including stock, pricing, and freshness.
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
    
    // For display purposes when joining with supplier
    private String supplierName;

    // Constructors
    public Flower() {}

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

    /**
     * Calculates the expiry date based on arrival date and freshness days.
     * @return the date when the flower will expire
     */
    public LocalDate getExpiryDate() {
        if (arrivalDate == null) {
            return null;
        }
        return arrivalDate.plusDays(freshnessDays);
    }

    /**
     * Checks if the flower stock is below the specified threshold.
     * @param threshold the minimum stock level
     * @return true if stock is low, false otherwise
     */
    public boolean isLowStock(int threshold) {
        return quantity < threshold;
    }

    /**
     * Reduces the stock by the specified amount.
     * @param amount the amount to reduce
     * @throws IllegalArgumentException if amount is negative or exceeds current stock
     */
    public void reduceStock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + quantity);
        }
        this.quantity -= amount;
    }

    /**
     * Checks if the flower has expired.
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        LocalDate expiryDate = getExpiryDate();
        return expiryDate != null && !expiryDate.isAfter(LocalDate.now());
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

    @Override
    public String toString() {
        return name + " (" + color + ")";
    }
}
