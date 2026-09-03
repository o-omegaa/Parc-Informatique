package com.supplierportal.domain.audit;

import java.time.Instant;
import java.util.List;

public interface AuditLogRepository {
    AuditLogEntry append(AuditLogEntry entry);
    List<AuditLogEntry> findByActorUserId(Long userId);
    List<AuditLogEntry> findByActionType(AuditActionType actionType);
    List<AuditLogEntry> findByTimestampBetween(Instant from, Instant to);
    List<AuditLogEntry> findAll();
}
