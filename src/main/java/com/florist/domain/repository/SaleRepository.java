package com.florist.domain.repository;

import com.florist.model.Sale;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Sale entity.
 */
public interface SaleRepository {

    List<Sale> findAll();

    List<Sale> findByDateRange(LocalDate startDate, LocalDate endDate);

    Optional<Sale> findById(int id);

    Sale save(Sale sale);

    boolean delete(int id);

    int countTodaySales();
}
