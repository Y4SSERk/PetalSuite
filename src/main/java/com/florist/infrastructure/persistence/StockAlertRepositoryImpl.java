package com.florist.infrastructure.persistence;

import com.florist.domain.repository.StockAlertRepository;
import com.florist.model.StockAlert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository implementation for StockAlert entity.
 * Directly handles SQL operations for efficiency.
 */
public class StockAlertRepositoryImpl implements StockAlertRepository {

    @Override
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
            e.printStackTrace();
        }
        return alerts;
    }

    @Override
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
            e.printStackTrace();
        }
        return alerts;
    }

    @Override
    public Optional<StockAlert> findById(int id) {
        return findUnresolved().stream().filter(a -> a.getId() == id).findFirst();
    }

    @Override
    public StockAlert save(StockAlert alert) {
        if (alertExists(alert.getFlowerId(), alert.getAlertType())) {
            return alert;
        }

        String sql = "INSERT INTO stock_alerts (flower_id, alert_type, severity, message, generated_date, resolved) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, alert.getFlowerId());
            pstmt.setString(2, alert.getAlertType());
            pstmt.setString(3, alert.getSeverity());
            pstmt.setString(4, alert.getMessage());
            pstmt.setDate(5, Date.valueOf(alert.getGeneratedDate()));
            pstmt.setBoolean(6, alert.isResolved());

            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        alert.setId(rs.getInt(1));
                        return alert;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failed to insert alert");
    }

    @Override
    public boolean markResolved(int id) {
        String sql = "UPDATE stock_alerts SET resolved = TRUE, resolved_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM stock_alerts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int countUnresolved() {
        String sql = "SELECT COUNT(*) FROM stock_alerts WHERE resolved = FALSE";
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

    @Override
    public boolean alertExists(int flowerId, String alertType) {
        String sql = "SELECT COUNT(*) FROM stock_alerts WHERE flower_id = ? AND alert_type = ? AND resolved = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, flowerId);
            pstmt.setString(2, alertType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void deleteUnresolved(int flowerId, String alertType) {
        String sql = "DELETE FROM stock_alerts WHERE flower_id = ? AND alert_type = ? AND resolved = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, flowerId);
            pstmt.setString(2, alertType);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private StockAlert extractAlertFromResultSet(ResultSet rs) throws SQLException {
        StockAlert alert = new StockAlert();
        alert.setId(rs.getInt("id"));
        alert.setFlowerId(rs.getInt("flower_id"));
        alert.setAlertType(rs.getString("alert_type"));
        alert.setSeverity(rs.getString("severity"));
        alert.setMessage(rs.getString("message"));
        Date generatedDate = rs.getDate("generated_date");
        if (generatedDate != null) {
            alert.setGeneratedDate(generatedDate.toLocalDate());
        }
        alert.setResolved(rs.getBoolean("resolved"));
        return alert;
    }
}
