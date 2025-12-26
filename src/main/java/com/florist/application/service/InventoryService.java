package com.florist.application.service;

import com.florist.domain.repository.FlowerRepository;
import com.florist.domain.repository.StockAlertRepository;
import com.florist.model.Flower;
import com.florist.model.StockAlert;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing inventory and generating stock alerts.
 * REFACTORED: Now uses Repositories.
 */
public class InventoryService {

    private final FlowerRepository flowerRepository;
    private final StockAlertRepository alertRepository;

    public static final int DEFAULT_LOW_STOCK_THRESHOLD = 2;

    public InventoryService(FlowerRepository flowerRepository, StockAlertRepository alertRepository) {
        this.flowerRepository = flowerRepository;
        this.alertRepository = alertRepository;
    }

    public int checkAllAlerts() {
        List<Flower> flowers = flowerRepository.findAll();
        for (Flower flower : flowers) {
            recalculateAlerts(flower, DEFAULT_LOW_STOCK_THRESHOLD);
        }
        return alertRepository.countUnresolved();
    }

    public void recalculateAlerts(Flower flower, int threshold) {
        // 1. Clear existing unresolved alerts for this flower
        alertRepository.deleteUnresolved(flower.getId(), "LOW_STOCK");
        alertRepository.deleteUnresolved(flower.getId(), "EXPIRY");

        // 2. Re-check Low Stock condition
        if (flower.getQuantity() < threshold) {
            generateLowStockAlert(flower);
        }

        // 3. Re-check Freshness condition
        int percentage = FreshnessService.calculateFreshnessPercentage(flower.getArrivalDate(),
                flower.getFreshnessDays());
        if (percentage < 40) {
            generateExpiryAlert(flower, percentage);
        }
    }

    private void generateLowStockAlert(Flower flower) {
        String severity = (flower.getQuantity() == 0) ? "DANGER" : "WARNING";
        String message = String.format("Low stock for '%s': only %d units remaining",
                flower.getName(), flower.getQuantity());

        StockAlert alert = new StockAlert();
        alert.setFlowerId(flower.getId());
        alert.setAlertType("LOW_STOCK");
        alert.setSeverity(severity);
        alert.setMessage(message);
        alert.setGeneratedDate(LocalDate.now());
        alert.setResolved(false);

        alertRepository.save(alert);
    }

    private void generateExpiryAlert(Flower flower, int percentage) {
        String severity = (percentage == 0) ? "DANGER" : "WARNING";
        String label = (percentage == 0) ? "EXPIRED" : "LOW FRESHNESS";
        String message = String.format("[%s] '%s' is at %d%% freshness",
                label, flower.getName(), percentage);

        StockAlert alert = new StockAlert();
        alert.setFlowerId(flower.getId());
        alert.setAlertType("EXPIRY");
        alert.setSeverity(severity);
        alert.setMessage(message);
        alert.setGeneratedDate(LocalDate.now());
        alert.setResolved(false);

        alertRepository.save(alert);
    }

    public void checkAndAlertAfterSale(Flower flower, int threshold) {
        recalculateAlerts(flower, threshold);
    }
}
