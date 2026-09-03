package com.supplierportal.infrastructure.persistence.jpa.repository;

import com.supplierportal.infrastructure.persistence.jpa.entity.FactureJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FactureJpaRepository extends JpaRepository<FactureJpaEntity, Long> {
    List<FactureJpaEntity> findByFournisseurUserId(Long userId);
    List<FactureJpaEntity> findByDemandeId(Long demandeId);
    List<FactureJpaEntity> findByStatus(String status);
}
