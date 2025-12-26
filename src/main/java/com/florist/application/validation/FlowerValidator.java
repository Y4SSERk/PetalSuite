package com.florist.application.validation;

import com.florist.model.Flower;

/**
 * Validator for Flower entities.
 * Centralizes all flower validation rules.
 */
public class FlowerValidator {

    /**
     * Validates a flower entity.
     * 
     * @param flower the flower to validate
     * @return validation result
     */
    public ValidationResult validate(Flower flower) {
        ValidationResult result = new ValidationResult();

        if (flower == null) {
            result.addError("flower", "Flower cannot be null");
            return result;
        }

        // Name validation
        if (flower.getName() == null || flower.getName().trim().isEmpty()) {
            result.addError("name", "Flower name is required");
        } else if (flower.getName().length() > 100) {
            result.addError("name", "Flower name must not exceed 100 characters");
        }

        // Color validation
        if (flower.getColor() == null || flower.getColor().trim().isEmpty()) {
            result.addError("color", "Color is required");
        }

        // Category validation
        if (flower.getCategory() == null || flower.getCategory().trim().isEmpty()) {
            result.addError("category", "Category is required");
        }

        // Price validation
        if (flower.getPrice() <= 0) {
            result.addError("price", "Price must be greater than zero");
        } else if (flower.getPrice() > 1000000) {
            result.addError("price", "Price is unreasonably high");
        }

        // Quantity validation
        if (flower.getQuantity() < 0) {
            result.addError("quantity", "Quantity cannot be negative");
        } else if (flower.getQuantity() > 1000000) {
            result.addError("quantity", "Quantity is unreasonably high");
        }

        // Arrival date validation
        if (flower.getArrivalDate() == null) {
            result.addError("arrivalDate", "Arrival date is required");
        }

        // Freshness days validation
        if (flower.getFreshnessDays() <= 0) {
            result.addError("freshnessDays", "Freshness days must be greater than zero");
        } else if (flower.getFreshnessDays() > 365) {
            result.addError("freshnessDays", "Freshness days cannot exceed 365");
        }

        // Supplier validation
        if (flower.getSupplierId() <= 0) {
            result.addError("supplier", "Valid supplier must be selected");
        }

        return result;
    }
}
