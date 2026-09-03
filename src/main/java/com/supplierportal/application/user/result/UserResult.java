package com.supplierportal.application.user.result;
import lombok.*;
import java.time.Instant;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class UserResult {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String status;
    private int failedLoginAttempts;
    private Instant lastLoginAt;
    private Instant createdAt;
}
