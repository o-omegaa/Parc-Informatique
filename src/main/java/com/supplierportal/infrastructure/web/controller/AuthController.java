package com.supplierportal.infrastructure.web.controller;

import com.supplierportal.application.auth.command.ChangePasswordCommand;
import com.supplierportal.application.auth.command.LoginCommand;
import com.supplierportal.application.auth.command.RegisterCommand;
import com.supplierportal.application.auth.port.PasswordEncoderPort;
import com.supplierportal.application.auth.port.TokenServicePort;
import com.supplierportal.application.auth.result.AuthenticationResult;
import com.supplierportal.application.auth.service.AuthenticationService;
import com.supplierportal.application.auth.service.PasswordResetService;
import com.supplierportal.infrastructure.security.ratelimit.AuthenticationRateLimiter;
import com.supplierportal.infrastructure.web.dto.request.ForgotPasswordRequest;
import com.supplierportal.infrastructure.web.dto.request.LoginRequest;
import com.supplierportal.infrastructure.web.dto.request.RegisterRequest;
import com.supplierportal.infrastructure.web.dto.request.ResetPasswordRequest;
import com.supplierportal.infrastructure.web.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;
    private final PasswordResetService passwordResetService;
    private final AuthenticationRateLimiter authenticationRateLimiter;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                       HttpServletRequest httpRequest) {
        enforceRateLimit(httpRequest, request.getUsername());
        RegisterCommand command = new RegisterCommand(
                request.getUsername(), request.getEmail(),
                request.getCompanyName(), request.getIce(), request.getCategory(),
                "SUPPLIER"
        );
        AuthenticationResult result = authService.register(command);

        if (result.isPendingValidation()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                    "message", "Votre compte a été créé. Un mot de passe temporaire vous sera envoyé par email une fois votre compte validé par l'administrateur.",
                    "pendingValidation", true,
                    "username", result.getUsername()
            ));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest) {
        enforceRateLimit(httpRequest, request.getUsername());
        AuthenticationResult result = authService.login(new LoginCommand(request.getUsername(), request.getPassword()));
        return ResponseEntity.ok(toResponse(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // JWT is stateless — client clears sessionStorage (DGSSI 8.2.3)
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                Principal principal) {
        authService.changePassword(new ChangePasswordCommand(
                principal.getName(), request.currentPassword(), request.newPassword()));
        return ResponseEntity.noContent().build();
    }

    private AuthResponse toResponse(AuthenticationResult result) {
        return AuthResponse.builder()
                .userId(result.getUserId())
                .accessToken(result.getAccessToken())
                .username(result.getUsername())
                .role(result.getRole())
                .mustChangePassword(result.isMustChangePassword())
                .build();
    }

    private void enforceRateLimit(HttpServletRequest request, String username) {
        String ip = request.getRemoteAddr();
        if (!authenticationRateLimiter.allow(ip + ":" + username)) {
            throw new com.supplierportal.domain.shared.exception.ValidationException(
                    "Trop de tentatives. Réessayez dans une minute.");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // Always returns 200 for security (don't reveal if email exists)
        passwordResetService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Si cette adresse email est associée à un compte, un lien de réinitialisation vous a été envoyé."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès."));
    }

    // Inner request DTO for change-password
    public record ChangePasswordRequest(String currentPassword, String newPassword) {}
}
