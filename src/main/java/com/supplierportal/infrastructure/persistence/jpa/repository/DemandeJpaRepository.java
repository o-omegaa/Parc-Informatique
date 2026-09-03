package com.supplierportal.infrastructure.persistence.jpa.repository;

import com.supplierportal.infrastructure.persistence.jpa.entity.DemandeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DemandeJpaRepository extends JpaRepository<DemandeJpaEntity, Long> {
    List<DemandeJpaEntity> findByFournisseurUserId(Long userId);
    List<DemandeJpaEntity> findByAppelOffreId(Long appelOffreId);
    List<DemandeJpaEntity> findByStatus(String status);
    boolean existsByAppelOffreIdAndFournisseurUserId(Long appelOffreId, Long fournisseurUserId);
}
