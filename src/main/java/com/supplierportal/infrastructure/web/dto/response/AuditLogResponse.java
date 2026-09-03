package com.supplierportal.infrastructure.web.dto.response;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLogResponse {
    private Long id;
    private String timestamp;
    private Long actorUserId;
    private String actionType;
    private String targetEntityType;
    private Long targetEntityId;
    private String ipAddress;
    private String outcome;
    private String detail;
}
