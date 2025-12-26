package com.florist.test;

import com.florist.config.ServiceFactory;
import com.florist.domain.repository.SaleRepository;
import com.florist.model.Sale;
import java.util.List;

public class TestSales {
    public static void main(String[] args) {
        System.out.println("Starting Sales Diagnostic...");
        SaleRepository saleRepository = ServiceFactory.getInstance().getSaleRepository();

        try {
            System.out.println("Attempting to fetch all sales...");
            List<Sale> sales = saleRepository.findAll();
            System.out.println("Found " + sales.size() + " sales.");

            for (Sale s : sales) {
                System.out.println("Sale ID: " + s.getId() +
                        ", Date: " + s.getSaleDate() +
                        ", Flower: " + s.getFlowerName() +
                        ", Category: " + s.getFlowerCategory() +
                        ", Amount: " + s.getTotalPrice());
            }

            if (sales.isEmpty()) {
                System.out.println("No sales found.");
                int count = saleRepository.countTodaySales();
                System.out.println("Today's sales count: " + count);
            }

        } catch (Exception e) {
            System.err.println("Exception during test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
