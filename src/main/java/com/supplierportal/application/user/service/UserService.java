package com.supplierportal.application.user.service;

import com.supplierportal.application.auth.port.PasswordEncoderPort;
import com.supplierportal.application.user.command.CreateUserCommand;
import com.supplierportal.application.user.command.UpdateUserCommand;
import com.supplierportal.application.user.result.UserResult;
import com.supplierportal.domain.shared.exception.NotFoundException;
import com.supplierportal.domain.shared.exception.ValidationException;
import com.supplierportal.domain.shared.valueobject.Email;
import com.supplierportal.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public List<UserResult> findAll() {
        return userRepository.findAll().stream().map(this::toResult).collect(Collectors.toList());
    }

    public UserResult findById(Long id) {
        return toResult(userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id)));
    }

    public UserResult create(CreateUserCommand cmd) {
        if (userRepository.existsByUsername(cmd.getUsername())) throw new ValidationException("Username already exists");
        if (userRepository.existsByEmail(cmd.getEmail())) throw new ValidationException("Email already exists");
        User user = User.builder()
                .username(cmd.getUsername())
                .email(Email.of(cmd.getEmail()))
                .passwordHash(passwordEncoder.encode(cmd.getPassword()))
                .role(Role.valueOf(cmd.getRole()))
                .status(UserStatus.ACTIVE)
                .failedLoginAttempts(0)
                .createdAt(Instant.now())
                .build();
        return toResult(userRepository.save(user));
    }

    public UserResult update(UpdateUserCommand cmd) {
        User user = userRepository.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("User not found"));
        if (cmd.getUsername() != null && !cmd.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(cmd.getUsername(), user.getId())) {
                throw new ValidationException("Username already exists");
            }
            user.setUsername(cmd.getUsername());
        }
        if (cmd.getEmail() != null && !cmd.getEmail().equals(user.getEmail().getValue())) {
            if (userRepository.existsByEmailAndIdNot(cmd.getEmail(), user.getId())) {
                throw new ValidationException("Email already exists");
            }
            user.setEmail(Email.of(cmd.getEmail()));
        }
        if (cmd.getRole() != null) user.setRole(Role.valueOf(cmd.getRole()));
        user.setUpdatedAt(Instant.now());
        return toResult(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        userRepository.deleteById(id);
    }

    public UserResult activate(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.activate();
        return toResult(userRepository.save(user));
    }

    public UserResult deactivate(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.disable();
        return toResult(userRepository.save(user));
    }

    public UserResult assignRole(Long id, String role) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setRole(Role.valueOf(role));
        return toResult(userRepository.save(user));
    }

    private UserResult toResult(User u) {
        return UserResult.builder()
                .id(u.getId()).username(u.getUsername())
                .email(u.getEmail() != null ? u.getEmail().getValue() : null)
                .role(u.getRole().name()).status(u.getStatus().name())
                .failedLoginAttempts(u.getFailedLoginAttempts())
                .lastLoginAt(u.getLastLoginAt()).createdAt(u.getCreatedAt())
                .build();
    }
}
