package com.florist.application.service;

import com.florist.application.validation.SupplierValidator;
import com.florist.application.validation.ValidationResult;
import com.florist.domain.repository.SupplierRepository;
import com.florist.model.Supplier;

import java.util.List;

/**
 * Application service for Supplier operations.
 */
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierValidator validator;

    public SupplierService(SupplierRepository repository, SupplierValidator validator) {
        this.supplierRepository = repository;
        this.validator = validator;
    }

    public Supplier createSupplier(Supplier supplier) {
        ValidationResult result = validator.validate(supplier);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorMessage());
        }

        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Supplier supplier) {
        ValidationResult result = validator.validate(supplier);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorMessage());
        }

        return supplierRepository.save(supplier);
    }

    public boolean deleteSupplier(int id) {
        return supplierRepository.delete(id);
    }

    public int deleteSuppliers(List<Integer> ids) {
        int deleted = 0;
        for (int id : ids) {
            if (supplierRepository.delete(id)) {
                deleted++;
            }
        }
        return deleted;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(int id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + id));
    }
}
