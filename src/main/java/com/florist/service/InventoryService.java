package com.florist.service;

import com.florist.dao.FlowerDao;
import com.florist.dao.StockAlertDao;
import com.florist.model.Flower;
import com.florist.model.StockAlert;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing inventory and generating stock alerts.
 * Handles business logic for stock monitoring and expiration tracking.
 */
public class InventoryService {
    
    private final FlowerDao flowerDao;
    private final StockAlertDao alertDao;
    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 20;

    public InventoryService() {
        this.flowerDao = new FlowerDao();
        this.alertDao = new StockAlertDao();
    }

    /**
     * Checks all flowers for expiration and generates alerts.
     * @return number of new expiry alerts generated
     */
    public int checkExpirations() {
        List<Flower> flowers = flowerDao.findAll();
        int alertsGenerated = 0;
        LocalDate today = LocalDate.now();
        
        System.out.println("Checking expiration for " + flowers.size() + " flowers...");
        
        for (Flower flower : flowers) {
            LocalDate expiryDate = flower.getExpiryDate();
            
            if (expiryDate != null && !expiryDate.isAfter(today)) {
                // Flower has expired or expires today
                String message = String.format(
                    "La fleur '%s' a expiré le %s (arrivée: %s, fraîcheur: %d jours)",
                    flower.getName(),
                    expiryDate,
                    flower.getArrivalDate(),
                    flower.getFreshnessDays()
                );
                
                StockAlert alert = new StockAlert();
                alert.setFlowerId(flower.getId());
                alert.setAlertType("EXPIRY");
                alert.setMessage(message);
                alert.setGeneratedDate(today);
                alert.setResolved(false);
                
                int alertId = alertDao.insert(alert);
                if (alertId > 0) {
                    alertsGenerated++;
                    System.out.println("✓ Alerte d'expiration créée pour: " + flower.getName());
                }
            }
        }
        
        System.out.println("Vérification d'expiration terminée. " + alertsGenerated + " nouvelles alertes générées.");
        return alertsGenerated;
    }

    /**
     * Checks all flowers for low stock and generates alerts.
     * @param threshold the stock threshold (default 20)
     * @return number of new low stock alerts generated
     */
    public int checkLowStock(int threshold) {
        List<Flower> flowers = flowerDao.findAll();
        int alertsGenerated = 0;
        LocalDate today = LocalDate.now();
        
        System.out.println("Checking low stock for " + flowers.size() + " flowers (threshold: " + threshold + ")...");
        
        for (Flower flower : flowers) {
            if (flower.isLowStock(threshold)) {
                String message = String.format(
                    "Stock faible pour '%s': seulement %d unités restantes (seuil: %d)",
                    flower.getName(),
                    flower.getQuantity(),
                    threshold
                );
                
                StockAlert alert = new StockAlert();
                alert.setFlowerId(flower.getId());
                alert.setAlertType("LOW_STOCK");
                alert.setMessage(message);
                alert.setGeneratedDate(today);
                alert.setResolved(false);
                
                int alertId = alertDao.insert(alert);
                if (alertId > 0) {
                    alertsGenerated++;
                    System.out.println("✓ Alerte de stock faible créée pour: " + flower.getName());
                }
            }
        }
        
        System.out.println("Vérification de stock terminée. " + alertsGenerated + " nouvelles alertes générées.");
        return alertsGenerated;
    }

    /**
     * Checks both expiration and low stock with default threshold.
     * @return total number of alerts generated
     */
    public int checkAllAlerts() {
        int expiryAlerts = checkExpirations();
        int stockAlerts = checkLowStock(DEFAULT_LOW_STOCK_THRESHOLD);
        return expiryAlerts + stockAlerts;
    }

    /**
     * Generates a low stock alert for a specific flower after a sale.
     * @param flower the flower to check
     * @param threshold the stock threshold
     */
    public void checkAndAlertAfterSale(Flower flower, int threshold) {
        if (flower.isLowStock(threshold)) {
            String message = String.format(
                "Stock faible pour '%s' après vente: seulement %d unités restantes (seuil: %d)",
                flower.getName(),
                flower.getQuantity(),
                threshold
            );
            
            StockAlert alert = new StockAlert();
            alert.setFlowerId(flower.getId());
            alert.setAlertType("LOW_STOCK");
            alert.setMessage(message);
            alert.setGeneratedDate(LocalDate.now());
            alert.setResolved(false);
            
            int alertId = alertDao.insert(alert);
            if (alertId > 0) {
                System.out.println("✓ Alerte de stock faible créée après vente: " + flower.getName());
            }
        }
    }
}
