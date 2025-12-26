package com.florist.application.service;

import com.florist.domain.repository.StockAlertRepository;
import com.florist.model.StockAlert;

import java.util.List;

/**
 * Application service for Alert operations.
 */
public class AlertService {

    private final StockAlertRepository alertRepository;

    public AlertService(StockAlertRepository repository) {
        this.alertRepository = repository;
    }

    public List<StockAlert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public List<StockAlert> getUnresolvedAlerts() {
        return alertRepository.findUnresolved();
    }

    public boolean resolveAlert(int id) {
        return alertRepository.markResolved(id);
    }

    public int getUnresolvedCount() {
        return alertRepository.countUnresolved();
    }

    public StockAlert getAlertById(int id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found with ID: " + id));
    }
}
