package com.florist.application.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a validation operation.
 */
public class ValidationResult {

    private final Map<String, List<String>> errors;

    public ValidationResult() {
        this.errors = new HashMap<>();
    }

    /**
     * Adds a validation error for a specific field.
     * 
     * @param field   the field name
     * @param message the error message
     */
    public void addError(String field, String message) {
        errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
    }

    /**
     * Checks if validation passed (no errors).
     * 
     * @return true if valid
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Gets all validation errors.
     * 
     * @return map of field names to error messages
     */
    public Map<String, List<String>> getErrors() {
        return new HashMap<>(errors);
    }

    /**
     * Gets all error messages as a single formatted string.
     * 
     * @return formatted error message
     */
    public String getErrorMessage() {
        if (isValid()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        errors.forEach((field, messages) -> {
            messages.forEach(msg -> {
                if (sb.length() > 0)
                    sb.append("\n");
                sb.append(msg);
            });
        });
        return sb.toString();
    }
}
