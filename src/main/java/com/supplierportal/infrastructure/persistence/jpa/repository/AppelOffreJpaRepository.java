package com.supplierportal.infrastructure.persistence.jpa.repository;

import com.supplierportal.infrastructure.persistence.jpa.entity.AppelOffreJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppelOffreJpaRepository extends JpaRepository<AppelOffreJpaEntity, Long> {
    List<AppelOffreJpaEntity> findByStatus(String status);
    List<AppelOffreJpaEntity> findByPublishedByUserId(Long userId);
}
