package com.supplierportal.domain.user;

import com.supplierportal.domain.shared.valueobject.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private Email email;
    private String passwordHash;
    private Role role;
    private UserStatus status;
    private int failedLoginAttempts;
    private Instant lastLoginAt;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean mustChangePassword;

    public boolean isEligibleToAuthenticate() {
        return this.status == UserStatus.ACTIVE;
    }

    public void registerFailedLogin() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.status = UserStatus.LOCKED;
        }
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lastLoginAt = Instant.now();
    }

    public void lock() {
        this.status = UserStatus.LOCKED;
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.failedLoginAttempts = 0;
    }
}
