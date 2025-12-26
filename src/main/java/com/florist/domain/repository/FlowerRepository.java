package com.florist.domain.repository;

import com.florist.model.Flower;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Flower entity.
 * Defines contract for data access operations.
 */
public interface FlowerRepository {

    /**
     * Retrieves all flowers.
     * 
     * @return list of all flowers
     */
    List<Flower> findAll();

    /**
     * Retrieves all flowers with supplier information.
     * 
     * @return list of flowers with supplier names
     */
    List<Flower> findAllWithSuppliers();

    /**
     * Finds a flower by ID.
     * 
     * @param id the flower ID
     * @return Optional containing the flower if found
     */
    Optional<Flower> findById(int id);

    /**
     * Saves a new flower or updates existing one.
     * 
     * @param flower the flower to save
     * @return the saved flower with generated ID
     */
    Flower save(Flower flower);

    /**
     * Deletes a flower by ID.
     * 
     * @param id the flower ID
     * @return true if deleted successfully
     */
    boolean delete(int id);

    /**
     * Updates stock quantity for a flower.
     * 
     * @param id       the flower ID
     * @param quantity the new quantity
     * @return true if updated successfully
     */
    boolean updateStock(int id, int quantity);
}
