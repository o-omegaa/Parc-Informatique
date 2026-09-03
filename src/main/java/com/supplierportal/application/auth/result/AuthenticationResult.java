package com.supplierportal.application.auth.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthenticationResult {
    private Long userId;
    private String accessToken;
    private String username;
    private String role;
    private boolean pendingValidation;
    /** true if the user must change their temporary password before accessing the portal */
    private boolean mustChangePassword;
}
