package com.supplierportal.infrastructure.persistence.mapper;

import com.supplierportal.domain.audit.AuditActionType;
import com.supplierportal.domain.audit.AuditLogEntry;
import com.supplierportal.infrastructure.persistence.jpa.entity.AuditLogJpaEntity;
import com.supplierportal.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogEntry toDomain(AuditLogJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return AuditLogEntry.builder()
                .id(entity.getId())
                .timestamp(entity.getEventTimestamp())
                .actorUserId(entity.getActorUser() != null ? entity.getActorUser().getId() : null)
                .actionType(AuditActionType.valueOf(entity.getActionType()))
                .targetEntityType(entity.getTargetEntityType())
                .targetEntityId(entity.getTargetEntityId())
                .ipAddress(entity.getIpAddress())
                .outcome(entity.getOutcome())
                .detail(entity.getDetail())
                .build();
    }

    public AuditLogJpaEntity toJpaEntity(AuditLogEntry domain) {
        if (domain == null) {
            return null;
        }
        UserJpaEntity actorUser = null;
        if (domain.getActorUserId() != null) {
            actorUser = new UserJpaEntity();
            actorUser.setId(domain.getActorUserId());
        }

        return AuditLogJpaEntity.builder()
                .id(domain.getId())
                .eventTimestamp(domain.getTimestamp())
                .actorUser(actorUser)
                .actionType(domain.getActionType().name())
                .targetEntityType(domain.getTargetEntityType())
                .targetEntityId(domain.getTargetEntityId())
                .ipAddress(domain.getIpAddress())
                .outcome(domain.getOutcome())
                .detail(domain.getDetail())
                .build();
    }
}
