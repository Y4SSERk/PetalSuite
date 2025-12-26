package com.florist.domain.repository;

import com.florist.model.StockAlert;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StockAlert entity.
 */
public interface StockAlertRepository {

    List<StockAlert> findAll();

    List<StockAlert> findUnresolved();

    Optional<StockAlert> findById(int id);

    StockAlert save(StockAlert alert);

    boolean markResolved(int id);

    boolean delete(int id);

    int countUnresolved();

    boolean alertExists(int flowerId, String alertType);

    void deleteUnresolved(int flowerId, String alertType);
}
