package com.supplierportal.domain.supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository {
    Optional<Supplier> findById(Long id);
    Optional<Supplier> findByRegisteredByUserId(Long userId);
    List<Supplier> findAll();
    List<Supplier> findByStatus(SupplierStatus status);
    Supplier save(Supplier supplier);
    void deleteById(Long id);
    boolean existsByIce(String iceValue);
}
