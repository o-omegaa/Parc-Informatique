package com.supplierportal.infrastructure.persistence.jpa.repository;
import com.supplierportal.infrastructure.persistence.jpa.entity.DocumentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DocumentJpaRepository extends JpaRepository<DocumentJpaEntity, Long> {
    List<DocumentJpaEntity> findBySupplierId(Long supplierId);
    List<DocumentJpaEntity> findByStatus(String status);
}
