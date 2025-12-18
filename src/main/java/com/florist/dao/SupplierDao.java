package com.florist.dao;

import com.florist.model.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Supplier entity.
 * Handles all database operations for suppliers.
 */
public class SupplierDao {

    /**
     * Retrieves all suppliers from the database.
     * @return list of all suppliers
     */
    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(extractSupplierFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving suppliers: " + e.getMessage());
        }
        
        return suppliers;
    }

    /**
     * Finds a supplier by ID.
     * @param id the supplier ID
     * @return the supplier, or null if not found
     */
    public Supplier findById(int id) {
        String sql = "SELECT * FROM suppliers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractSupplierFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding supplier by ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Inserts a new supplier into the database.
     * @param supplier the supplier to insert
     * @return the generated ID, or -1 if failed
     */
    public int insert(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name, phone, email) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getPhone());
            pstmt.setString(3, supplier.getEmail());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting supplier: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Updates an existing supplier.
     * @param supplier the supplier to update
     * @return true if successful, false otherwise
     */
    public boolean update(Supplier supplier) {
        String sql = "UPDATE suppliers SET name = ?, phone = ?, email = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getPhone());
            pstmt.setString(3, supplier.getEmail());
            pstmt.setInt(4, supplier.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a supplier by ID.
     * @param id the supplier ID
     * @return true if successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM suppliers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts a Supplier object from a ResultSet.
     * @param rs the ResultSet
     * @return the Supplier object
     * @throws SQLException if extraction fails
     */
    private Supplier extractSupplierFromResultSet(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setId(rs.getInt("id"));
        supplier.setName(rs.getString("name"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setEmail(rs.getString("email"));
        return supplier;
    }
}
