package com.florist.model;

import java.time.LocalDate;

/**
 * Sale entity representing a flower sale transaction.
 * Records the sale details including customer, quantity, and price.
 */
public class Sale {
    private int id;
    private LocalDate saleDate;
    private int flowerId;
    private int quantitySold;
    private double unitPrice;
    private double totalPrice;
    private String customerName;

    // For display purposes
    private String flowerName;
    private String flowerCategory;

    // Constructors
    public Sale() {
    }

    public Sale(int id, LocalDate saleDate, int flowerId, int quantitySold,
            double totalPrice, String customerName) {
        this.id = id;
        this.saleDate = saleDate;
        this.flowerId = flowerId;
        this.quantitySold = quantitySold;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
    }

    /**
     * Calculates the total price based on unit price and quantity.
     */
    public void calculateTotal() {
        this.totalPrice = unitPrice * quantitySold;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public int getFlowerId() {
        return flowerId;
    }

    public void setFlowerId(int flowerId) {
        this.flowerId = flowerId;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public String getFlowerCategory() {
        return flowerCategory;
    }

    public void setFlowerCategory(String flowerCategory) {
        this.flowerCategory = flowerCategory;
    }

    @Override
    public String toString() {
        return "Sale #" + id + " - " + flowerName + " x" + quantitySold;
    }
}
