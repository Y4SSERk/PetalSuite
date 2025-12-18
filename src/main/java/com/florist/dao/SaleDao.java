package com.florist.dao;

import com.florist.model.Sale;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Sale entity.
 * Handles all database operations for sales.
 */
public class SaleDao {

    /**
     * Retrieves all sales from the database.
     * 
     * @return list of all sales
     */
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
            System.err.println("Error retrieving sales: " + e.getMessage());
        }

        return sales;
    }

    /**
     * Retrieves sales for today.
     * 
     * @return list of today's sales
     */
    public List<Sale> findTodaySales() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT s.*, f.name AS flower_name, f.category AS flower_category " +
                "FROM sales s " +
                "LEFT JOIN flowers f ON s.flower_id = f.id " +
                "WHERE s.sale_date = CURDATE() " +
                "ORDER BY s.id DESC";

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
            System.err.println("Error retrieving today's sales: " + e.getMessage());
        }

        return sales;
    }

    /**
     * Counts today's sales.
     * 
     * @return number of sales today
     */
    public int countTodaySales() {
        String sql = "SELECT COUNT(*) FROM sales WHERE sale_date = CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting today's sales: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Inserts a new sale into the database.
     * 
     * @param sale the sale to insert
     * @return the generated ID, or -1 if failed
     */
    public int insert(Sale sale) {
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
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting sale: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Extracts a Sale object from a ResultSet.
     * 
     * @param rs the ResultSet
     * @return the Sale object
     * @throws SQLException if extraction fails
     */
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
