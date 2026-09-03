package com.supplierportal.infrastructure.config;

import com.supplierportal.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.supplierportal.infrastructure.persistence.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** Creates an administrator only for an empty development database. */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    private static final String DEFAULT_ADMIN_EMAIL = "admin@supplierportal.ma";
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin-password:}")
    private String initialAdminPassword;

    @Value("${app.bootstrap.admin-email:admin@supplierportal.ma}")
    private String initialAdminEmail;

    @Override
    public void run(String... args) {
        var existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin.isPresent()) {
            updateDefaultAdminEmail(existingAdmin.get());
            // If a password is explicitly set in config, reset admin password
            if (initialAdminPassword != null && initialAdminPassword.length() >= 8) {
                UserJpaEntity admin = existingAdmin.get();
                admin.setPasswordHash(passwordEncoder.encode(initialAdminPassword));
                admin.setStatus("ACTIVE");
                admin.setFailedLoginAttempts(0);
                admin.setUpdatedAt(LocalDateTime.now());
                userRepository.save(admin);
                log.info("✅ Admin password has been reset from configuration.");
            } else {
                log.info("Admin account already exists; its password is never reset at startup.");
            }
            return;
        }
        if (initialAdminPassword == null || initialAdminPassword.length() < 12) {
            log.warn("No initial admin account was created. Set APP_BOOTSTRAP_ADMIN_PASSWORD for a fresh database.");
            return;
        }
        UserJpaEntity admin = UserJpaEntity.builder()
                .username("admin")
                .email(initialAdminEmail)
                .passwordHash(passwordEncoder.encode(initialAdminPassword))
                .role("ADMIN")
                .status("ACTIVE")
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(admin);
        log.info("Initial admin user created.");
    }

    private void updateDefaultAdminEmail(UserJpaEntity admin) {
        if (initialAdminEmail == null || initialAdminEmail.isBlank()
                || DEFAULT_ADMIN_EMAIL.equalsIgnoreCase(initialAdminEmail)
                || !DEFAULT_ADMIN_EMAIL.equalsIgnoreCase(admin.getEmail())) {
            return;
        }
        if (userRepository.existsByEmail(initialAdminEmail)) {
            log.warn("Initial admin email was not updated because the configured address is already used.");
            return;
        }
        admin.setEmail(initialAdminEmail.trim().toLowerCase());
        admin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(admin);
        log.info("Default administrator email was updated from local configuration.");
    }
}
