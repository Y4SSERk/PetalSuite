package com.florist.config;

import com.florist.application.service.AlertService;
import com.florist.application.service.FlowerService;
import com.florist.application.service.SaleService;
import com.florist.application.service.SupplierService;
import com.florist.application.validation.FlowerValidator;
import com.florist.application.validation.SaleValidator;
import com.florist.application.validation.SupplierValidator;
import com.florist.domain.repository.FlowerRepository;
import com.florist.domain.repository.SaleRepository;
import com.florist.domain.repository.StockAlertRepository;
import com.florist.domain.repository.SupplierRepository;
import com.florist.infrastructure.persistence.FlowerRepositoryImpl;
import com.florist.infrastructure.persistence.SaleRepositoryImpl;
import com.florist.infrastructure.persistence.StockAlertRepositoryImpl;
import com.florist.infrastructure.persistence.SupplierRepositoryImpl;
import com.florist.application.service.InventoryService;
import com.florist.application.service.StatisticsService;

/**
 * Service Factory for dependency injection.
 * Centralizes object creation and wiring.
 */
public class ServiceFactory {

    private static ServiceFactory instance;

    // Repositories
    private final FlowerRepository flowerRepository;
    private final SupplierRepository supplierRepository;
    private final SaleRepository saleRepository;
    private final StockAlertRepository alertRepository;

    // Validators
    private final FlowerValidator flowerValidator;
    private final SupplierValidator supplierValidator;
    private final SaleValidator saleValidator;

    // Domain Services
    private final InventoryService inventoryService;
    private final StatisticsService statisticsService;

    // Application Services
    private final FlowerService flowerService;
    private final SupplierService supplierService;
    private final SaleService saleService;
    private final AlertService alertService;

    private ServiceFactory() {
        // Initialize repositories
        this.flowerRepository = new FlowerRepositoryImpl();
        this.supplierRepository = new SupplierRepositoryImpl();
        this.saleRepository = new SaleRepositoryImpl();
        this.alertRepository = new StockAlertRepositoryImpl();

        // Initialize validators
        this.flowerValidator = new FlowerValidator();
        this.supplierValidator = new SupplierValidator();
        this.saleValidator = new SaleValidator();

        // Initialize domain services
        this.inventoryService = new InventoryService(flowerRepository, alertRepository);
        this.statisticsService = new StatisticsService(flowerRepository, saleRepository);

        // Initialize application services
        this.flowerService = new FlowerService(flowerRepository, flowerValidator, inventoryService);
        this.supplierService = new SupplierService(supplierRepository, supplierValidator);
        this.saleService = new SaleService(saleRepository, flowerRepository, saleValidator, inventoryService);
        this.alertService = new AlertService(alertRepository);
    }

    /**
     * Gets the singleton instance.
     * 
     * @return the service factory instance
     */
    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    // Getters for application services

    public FlowerService getFlowerService() {
        return flowerService;
    }

    public SupplierService getSupplierService() {
        return supplierService;
    }

    public SaleService getSaleService() {
        return saleService;
    }

    public AlertService getAlertService() {
        return alertService;
    }

    // Getters for domain services (for backward compatibility)

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public StatisticsService getStatisticsService() {
        return statisticsService;
    }

    // Getters for repositories (if needed for special cases)

    public FlowerRepository getFlowerRepository() {
        return flowerRepository;
    }

    public SupplierRepository getSupplierRepository() {
        return supplierRepository;
    }

    public SaleRepository getSaleRepository() {
        return saleRepository;
    }

    public StockAlertRepository getAlertRepository() {
        return alertRepository;
    }
}
