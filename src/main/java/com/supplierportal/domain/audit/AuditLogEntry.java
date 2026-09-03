package com.supplierportal.domain.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogEntry {
    private Long id;
    private Instant timestamp;
    private Long actorUserId;
    private AuditActionType actionType;
    private String targetEntityType;
    private Long targetEntityId;
    private String ipAddress;
    private String outcome;
    private String detail;
}
