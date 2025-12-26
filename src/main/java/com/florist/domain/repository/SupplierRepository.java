package com.florist.domain.repository;

import com.florist.model.Supplier;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Supplier entity.
 */
public interface SupplierRepository {

    List<Supplier> findAll();

    Optional<Supplier> findById(int id);

    Supplier save(Supplier supplier);

    boolean delete(int id);
}
