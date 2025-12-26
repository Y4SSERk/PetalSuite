package com.florist.application.validation;

import com.florist.model.Sale;

/**
 * Validator for Sale entities.
 */
public class SaleValidator {

    public ValidationResult validate(Sale sale) {
        ValidationResult result = new ValidationResult();

        if (sale == null) {
            result.addError("sale", "Sale cannot be null");
            return result;
        }

        // Sale date validation
        if (sale.getSaleDate() == null) {
            result.addError("saleDate", "Sale date is required");
        }

        // Flower ID validation
        if (sale.getFlowerId() <= 0) {
            result.addError("flower", "Valid flower must be selected");
        }

        // Quantity validation
        if (sale.getQuantitySold() <= 0) {
            result.addError("quantity", "Quantity sold must be greater than zero");
        } else if (sale.getQuantitySold() > 10000) {
            result.addError("quantity", "Quantity sold is unreasonably high");
        }

        // Price validation
        if (sale.getTotalPrice() <= 0) {
            result.addError("totalPrice", "Total price must be greater than zero");
        }

        // Customer name validation (optional)
        if (sale.getCustomerName() != null && sale.getCustomerName().length() > 100) {
            result.addError("customerName", "Customer name must not exceed 100 characters");
        }

        return result;
    }
}
