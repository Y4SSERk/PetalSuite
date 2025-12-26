package com.florist.application.service;

import com.florist.domain.repository.FlowerRepository;
import com.florist.domain.repository.SaleRepository;
import com.florist.model.Flower;
import com.florist.model.Sale;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for statistical calculations using Stream API.
 * REFACTORED: Now uses Repositories.
 */
public class StatisticsService {

    private final FlowerRepository flowerRepository;
    private final SaleRepository saleRepository;

    public StatisticsService(FlowerRepository flowerRepository, SaleRepository saleRepository) {
        this.flowerRepository = flowerRepository;
        this.saleRepository = saleRepository;
    }

    public double calculateTotalRevenue() {
        return saleRepository.findAll().stream()
                .mapToDouble(Sale::getTotalPrice)
                .sum();
    }

    public double calculateTodayRevenue() {
        LocalDate today = LocalDate.now();
        return saleRepository.findAll().stream()
                .filter(sale -> sale.getSaleDate().equals(today))
                .mapToDouble(Sale::getTotalPrice)
                .sum();
    }

    public double calculateAverageSaleAmount() {
        return saleRepository.findAll().stream()
                .mapToDouble(Sale::getTotalPrice)
                .average()
                .orElse(0.0);
    }

    public Map<String, Long> getFlowerCountByCategory() {
        return flowerRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Flower::getCategory,
                        Collectors.counting()));
    }

    public List<Flower> getTopExpensiveFlowers(int limit) {
        return flowerRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Flower::getPrice).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Flower> getLowStockFlowers(int threshold) {
        return flowerRepository.findAll().stream()
                .filter(flower -> flower.getQuantity() < threshold)
                .sorted(Comparator.comparingInt(Flower::getQuantity))
                .collect(Collectors.toList());
    }

    public double calculateTotalInventoryValue() {
        return flowerRepository.findAll().stream()
                .mapToDouble(flower -> flower.getPrice() * flower.getQuantity())
                .sum();
    }

    public List<Flower> getFlowersExpiringSoon(int days) {
        LocalDate targetDate = LocalDate.now().plusDays(days);
        return flowerRepository.findAll().stream()
                .filter(flower -> {
                    LocalDate expiryDate = flower.getExpiryDate();
                    return expiryDate != null &&
                            !expiryDate.isAfter(targetDate) &&
                            !expiryDate.isBefore(LocalDate.now());
                })
                .sorted(Comparator.comparing(Flower::getExpiryDate))
                .collect(Collectors.toList());
    }

    public Map<LocalDate, List<Sale>> getSalesGroupedByDate() {
        return saleRepository.findAll().stream()
                .collect(Collectors.groupingBy(Sale::getSaleDate));
    }

    public Map<String, Double> getAveragePriceByCategory() {
        return flowerRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Flower::getCategory,
                        Collectors.averagingDouble(Flower::getPrice)));
    }

    public long getTotalFlowerCount() {
        return flowerRepository.findAll().stream()
                .mapToInt(Flower::getQuantity)
                .sum();
    }

    public Map<Integer, Long> getMostSoldFlowers() {
        return saleRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Sale::getFlowerId,
                        Collectors.summingLong(Sale::getQuantitySold)));
    }
}
