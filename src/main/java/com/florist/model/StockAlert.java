package com.florist.model;

import java.time.LocalDate;

/**
 * StockAlert entity representing alerts for low stock or expiring flowers.
 * Helps track inventory issues that need attention.
 */
public class StockAlert {
    private int id;
    private int flowerId;
    private String alertType;  // "LOW_STOCK" or "EXPIRY"
    private String message;
    private LocalDate generatedDate;
    private boolean resolved;
    
    // For display purposes
    private String flowerName;

    // Constructors
    public StockAlert() {}

    public StockAlert(int id, int flowerId, String alertType, String message, 
                      LocalDate generatedDate, boolean resolved) {
        this.id = id;
        this.flowerId = flowerId;
        this.alertType = alertType;
        this.message = message;
        this.generatedDate = generatedDate;
        this.resolved = resolved;
    }

    /**
     * Marks the alert as resolved.
     */
    public void resolveAlert() {
        this.resolved = true;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlowerId() {
        return flowerId;
    }

    public void setFlowerId(int flowerId) {
        this.flowerId = flowerId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    @Override
    public String toString() {
        String status = resolved ? "[RÉSOLU]" : "[ACTIF]";
        return status + " " + alertType + ": " + message;
    }
}
