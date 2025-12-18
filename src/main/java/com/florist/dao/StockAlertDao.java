package com.florist.dao;

import com.florist.model.StockAlert;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for StockAlert entity.
 * Handles all database operations for stock alerts.
 */
public class StockAlertDao {

    /**
     * Retrieves all stock alerts from the database.
     * @return list of all alerts
     */
    public List<StockAlert> findAll() {
        List<StockAlert> alerts = new ArrayList<>();
        String sql = "SELECT a.*, f.name AS flower_name " +
                     "FROM stock_alerts a " +
                     "LEFT JOIN flowers f ON a.flower_id = f.id " +
                     "ORDER BY a.resolved ASC, a.generated_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                StockAlert alert = extractAlertFromResultSet(rs);
                alert.setFlowerName(rs.getString("flower_name"));
                alerts.add(alert);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving alerts: " + e.getMessage());
        }
        
        return alerts;
    }

    /**
     * Retrieves unresolved stock alerts.
     * @return list of unresolved alerts
     */
    public List<StockAlert> findUnresolved() {
        List<StockAlert> alerts = new ArrayList<>();
        String sql = "SELECT a.*, f.name AS flower_name " +
                     "FROM stock_alerts a " +
                     "LEFT JOIN flowers f ON a.flower_id = f.id " +
                     "WHERE a.resolved = FALSE " +
                     "ORDER BY a.generated_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                StockAlert alert = extractAlertFromResultSet(rs);
                alert.setFlowerName(rs.getString("flower_name"));
                alerts.add(alert);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving unresolved alerts: " + e.getMessage());
        }
        
        return alerts;
    }

    /**
     * Counts unresolved alerts.
     * @return number of unresolved alerts
     */
    public int countUnresolved() {
        String sql = "SELECT COUNT(*) FROM stock_alerts WHERE resolved = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting unresolved alerts: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Checks if an alert already exists for a flower.
     * @param flowerId the flower ID
     * @param alertType the alert type
     * @return true if alert exists and is unresolved
     */
    public boolean alertExists(int flowerId, String alertType) {
        String sql = "SELECT COUNT(*) FROM stock_alerts " +
                     "WHERE flower_id = ? AND alert_type = ? AND resolved = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, flowerId);
            pstmt.setString(2, alertType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking alert existence: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Inserts a new stock alert.
     * @param alert the alert to insert
     * @return the generated ID, or -1 if failed
     */
    public int insert(StockAlert alert) {
        // Check if alert already exists
        if (alertExists(alert.getFlowerId(), alert.getAlertType())) {
            System.out.println("Alert already exists for flower ID " + alert.getFlowerId());
            return -1;
        }
        
        String sql = "INSERT INTO stock_alerts (flower_id, alert_type, message, generated_date, resolved) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, alert.getFlowerId());
            pstmt.setString(2, alert.getAlertType());
            pstmt.setString(3, alert.getMessage());
            pstmt.setDate(4, Date.valueOf(alert.getGeneratedDate()));
            pstmt.setBoolean(5, alert.isResolved());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting alert: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Marks an alert as resolved.
     * @param id the alert ID
     * @return true if successful
     */
    public boolean markResolved(int id) {
        String sql = "UPDATE stock_alerts SET resolved = TRUE, resolved_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error marking alert as resolved: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes an alert by ID.
     * @param id the alert ID
     * @return true if successful
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM stock_alerts WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting alert: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts a StockAlert object from a ResultSet.
     * @param rs the ResultSet
     * @return the StockAlert object
     * @throws SQLException if extraction fails
     */
    private StockAlert extractAlertFromResultSet(ResultSet rs) throws SQLException {
        StockAlert alert = new StockAlert();
        alert.setId(rs.getInt("id"));
        alert.setFlowerId(rs.getInt("flower_id"));
        alert.setAlertType(rs.getString("alert_type"));
        alert.setMessage(rs.getString("message"));
        
        Date generatedDate = rs.getDate("generated_date");
        if (generatedDate != null) {
            alert.setGeneratedDate(generatedDate.toLocalDate());
        }
        
        alert.setResolved(rs.getBoolean("resolved"));
        
        return alert;
    }
}
