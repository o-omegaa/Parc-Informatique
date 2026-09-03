package com.supplierportal.application.auth.service;

import com.supplierportal.application.auth.command.LoginCommand;
import com.supplierportal.application.auth.command.RegisterCommand;
import com.supplierportal.application.auth.port.PasswordEncoderPort;
import com.supplierportal.application.auth.port.TokenServicePort;
import com.supplierportal.application.auth.result.AuthenticationResult;
import com.supplierportal.application.audit.service.AuditService;
import com.supplierportal.domain.audit.AuditActionType;
import com.supplierportal.domain.shared.exception.NotFoundException;
import com.supplierportal.domain.shared.exception.ValidationException;
import com.supplierportal.domain.shared.valueobject.Email;
import com.supplierportal.domain.shared.valueobject.IceNumber;
import com.supplierportal.domain.supplier.Supplier;
import com.supplierportal.domain.supplier.SupplierCategory;
import com.supplierportal.domain.supplier.SupplierRepository;
import com.supplierportal.domain.supplier.SupplierStatus;
import com.supplierportal.domain.user.Role;
import com.supplierportal.domain.user.User;
import com.supplierportal.domain.user.UserRepository;
import com.supplierportal.domain.user.UserStatus;
import com.supplierportal.infrastructure.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenServicePort tokenService;
    private final AuditService auditService;
    private final EmailService emailService;

    // Caractères autorisés pour le mot de passe temporaire (sans ambigüité visuelle)
    private static final String TEMP_PWD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789@#&!";
    private static final int TEMP_PWD_LENGTH = 12;

    /**
     * Generates a cryptographically secure temporary password.
     */
    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(TEMP_PWD_LENGTH);
        for (int i = 0; i < TEMP_PWD_LENGTH; i++) {
            sb.append(TEMP_PWD_CHARS.charAt(random.nextInt(TEMP_PWD_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Registers a new supplier:
     * 1. Creates account with DISABLED status (pending admin activation)
     * 2. Generates a temporary password
     * 3. Sends the temporary password by email
     * 4. Sets mustChangePassword = true (enforced on first login)
     */
    public AuthenticationResult register(RegisterCommand command) {
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new ValidationException("Ce nom d'utilisateur est déjà pris.");
        }
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new ValidationException("Cette adresse email est déjà utilisée.");
        }

        // Only SUPPLIER self-registration is allowed
        if (command.getRole() != null && !command.getRole().equalsIgnoreCase("SUPPLIER")) {
            throw new ValidationException("Seul le rôle SUPPLIER est autorisé pour l'inscription publique.");
        }

        // Generate a temporary password (ignores what the user typed)
        String temporaryPassword = generateTemporaryPassword();
        log.info("📧 Mot de passe temporaire généré pour {} — envoi email à {}",
                command.getUsername(), command.getEmail());

        // DGSSI: Supplier accounts start DISABLED — admin must activate
        User user = User.builder()
                .username(command.getUsername())
                .email(Email.of(command.getEmail()))
                .passwordHash(passwordEncoder.encode(temporaryPassword))
                .role(Role.SUPPLIER)
                .status(UserStatus.DISABLED)
                .mustChangePassword(true)
                .failedLoginAttempts(0)
                .createdAt(Instant.now())
                .build();
        user = userRepository.save(user);

        // Create supplier profile if company name provided
        if (command.getCompanyName() != null && !command.getCompanyName().isBlank()) {
            String iceValue = (command.getIce() != null && !command.getIce().isBlank())
                    ? command.getIce() : "000000000000000";
            SupplierCategory parsedCategory;
            try {
                String rawCat = (command.getCategory() != null && !command.getCategory().isBlank())
                        ? command.getCategory().toUpperCase().replace(" ", "_") : "AUTRE";
                parsedCategory = SupplierCategory.valueOf(rawCat);
            } catch (IllegalArgumentException ex) {
                parsedCategory = SupplierCategory.AUTRE;
            }
            Supplier supplier = Supplier.builder()
                    .companyName(command.getCompanyName())
                    .ice(IceNumber.of(iceValue))
                    .category(parsedCategory)
                    .status(SupplierStatus.PENDING_VALIDATION)
                    .registeredByUserId(user.getId())
                    .createdAt(Instant.now())
                    .build();
            supplierRepository.save(supplier);
        }

        auditService.record(user.getId(), AuditActionType.SUPPLIER_REGISTERED,
                "User", user.getId(), "Fournisseur inscrit - en attente de validation");

        // Send temporary password email asynchronously (won't block the response)
        emailService.sendTemporaryPassword(command.getEmail(), command.getUsername(), temporaryPassword);

        return AuthenticationResult.builder()
                .userId(user.getId())
                .accessToken(null)
                .username(user.getUsername())
                .role(user.getRole().name())
                .pendingValidation(true)
                .mustChangePassword(false) // Not yet — admin activates first
                .build();
    }

    public AuthenticationResult login(LoginCommand command) {
        User user = userRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> {
                    auditService.recordFailure(null, AuditActionType.LOGIN_FAILURE,
                            "User", null, "Identifiant inconnu");
                    return new ValidationException("Identifiants invalides.");
                });

        if (user.getStatus() == UserStatus.DISABLED) {
            auditService.recordFailure(user.getId(), AuditActionType.LOGIN_FAILURE,
                    "User", user.getId(), "Compte en attente de validation");
            throw new ValidationException("Votre compte est en attente de validation par l'administrateur.");
        }

        if (!user.isEligibleToAuthenticate()) {
            auditService.recordFailure(user.getId(), AuditActionType.LOGIN_FAILURE,
                    "User", user.getId(), "Compte verrouillé ou désactivé");
            throw new ValidationException("Votre compte est verrouillé ou désactivé. Contactez l'administrateur.");
        }

        if (!passwordEncoder.matches(command.getPassword(), user.getPasswordHash())) {
            user.registerFailedLogin();
            userRepository.save(user);
            auditService.recordFailure(user.getId(), AuditActionType.LOGIN_FAILURE,
                    "User", user.getId(), "Mot de passe incorrect");
            int remaining = 5 - user.getFailedLoginAttempts();
            if (remaining <= 0) {
                throw new ValidationException("Compte verrouillé après trop de tentatives. Contactez l'administrateur.");
            }
            throw new ValidationException("Identifiants invalides. " + remaining + " tentative(s) restante(s).");
        }

        user.resetFailedLoginAttempts();
        userRepository.save(user);
        auditService.record(user.getId(), AuditActionType.LOGIN_SUCCESS,
                "User", user.getId(), "Connexion réussie");

        String token = tokenService.generateAccessToken(user.getUsername(), user.getRole().name());

        return AuthenticationResult.builder()
                .userId(user.getId())
                .accessToken(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .pendingValidation(false)
                .mustChangePassword(user.isMustChangePassword())
                .build();
    }

    public void changePassword(com.supplierportal.application.auth.command.ChangePasswordCommand command) {
        User user = userRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé."));
        if (!passwordEncoder.matches(command.getCurrentPassword(), user.getPasswordHash())) {
            throw new ValidationException("Mot de passe actuel incorrect.");
        }
        if (command.getNewPassword() == null || command.getNewPassword().length() < 12) {
            throw new ValidationException("Le nouveau mot de passe doit contenir au moins 12 caractères.");
        }
        user.setPasswordHash(passwordEncoder.encode(command.getNewPassword()));
        user.setMustChangePassword(false);  // Flag cleared after successful change
        userRepository.save(user);
        auditService.record(user.getId(), AuditActionType.USER_STATUS_CHANGED,
                "User", user.getId(), "Mot de passe changé");
    }
}
