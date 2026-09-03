package com.supplierportal.infrastructure.web.controller;

import com.supplierportal.application.user.command.CreateUserCommand;
import com.supplierportal.application.user.command.UpdateUserCommand;
import com.supplierportal.application.user.result.UserResult;
import com.supplierportal.application.user.service.UserService;
import com.supplierportal.application.audit.service.AuditService;
import com.supplierportal.domain.audit.AuditActionType;
import com.supplierportal.infrastructure.security.CurrentUserResolver;
import com.supplierportal.infrastructure.web.dto.request.*;
import com.supplierportal.infrastructure.web.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;
    private final CurrentUserResolver currentUserResolver;
    private final AuditService auditService;
    private final com.supplierportal.domain.supplier.SupplierRepository supplierRepository;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/pending-validation")
    public ResponseEntity<List<UserResponse>> getPendingValidation() {
        List<UserResponse> users = userService.findAll().stream()
                .filter(u -> "SUPPLIER".equals(u.getRole()) && "DISABLED".equals(u.getStatus()))
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return toResponse(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@jakarta.validation.Valid @RequestBody CreateUserRequest request, Principal principal) {
        UserResult result = userService.create(new CreateUserCommand(request.getUsername(), request.getEmail(), request.getPassword(), request.getRole()));
        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.USER_CREATED,
                "User", result.getId(), "User account created");
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @jakarta.validation.Valid @RequestBody UpdateUserRequest request, Principal principal) {
        UserResponse response = toResponse(userService.update(new UpdateUserCommand(id, request.getUsername(), request.getEmail(), request.getRole())));
        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.USER_UPDATED,
                "User", id, "User account updated");
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Principal principal) {
        if (currentUserResolver.resolveId(principal).equals(id)) {
            throw new com.supplierportal.domain.shared.exception.ValidationException("You cannot delete your own account");
        }
        userService.delete(id);
        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.USER_DELETED,
                "User", id, "User account deleted");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    public UserResponse activateUser(@PathVariable Long id, Principal principal) {
        UserResponse response = toResponse(userService.activate(id));
        
        supplierRepository.findByRegisteredByUserId(id).ifPresent(supplier -> {
            supplier.setStatus(com.supplierportal.domain.supplier.SupplierStatus.PENDING_VALIDATION);
            supplierRepository.save(supplier);
        });

        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.USER_STATUS_CHANGED,
                "User", id, "User account activated");
        return response;
    }

    @PostMapping("/{id}/deactivate")
    public UserResponse deactivateUser(@PathVariable Long id, Principal principal) {
        if (currentUserResolver.resolveId(principal).equals(id)) {
            throw new com.supplierportal.domain.shared.exception.ValidationException("You cannot deactivate your own account");
        }
        UserResponse response = toResponse(userService.deactivate(id));
        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.USER_STATUS_CHANGED,
                "User", id, "User account deactivated");
        return response;
    }

    @PostMapping("/{id}/assign-role")
    public UserResponse assignRole(@PathVariable Long id, @RequestBody AssignRoleRequest request, Principal principal) {
        UserResponse response = toResponse(userService.assignRole(id, request.getRole()));
        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.USER_ROLE_CHANGED,
                "User", id, "User role assigned");
        return response;
    }

    private UserResponse toResponse(UserResult r) {
        return UserResponse.builder()
                .id(r.getId()).username(r.getUsername()).email(r.getEmail())
                .role(r.getRole()).status(r.getStatus())
                .failedLoginAttempts(r.getFailedLoginAttempts())
                .lastLoginAt(r.getLastLoginAt() != null ? r.getLastLoginAt().toString() : null)
                .createdAt(r.getCreatedAt() != null ? r.getCreatedAt().toString() : null)
                .build();
    }
}
