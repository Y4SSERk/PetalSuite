package com.florist.infrastructure.persistence;

import com.florist.domain.repository.FlowerRepository;
import com.florist.model.Flower;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository implementation for Flower entity.
 * Directly handles SQL operations for efficiency.
 */
public class FlowerRepositoryImpl implements FlowerRepository {

    @Override
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
            e.printStackTrace();
        }
        return flowers;
    }

    @Override
    public List<Flower> findAllWithSuppliers() {
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
            e.printStackTrace();
        }
        return flowers;
    }

    @Override
    public Optional<Flower> findById(int id) {
        String sql = "SELECT * FROM flowers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractFlowerFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Flower save(Flower flower) {
        if (flower.getId() == 0) {
            return insert(flower);
        } else {
            return update(flower);
        }
    }

    private Flower insert(Flower flower) {
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

            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        flower.setId(rs.getInt(1));
                        return flower;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failed to insert flower");
    }

    private Flower update(Flower flower) {
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

            if (pstmt.executeUpdate() > 0) {
                return flower;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failed to update flower");
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM flowers WHERE id = ?";
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
    public boolean updateStock(int id, int quantity) {
        String sql = "UPDATE flowers SET quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
