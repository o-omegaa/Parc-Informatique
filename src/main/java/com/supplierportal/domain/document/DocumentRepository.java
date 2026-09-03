package com.supplierportal.domain.document;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository {
    Optional<SupplierDocument> findById(Long id);
    List<SupplierDocument> findAll();
    List<SupplierDocument> findBySupplierId(Long supplierId);
    List<SupplierDocument> findByStatus(DocumentStatus status);
    SupplierDocument save(SupplierDocument document);
    void deleteById(Long id);
}
