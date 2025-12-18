package com.florist.dao;

import com.florist.model.Flower;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Flower entity.
 * Handles all database operations for flowers.
 */
public class FlowerDao {

    /**
     * Retrieves all flowers from the database.
     * @return list of all flowers
     */
    public List<Flower> findAll() {
        List<Flower> flowers = new ArrayList<>();
        String sql = "SELECT * FROM flowers ORDER BY id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                flowers.add(extractFlowerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving flowers: " + e.getMessage());
        }
        
        return flowers;
    }

    /**
     * Retrieves all flowers with supplier names (JOIN query).
     * @return list of flowers with supplier information
     */
    public List<Flower> findAllWithSupplierNames() {
        List<Flower> flowers = new ArrayList<>();
        String sql = "SELECT f.*, s.name AS supplier_name " +
                     "FROM flowers f " +
                     "LEFT JOIN suppliers s ON f.supplier_id = s.id " +
                     "ORDER BY f.id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Flower flower = extractFlowerFromResultSet(rs);
                flower.setSupplierName(rs.getString("supplier_name"));
                flowers.add(flower);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving flowers with suppliers: " + e.getMessage());
        }
        
        return flowers;
    }

    /**
     * Finds a flower by ID.
     * @param id the flower ID
     * @return the flower, or null if not found
     */
    public Flower findById(int id) {
        String sql = "SELECT * FROM flowers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractFlowerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding flower by ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Inserts a new flower into the database.
     * @param flower the flower to insert
     * @return the generated ID, or -1 if failed
     */
    public int insert(Flower flower) {
        String sql = "INSERT INTO flowers (name, color, category, price, quantity, " +
                     "arrival_date, freshness_days, supplier_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, flower.getName());
            pstmt.setString(2, flower.getColor());
            pstmt.setString(3, flower.getCategory());
            pstmt.setDouble(4, flower.getPrice());
            pstmt.setInt(5, flower.getQuantity());
            pstmt.setDate(6, Date.valueOf(flower.getArrivalDate()));
            pstmt.setInt(7, flower.getFreshnessDays());
            pstmt.setInt(8, flower.getSupplierId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting flower: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Updates an existing flower.
     * @param flower the flower to update
     * @return true if successful, false otherwise
     */
    public boolean update(Flower flower) {
        String sql = "UPDATE flowers SET name = ?, color = ?, category = ?, price = ?, " +
                     "quantity = ?, arrival_date = ?, freshness_days = ?, supplier_id = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, flower.getName());
            pstmt.setString(2, flower.getColor());
            pstmt.setString(3, flower.getCategory());
            pstmt.setDouble(4, flower.getPrice());
            pstmt.setInt(5, flower.getQuantity());
            pstmt.setDate(6, Date.valueOf(flower.getArrivalDate()));
            pstmt.setInt(7, flower.getFreshnessDays());
            pstmt.setInt(8, flower.getSupplierId());
            pstmt.setInt(9, flower.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating flower: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a flower by ID.
     * @param id the flower ID
     * @return true if successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM flowers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting flower: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the stock quantity for a flower.
     * @param id the flower ID
     * @param newQuantity the new quantity
     * @return true if successful, false otherwise
     */
    public boolean updateStock(int id, int newQuantity) {
        String sql = "UPDATE flowers SET quantity = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts a Flower object from a ResultSet.
     * @param rs the ResultSet
     * @return the Flower object
     * @throws SQLException if extraction fails
     */
    private Flower extractFlowerFromResultSet(ResultSet rs) throws SQLException {
        Flower flower = new Flower();
        flower.setId(rs.getInt("id"));
        flower.setName(rs.getString("name"));
        flower.setColor(rs.getString("color"));
        flower.setCategory(rs.getString("category"));
        flower.setPrice(rs.getDouble("price"));
        flower.setQuantity(rs.getInt("quantity"));
        
        Date arrivalDate = rs.getDate("arrival_date");
        if (arrivalDate != null) {
            flower.setArrivalDate(arrivalDate.toLocalDate());
        }
        
        flower.setFreshnessDays(rs.getInt("freshness_days"));
        flower.setSupplierId(rs.getInt("supplier_id"));
        
        return flower;
    }
}
