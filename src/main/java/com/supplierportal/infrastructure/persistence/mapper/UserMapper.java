package com.supplierportal.infrastructure.persistence.mapper;

import com.supplierportal.domain.shared.valueobject.Email;
import com.supplierportal.domain.user.Role;
import com.supplierportal.domain.user.User;
import com.supplierportal.domain.user.UserStatus;
import com.supplierportal.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.Instant;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(Email.of(entity.getEmail()))
                .passwordHash(entity.getPasswordHash())
                .role(Role.valueOf(entity.getRole()))
                .status(UserStatus.valueOf(entity.getStatus()))
                .failedLoginAttempts(entity.getFailedLoginAttempts() != null ? entity.getFailedLoginAttempts() : 0)
                .lastLoginAt(toInstant(entity.getLastLoginAt()))
                .createdAt(toInstant(entity.getCreatedAt()))
                .updatedAt(toInstant(entity.getUpdatedAt()))
                .mustChangePassword(entity.isMustChangePassword())
                .build();
    }

    public UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) {
            return null;
        }
        return UserJpaEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .email(domain.getEmail() != null ? domain.getEmail().getValue() : null)
                .passwordHash(domain.getPasswordHash())
                .role(domain.getRole().name())
                .status(domain.getStatus().name())
                .failedLoginAttempts(domain.getFailedLoginAttempts())
                .lastLoginAt(toLocalDateTime(domain.getLastLoginAt()))
                .createdAt(toLocalDateTime(domain.getCreatedAt()))
                .updatedAt(toLocalDateTime(domain.getUpdatedAt()))
                .mustChangePassword(domain.isMustChangePassword())
                .build();
    }

    private Instant toInstant(LocalDateTime ldt) {
        return ldt != null ? ldt.toInstant(ZoneOffset.UTC) : null;
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }
}
