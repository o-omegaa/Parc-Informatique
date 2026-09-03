package com.supplierportal.application.audit.service;

import com.supplierportal.domain.audit.AuditActionType;
import com.supplierportal.domain.audit.AuditLogEntry;
import com.supplierportal.domain.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Centralises business audit entries so controllers and services do not each
 * build a different audit record format.
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void record(Long actorUserId, AuditActionType actionType,
                       String targetEntityType, Long targetEntityId, String detail) {
        auditLogRepository.append(AuditLogEntry.builder()
                .timestamp(Instant.now())
                .actorUserId(actorUserId)
                .actionType(actionType)
                .targetEntityType(targetEntityType)
                .targetEntityId(targetEntityId)
                .outcome("SUCCESS")
                .detail(detail)
                .build());
    }

    public void recordFailure(Long actorUserId, AuditActionType actionType,
                              String targetEntityType, Long targetEntityId, String detail) {
        auditLogRepository.append(AuditLogEntry.builder()
                .timestamp(Instant.now())
                .actorUserId(actorUserId)
                .actionType(actionType)
                .targetEntityType(targetEntityType)
                .targetEntityId(targetEntityId)
                .outcome("FAILURE")
                .detail(detail)
                .build());
    }
}
