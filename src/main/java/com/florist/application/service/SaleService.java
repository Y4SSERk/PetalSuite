package com.florist.application.service;

import com.florist.application.validation.SaleValidator;
import com.florist.application.validation.ValidationResult;
import com.florist.domain.repository.FlowerRepository;
import com.florist.domain.repository.SaleRepository;
import com.florist.model.Flower;
import com.florist.model.Sale;

import java.time.LocalDate;
import java.util.List;

/**
 * Application service for Sale operations.
 * Handles sale processing with stock management.
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final FlowerRepository flowerRepository;
    private final SaleValidator validator;
    private final InventoryService inventoryService;

    public SaleService(SaleRepository saleRepo, FlowerRepository flowerRepo,
            SaleValidator validator, InventoryService inventoryService) {
        this.saleRepository = saleRepo;
        this.flowerRepository = flowerRepo;
        this.validator = validator;
        this.inventoryService = inventoryService;
    }

    /**
     * Processes a sale transaction.
     * Validates, updates stock, and creates sale record.
     * 
     * @param sale the sale to process
     * @return the created sale
     * @throws IllegalArgumentException if validation fails or insufficient stock
     */
    public Sale processSale(Sale sale) {
        // Validate sale
        ValidationResult result = validator.validate(sale);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorMessage());
        }

        // Get flower and check stock
        Flower flower = flowerRepository.findById(sale.getFlowerId())
                .orElseThrow(() -> new IllegalArgumentException("Flower not found"));

        if (flower.getQuantity() < sale.getQuantitySold()) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock. Available: %d, Requested: %d",
                            flower.getQuantity(), sale.getQuantitySold()));
        }

        // Update stock
        int newQuantity = flower.getQuantity() - sale.getQuantitySold();
        boolean stockUpdated = flowerRepository.updateStock(flower.getId(), newQuantity);

        if (!stockUpdated) {
            throw new RuntimeException("Failed to update stock");
        }

        // Create sale record
        Sale savedSale = saleRepository.save(sale);

        // Check for low stock alerts
        flower.setQuantity(newQuantity);
        inventoryService.checkAndAlertAfterSale(flower, InventoryService.DEFAULT_LOW_STOCK_THRESHOLD);

        return savedSale;
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public List<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        return saleRepository.findByDateRange(startDate, endDate);
    }

    public Sale getSaleById(int id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found with ID: " + id));
    }

    public int getTodaySalesCount() {
        return saleRepository.countTodaySales();
    }
}
