package com.supplierportal.infrastructure.persistence.jpa.repository;

import com.supplierportal.infrastructure.persistence.jpa.entity.AuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, Long> {
    List<AuditLogJpaEntity> findByActorUserId(Long userId);
    List<AuditLogJpaEntity> findByActionType(String actionType);
    List<AuditLogJpaEntity> findByEventTimestampBetween(Instant from, Instant to);
}
