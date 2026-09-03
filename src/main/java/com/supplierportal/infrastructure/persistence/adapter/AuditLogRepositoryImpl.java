package com.supplierportal.infrastructure.persistence.adapter;

import com.supplierportal.domain.audit.AuditActionType;
import com.supplierportal.domain.audit.AuditLogEntry;
import com.supplierportal.domain.audit.AuditLogRepository;
import com.supplierportal.infrastructure.persistence.jpa.repository.AuditLogJpaRepository;
import com.supplierportal.infrastructure.persistence.mapper.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogJpaRepository auditLogJpaRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public AuditLogEntry append(AuditLogEntry entry) {
        return auditLogMapper.toDomain(auditLogJpaRepository.save(auditLogMapper.toJpaEntity(entry)));
    }

    @Override
    public List<AuditLogEntry> findByActorUserId(Long userId) {
        return auditLogJpaRepository.findByActorUserId(userId).stream()
                .map(auditLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogEntry> findByActionType(AuditActionType actionType) {
        return auditLogJpaRepository.findByActionType(actionType.name()).stream()
                .map(auditLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogEntry> findByTimestampBetween(Instant from, Instant to) {
        return auditLogJpaRepository.findByEventTimestampBetween(from, to).stream()
                .map(auditLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogEntry> findAll() {
        return auditLogJpaRepository.findAll().stream()
                .map(auditLogMapper::toDomain)
                .collect(Collectors.toList());
    }
}
