package com.supplierportal.infrastructure.web.controller;

import com.supplierportal.application.supplier.command.UpdateSupplierCommand;
import com.supplierportal.application.supplier.command.CreateSupplierCommand;
import com.supplierportal.application.supplier.result.SupplierResult;
import com.supplierportal.application.supplier.service.SupplierService;
import com.supplierportal.application.audit.service.AuditService;
import com.supplierportal.domain.audit.AuditActionType;
import com.supplierportal.domain.user.Role;
import com.supplierportal.domain.user.User;
import com.supplierportal.infrastructure.security.CurrentUserResolver;
import com.supplierportal.infrastructure.web.dto.request.RejectSupplierRequest;
import com.supplierportal.infrastructure.web.dto.request.CreateSupplierRequest;
import com.supplierportal.infrastructure.web.dto.request.UpdateSupplierRequest;
import com.supplierportal.infrastructure.web.dto.response.SupplierResponse;
import com.supplierportal.infrastructure.web.dto.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;
    private final CurrentUserResolver currentUserResolver;
    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'AUDITOR')")
    public List<SupplierResponse> getAllSuppliers() {
        return supplierService.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupplierResponse> createSupplier(@jakarta.validation.Valid @RequestBody CreateSupplierRequest request,
                                                           Principal principal) {
        SupplierResult result = supplierService.create(new CreateSupplierCommand(
                request.getRegisteredByUserId(), request.getCompanyName(), request.getIce(), request.getCategory(),
                request.getAddress(), request.getContactPerson(), request.getContactEmail(), request.getContactPhone()));
        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.SUPPLIER_REGISTERED,
                "Supplier", result.getId(), "Supplier profile created by administrator");
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('SUPPLIER')")
    public SupplierResponse getMySupplierProfile(Principal principal) {
        return toResponse(supplierService.findByUserId(currentUserResolver.resolveId(principal)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'AUDITOR', 'SUPPLIER')")
    public SupplierResponse getSupplierById(@PathVariable Long id, Principal principal) {
        checkOwnershipIfSupplier(id, principal);
        return toResponse(supplierService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLIER')")
    public SupplierResponse updateSupplier(@PathVariable Long id, @RequestBody UpdateSupplierRequest request, Principal principal) {
        User current = currentUserResolver.resolve(principal);
        if (current.getRole() == Role.SUPPLIER) {
            if (!supplierService.isOwnedByUser(id, current.getId())) {
                throw new AccessDeniedException("Cette fiche fournisseur ne vous appartient pas");
            }
            SupplierResult existing = supplierService.findById(id);
            if (!"DRAFT".equals(existing.getStatus())) {
                throw new AccessDeniedException("Le profil ne peut plus etre modifie apres soumission");
            }
        }
        UpdateSupplierCommand command = new UpdateSupplierCommand(
                id, request.getCompanyName(), request.getAddress(),
                request.getContactPerson(), request.getContactEmail(), request.getContactPhone()
        );
        return toResponse(supplierService.update(command));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('SUPPLIER')")
    public SupplierResponse submitSupplier(@PathVariable Long id, Principal principal) {
        checkOwnershipIfSupplier(id, principal);
        SupplierResponse response = toResponse(supplierService.submit(id));
        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.SUPPLIER_SUBMITTED,
                "Supplier", id, "Supplier submitted for validation");
        return response;
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public SupplierResponse validateSupplier(@PathVariable Long id, Principal principal) {
        SupplierResponse response = toResponse(supplierService.validate(id));
        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.SUPPLIER_VALIDATED,
                "Supplier", id, "Supplier validated");
        return response;
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public SupplierResponse rejectSupplier(@PathVariable Long id, @RequestBody RejectSupplierRequest request, Principal principal) {
        SupplierResponse response = toResponse(supplierService.reject(id, request.getReason()));
        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.SUPPLIER_REJECTED,
                "Supplier", id, "Supplier rejected");
        return response;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public SupplierResponse suspendSupplier(@PathVariable Long id, @RequestBody RejectSupplierRequest request, Principal principal) {
        SupplierResponse response = toResponse(supplierService.suspend(id, request.getReason()));
        auditService.record(currentUserResolver.resolveId(principal), AuditActionType.SUPPLIER_SUSPENDED,
                "Supplier", id, "Supplier suspended");
        return response;
    }

    @PostMapping("/{id}/reinstate")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public SupplierResponse reinstateSupplier(@PathVariable Long id) {
        SupplierResponse response = toResponse(supplierService.reinstate(id));
        return response;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'AUDITOR')")
    public List<SupplierResponse> searchSuppliers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        return supplierService.search(query, status, category).stream()
                .map(this::toResponse).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/search/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'AUDITOR')")
    public PagedResponse<SupplierResponse> searchSuppliersPaged(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "companyName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        List<SupplierResponse> results = supplierService.search(query, status, category).stream()
                .map(this::toResponse)
                .sorted(supplierComparator(sort, direction))
                .collect(Collectors.toList());
        int from = Math.min(safePage * safeSize, results.size());
        int to = Math.min(from + safeSize, results.size());
        int totalPages = Math.max(1, (int) Math.ceil((double) results.size() / safeSize));
        return new PagedResponse<>(results.subList(from, to), safePage, safeSize, results.size(), totalPages);
    }

    private Comparator<SupplierResponse> supplierComparator(String sort, String direction) {
        Comparator<SupplierResponse> comparator = switch (sort) {
            case "createdAt" -> Comparator.comparing(SupplierResponse::getCreatedAt,
                    Comparator.nullsLast(String::compareTo));
            case "status" -> Comparator.comparing(SupplierResponse::getStatus,
                    Comparator.nullsLast(String::compareTo));
            case "category" -> Comparator.comparing(SupplierResponse::getCategory,
                    Comparator.nullsLast(String::compareTo));
            default -> Comparator.comparing(SupplierResponse::getCompanyName,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        };
        return "desc".equalsIgnoreCase(direction) ? comparator.reversed() : comparator;
    }

    private void checkOwnershipIfSupplier(Long supplierId, Principal principal) {
        User current = currentUserResolver.resolve(principal);
        if (current.getRole() == Role.SUPPLIER && !supplierService.isOwnedByUser(supplierId, current.getId())) {
            throw new AccessDeniedException("Cette fiche fournisseur ne vous appartient pas");
        }
    }

    private SupplierResponse toResponse(SupplierResult result) {
        return SupplierResponse.builder()
                .id(result.getId())
                .companyName(result.getCompanyName())
                .ice(result.getIce())
                .commercialRegister(result.getCommercialRegister())
                .taxIdentifier(result.getTaxIdentifier())
                .address(result.getAddress())
                .contactPerson(result.getContactPerson())
                .contactEmail(result.getContactEmail())
                .contactPhone(result.getContactPhone())
                .category(result.getCategory())
                .status(result.getStatus())
                .registeredByUserId(result.getRegisteredByUserId())
                .createdAt(result.getCreatedAt() != null ? result.getCreatedAt().toString() : null)
                .build();
    }
}
