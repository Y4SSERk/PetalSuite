package com.florist.infrastructure.persistence;

import com.florist.domain.repository.SaleRepository;
import com.florist.model.Sale;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository implementation for Sale entity.
 * Directly handles SQL operations for efficiency.
 */
public class SaleRepositoryImpl implements SaleRepository {

    @Override
    public List<Sale> findAll() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT s.*, f.name AS flower_name, f.category AS flower_category " +
                "FROM sales s " +
                "LEFT JOIN flowers f ON s.flower_id = f.id " +
                "ORDER BY s.sale_date DESC, s.id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Sale sale = extractSaleFromResultSet(rs);
                sale.setFlowerName(rs.getString("flower_name"));
                sale.setFlowerCategory(rs.getString("flower_category"));
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    @Override
    public List<Sale> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return findAll().stream()
                .filter(sale -> !sale.getSaleDate().isBefore(startDate) && !sale.getSaleDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Sale> findById(int id) {
        return findAll().stream()
                .filter(sale -> sale.getId() == id)
                .findFirst();
    }

    @Override
    public Sale save(Sale sale) {
        String sql = "INSERT INTO sales (sale_date, flower_id, quantity_sold, total_price, customer_name) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, Date.valueOf(sale.getSaleDate()));
            pstmt.setInt(2, sale.getFlowerId());
            pstmt.setInt(3, sale.getQuantitySold());
            pstmt.setDouble(4, sale.getTotalPrice());
            pstmt.setString(5, sale.getCustomerName());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        sale.setId(rs.getInt(1));
                        return sale;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failed to insert sale");
    }

    @Override
    public boolean delete(int id) {
        throw new UnsupportedOperationException("Sales cannot be deleted");
    }

    @Override
    public int countTodaySales() {
        String sql = "SELECT COUNT(*) FROM sales WHERE sale_date = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Sale extractSaleFromResultSet(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setId(rs.getInt("id"));
        Date saleDate = rs.getDate("sale_date");
        if (saleDate != null) {
            sale.setSaleDate(saleDate.toLocalDate());
        }
        sale.setFlowerId(rs.getInt("flower_id"));
        sale.setQuantitySold(rs.getInt("quantity_sold"));
        sale.setTotalPrice(rs.getDouble("total_price"));
        sale.setCustomerName(rs.getString("customer_name"));
        return sale;
    }
}
