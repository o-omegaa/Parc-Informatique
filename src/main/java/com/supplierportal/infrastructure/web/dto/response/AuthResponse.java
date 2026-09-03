package com.supplierportal.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long userId;
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private String username;
    private String role;
    /** true when the user logged in with a temporary password and must change it */
    private boolean mustChangePassword;
}
