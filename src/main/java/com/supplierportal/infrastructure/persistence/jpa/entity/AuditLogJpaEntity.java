package com.supplierportal.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id")
    private UserJpaEntity actorUser;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "target_entity_type", length = 50)
    private String targetEntityType;

    @Column(name = "target_entity_id")
    private Long targetEntityId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(nullable = false, length = 10)
    private String outcome;

    @Column(length = 1000)
    private String detail;
}
