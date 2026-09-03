package com.supplierportal.infrastructure.web.controller;

import com.supplierportal.application.document.command.UploadDocumentCommand;
import com.supplierportal.application.document.result.DocumentResult;
import com.supplierportal.application.document.service.DocumentService;
import com.supplierportal.application.audit.service.AuditService;
import com.supplierportal.domain.audit.AuditActionType;
import com.supplierportal.domain.user.Role;
import com.supplierportal.domain.user.User;
import com.supplierportal.infrastructure.security.CurrentUserResolver;
import com.supplierportal.infrastructure.web.dto.request.*;
import com.supplierportal.infrastructure.web.dto.response.DocumentResponse;
import com.supplierportal.infrastructure.web.dto.response.DocumentComplianceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final CurrentUserResolver currentUserResolver;
    private final AuditService auditService;

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<DocumentResponse> upload(
            @RequestParam("supplierId") Long supplierId,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            Principal principal) throws java.io.IOException {
        Long userId = currentUserResolver.resolveId(principal);
        if (!documentService.isSupplierOwnedByUser(supplierId, userId)) {
            throw new AccessDeniedException("Vous ne pouvez televerser un document que pour votre propre fiche fournisseur");
        }
        String storageKey = "dummy-key-" + file.getOriginalFilename();
        DocumentResult r = documentService.upload(new UploadDocumentCommand(supplierId, documentType, storageKey, expiryDate));
        auditService.record(userId, AuditActionType.DOCUMENT_UPLOADED,
                "SupplierDocument", r.getId(), "Compliance document uploaded");
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(r));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'AUDITOR')")
    public List<DocumentResponse> getAll() {
        return documentService.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}/file")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'SUPPLIER')")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id, Principal principal) throws java.io.IOException {
        User current = currentUserResolver.resolve(principal);
        if (current.getRole() == Role.SUPPLIER && !documentService.isDocumentOwnedByUser(id, current.getId())) {
            throw new AccessDeniedException("Ce document ne vous appartient pas");
        }
        DocumentResult doc = documentService.findById(id);
        byte[] fileBytes = new byte[0]; // dummy
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileReference() + "\"")
                .body(fileBytes);
    }

    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'AUDITOR', 'SUPPLIER')")
    public List<DocumentResponse> getBySupplierId(@PathVariable Long supplierId, Principal principal) {
        checkReadAccess(supplierId, principal);
        return documentService.findBySupplierId(supplierId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/supplier/{supplierId}/compliance")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'AUDITOR', 'SUPPLIER')")
    public DocumentComplianceResponse getCompliance(@PathVariable Long supplierId, Principal principal) {
        checkReadAccess(supplierId, principal);
        var result = documentService.getCompliance(supplierId);
        return new DocumentComplianceResponse(result.supplierId(), result.compliant(),
                result.missingDocumentTypes(), result.pendingReviewDocumentTypes(),
                result.rejectedDocumentTypes(), result.expiredDocumentTypes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER', 'AUDITOR', 'SUPPLIER')")
    public DocumentResponse getById(@PathVariable Long id, Principal principal) {
        User current = currentUserResolver.resolve(principal);
        if (current.getRole() == Role.SUPPLIER && !documentService.isDocumentOwnedByUser(id, current.getId())) {
            throw new AccessDeniedException("Ce document ne vous appartient pas");
        }
        return toResponse(documentService.findById(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public DocumentResponse approve(@PathVariable Long id, @RequestBody ReviewDocumentRequest req, Principal principal) {
        Long reviewerId = currentUserResolver.resolveId(principal);
        DocumentResponse response = toResponse(documentService.approve(id, reviewerId, req.getComment()));
        auditService.record(reviewerId, AuditActionType.DOCUMENT_APPROVED,
                "SupplierDocument", id, "Document approved");
        return response;
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public DocumentResponse reject(@PathVariable Long id, @RequestBody ReviewDocumentRequest req, Principal principal) {
        Long reviewerId = currentUserResolver.resolveId(principal);
        DocumentResponse response = toResponse(documentService.reject(id, reviewerId, req.getComment()));
        auditService.record(reviewerId, AuditActionType.DOCUMENT_REJECTED,
                "SupplierDocument", id, "Document rejected");
        return response;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        DocumentResult document = documentService.findById(id);
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_OFFICER')")
    public List<DocumentResponse> getByStatus(@PathVariable String status) {
        return documentService.findByStatus(status).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void checkReadAccess(Long supplierId, Principal principal) {
        User current = currentUserResolver.resolve(principal);
        if (current.getRole() == Role.SUPPLIER && !documentService.isSupplierOwnedByUser(supplierId, current.getId())) {
            throw new AccessDeniedException("Ces documents ne vous appartiennent pas");
        }
    }

    private DocumentResponse toResponse(DocumentResult r) {
        return DocumentResponse.builder()
                .id(r.getId()).supplierId(r.getSupplierId())
                .documentType(r.getDocumentType()).fileReference(r.getFileReference())
                .uploadedAt(r.getUploadedAt() != null ? r.getUploadedAt().toString() : null)
                .expiryDate(r.getExpiryDate() != null ? r.getExpiryDate().toString() : null)
                .status(r.getStatus())
                .reviewComment(r.getReviewComment())
                .reviewedAt(r.getReviewedAt() != null ? r.getReviewedAt().toString() : null)
                .build();
    }
}
