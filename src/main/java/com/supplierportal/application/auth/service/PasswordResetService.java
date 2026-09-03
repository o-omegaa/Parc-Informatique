package com.supplierportal.application.auth.service;

import com.supplierportal.application.auth.port.PasswordEncoderPort;
import com.supplierportal.domain.shared.exception.NotFoundException;
import com.supplierportal.domain.shared.exception.ValidationException;
import com.supplierportal.domain.user.User;
import com.supplierportal.domain.user.UserRepository;
import com.supplierportal.infrastructure.email.EmailService;
import com.supplierportal.infrastructure.persistence.jpa.entity.PasswordResetTokenEntity;
import com.supplierportal.infrastructure.persistence.jpa.repository.PasswordResetTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenJpaRepository tokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset-url:http://localhost:8080/index.html?resetToken=%s}")
    private String resetUrlTemplate;

    private static final int TOKEN_EXPIRY_MINUTES = 30;

    /**
     * Initiates a password reset. Generates a secure token,
     * stores its hash, and sends the reset link by email.
     * Always returns success (security: never reveal if email exists).
     */
    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            // Invalidate all existing tokens for this user
            tokenRepository.deleteByUserId(user.getId());

            // Generate secure random token
            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            String plainToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            String tokenHash = passwordEncoder.encode(plainToken);

            // Save hashed token (never store plain token)
            PasswordResetTokenEntity entity = PasswordResetTokenEntity.builder()
                    .userId(user.getId())
                    .tokenHash(tokenHash)
                    .expiryDate(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES))
                    .used(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            tokenRepository.save(entity);

            // Send reset email asynchronously
            String resetLink = String.format(resetUrlTemplate, plainToken);
            emailService.sendPasswordResetLink(
                    user.getEmail().getValue(),
                    user.getUsername(),
                    resetLink,
                    TOKEN_EXPIRY_MINUTES
            );

            log.info("📧 Lien de réinitialisation envoyé à : {}", email);
        });
    }

    /**
     * Validates the reset token and sets the new password.
     */
    public void resetPassword(String plainToken, String newPassword) {
        if (newPassword == null || newPassword.length() < 12) {
            throw new ValidationException("Le nouveau mot de passe doit contenir au moins 12 caractères.");
        }

        // Find all active tokens and check which one matches
        var tokens = tokenRepository.findAllActiveTokens(LocalDateTime.now());
        PasswordResetTokenEntity matched = tokens.stream()
                .filter(t -> passwordEncoder.matches(plainToken, t.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Lien de réinitialisation invalide ou expiré."));

        if (matched.isUsed()) {
            throw new ValidationException("Ce lien de réinitialisation a déjà été utilisé.");
        }

        // Find user and update password
        User user = userRepository.findById(matched.getUserId())
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable."));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);

        // Mark token as used
        matched.setUsed(true);
        tokenRepository.save(matched);

        log.info("✅ Mot de passe réinitialisé pour userId={}", user.getId());
    }
}
