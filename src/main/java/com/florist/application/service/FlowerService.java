package com.florist.application.service;

import com.florist.application.validation.FlowerValidator;
import com.florist.application.validation.ValidationResult;
import com.florist.domain.repository.FlowerRepository;
import com.florist.model.Flower;

import java.util.List;

/**
 * Application service for Flower operations.
 * Orchestrates business logic for flower management.
 */
public class FlowerService {

    private final FlowerRepository flowerRepository;
    private final FlowerValidator validator;
    private final InventoryService inventoryService;

    public FlowerService(FlowerRepository repository, FlowerValidator validator, InventoryService inventoryService) {
        this.flowerRepository = repository;
        this.validator = validator;
        this.inventoryService = inventoryService;
    }

    /**
     * Creates a new flower.
     * 
     * @param flower the flower to create
     * @return the created flower
     * @throws IllegalArgumentException if validation fails
     */
    public Flower createFlower(Flower flower) {
        // Validate
        ValidationResult result = validator.validate(flower);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorMessage());
        }

        // Save
        Flower saved = flowerRepository.save(flower);

        // Check for alerts after creation
        inventoryService.checkAndAlertAfterSale(saved, InventoryService.DEFAULT_LOW_STOCK_THRESHOLD);

        return saved;
    }

    /**
     * Updates an existing flower.
     * 
     * @param flower the flower to update
     * @return the updated flower
     * @throws IllegalArgumentException if validation fails
     */
    public Flower updateFlower(Flower flower) {
        // Validate
        ValidationResult result = validator.validate(flower);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorMessage());
        }

        // Update
        Flower updated = flowerRepository.save(flower);

        // Check for alerts after update
        inventoryService.checkAndAlertAfterSale(updated, InventoryService.DEFAULT_LOW_STOCK_THRESHOLD);

        return updated;
    }

    /**
     * Deletes a flower by ID.
     * 
     * @param id the flower ID
     * @return true if deleted successfully
     */
    public boolean deleteFlower(int id) {
        return flowerRepository.delete(id);
    }

    /**
     * Deletes multiple flowers.
     * 
     * @param ids list of flower IDs to delete
     * @return number of flowers deleted
     */
    public int deleteFlowers(List<Integer> ids) {
        int deleted = 0;
        for (int id : ids) {
            if (flowerRepository.delete(id)) {
                deleted++;
            }
        }
        return deleted;
    }

    /**
     * Retrieves all flowers.
     * 
     * @return list of all flowers
     */
    public List<Flower> getAllFlowers() {
        return flowerRepository.findAll();
    }

    /**
     * Retrieves all flowers with supplier information.
     * 
     * @return list of flowers with suppliers
     */
    public List<Flower> getAllFlowersWithSuppliers() {
        return flowerRepository.findAllWithSuppliers();
    }

    /**
     * Finds a flower by ID.
     * 
     * @param id the flower ID
     * @return the flower if found
     * @throws IllegalArgumentException if flower not found
     */
    public Flower getFlowerById(int id) {
        return flowerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flower not found with ID: " + id));
    }

    /**
     * Updates stock for a flower.
     * 
     * @param id       the flower ID
     * @param quantity the new quantity
     * @return true if updated successfully
     */
    public boolean updateStock(int id, int quantity) {
        boolean updated = flowerRepository.updateStock(id, quantity);

        if (updated) {
            // Check for alerts after stock update
            Flower flower = getFlowerById(id);
            inventoryService.checkAndAlertAfterSale(flower, InventoryService.DEFAULT_LOW_STOCK_THRESHOLD);
        }

        return updated;
    }
}
