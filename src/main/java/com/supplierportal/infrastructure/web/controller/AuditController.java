package com.supplierportal.infrastructure.web.controller;

import com.supplierportal.domain.audit.*;
import com.supplierportal.infrastructure.web.dto.response.AuditLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
public class AuditController {
    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLogResponse> getAll() {
        return auditLogRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/user/{userId}")
    public List<AuditLogResponse> getByUser(@PathVariable Long userId) {
        return auditLogRepository.findByActorUserId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/action/{actionType}")
    public List<AuditLogResponse> getByAction(@PathVariable String actionType) {
        return auditLogRepository.findByActionType(AuditActionType.valueOf(actionType)).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AuditLogResponse toResponse(AuditLogEntry e) {
        return AuditLogResponse.builder()
                .id(e.getId())
                .timestamp(e.getTimestamp() != null ? e.getTimestamp().toString() : null)
                .actorUserId(e.getActorUserId())
                .actionType(e.getActionType() != null ? e.getActionType().name() : null)
                .targetEntityType(e.getTargetEntityType())
                .targetEntityId(e.getTargetEntityId())
                .ipAddress(e.getIpAddress())
                .outcome(e.getOutcome())
                .detail(e.getDetail())
                .build();
    }
}
