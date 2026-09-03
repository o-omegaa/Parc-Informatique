package com.supplierportal.infrastructure.web.dto.response;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String status;
    private int failedLoginAttempts;
    private String lastLoginAt;
    private String createdAt;
}
