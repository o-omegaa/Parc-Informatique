package com.supplierportal.infrastructure.persistence.jpa.repository;

import com.supplierportal.infrastructure.persistence.jpa.entity.SupplierJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierJpaRepository extends JpaRepository<SupplierJpaEntity, Long> {
    Optional<SupplierJpaEntity> findByRegisteredByUserId(Long userId);
    List<SupplierJpaEntity> findByStatus(String status);
    boolean existsByIce(String ice);
}
