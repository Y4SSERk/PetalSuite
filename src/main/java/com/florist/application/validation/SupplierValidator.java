package com.florist.application.validation;

import com.florist.model.Supplier;

/**
 * Validator for Supplier entities.
 */
public class SupplierValidator {

    public ValidationResult validate(Supplier supplier) {
        ValidationResult result = new ValidationResult();

        if (supplier == null) {
            result.addError("supplier", "Supplier cannot be null");
            return result;
        }

        // Name validation
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            result.addError("name", "Supplier name is required");
        } else if (supplier.getName().length() > 100) {
            result.addError("name", "Supplier name must not exceed 100 characters");
        }

        // Phone validation (optional but format check if provided)
        if (supplier.getPhone() != null && !supplier.getPhone().trim().isEmpty()) {
            if (supplier.getPhone().length() > 20) {
                result.addError("phone", "Phone number must not exceed 20 characters");
            }
        }

        // Email validation (optional but format check if provided)
        if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
            if (!supplier.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                result.addError("email", "Invalid email format");
            }
        }

        return result;
    }
}
