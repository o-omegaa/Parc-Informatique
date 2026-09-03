package com.supplierportal.application.document.service;

import com.supplierportal.application.document.command.UploadDocumentCommand;
import com.supplierportal.application.document.result.DocumentComplianceResult;
import com.supplierportal.application.document.result.DocumentResult;
import com.supplierportal.domain.document.*;
import com.supplierportal.domain.shared.exception.NotFoundException;
import com.supplierportal.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {
    private static final Set<DocumentType> REQUIRED_COMPLIANCE_DOCUMENTS = EnumSet.of(
            DocumentType.RC_EXTRACT,
            DocumentType.TAX_COMPLIANCE_CERTIFICATE,
            DocumentType.CNSS_ATTESTATION,
            DocumentType.ICE_CERTIFICATE,
            DocumentType.BANK_RIB
    );

    private final DocumentRepository documentRepository;
    private final com.supplierportal.domain.supplier.SupplierRepository supplierRepository;

    public DocumentResult upload(UploadDocumentCommand cmd) {
        supplierRepository.findById(cmd.getSupplierId())
                .orElseThrow(() -> new NotFoundException("Supplier not found"));
        DocumentType documentType;
        LocalDate expiryDate = null;
        try {
            documentType = DocumentType.valueOf(cmd.getDocumentType());
            if (cmd.getExpiryDate() != null && !cmd.getExpiryDate().isBlank()) {
                expiryDate = LocalDate.parse(cmd.getExpiryDate());
            }
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Invalid document type or expiry date");
        }
        SupplierDocument doc = SupplierDocument.builder()
                .supplierId(cmd.getSupplierId())
                .documentType(documentType)
                .fileReference(cmd.getFileReference())
                .uploadedAt(Instant.now())
                .expiryDate(expiryDate)
                .status(DocumentStatus.PENDING_REVIEW)
                .build();
        return toResult(documentRepository.save(doc));
    }

    public List<DocumentResult> findBySupplierId(Long supplierId) {
        return documentRepository.findBySupplierId(supplierId).stream().map(this::toResult).collect(Collectors.toList());
    }

    public List<DocumentResult> findAll() {
        return documentRepository.findAll().stream().map(this::toResult).collect(Collectors.toList());
    }

    public DocumentResult findById(Long id) {
        return toResult(documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found")));
    }

    public DocumentResult approve(Long id, Long reviewerUserId, String comment) {
        SupplierDocument doc = documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found"));
        doc.approve(reviewerUserId, comment);
        return toResult(documentRepository.save(doc));
    }

    public DocumentResult reject(Long id, Long reviewerUserId, String comment) {
        SupplierDocument doc = documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found"));
        doc.reject(reviewerUserId, comment);
        return toResult(documentRepository.save(doc));
    }

    public void delete(Long id) {
        documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found"));
        documentRepository.deleteById(id);
    }

    public List<DocumentResult> findByStatus(String status) {
        return documentRepository.findByStatus(DocumentStatus.valueOf(status)).stream().map(this::toResult).collect(Collectors.toList());
    }

    public boolean isSupplierOwnedByUser(Long supplierId, Long userId) {
        return supplierRepository.findById(supplierId)
                .map(s -> userId.equals(s.getRegisteredByUserId()))
                .orElse(false);
    }

    public boolean isDocumentOwnedByUser(Long documentId, Long userId) {
        SupplierDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));
        return isSupplierOwnedByUser(doc.getSupplierId(), userId);
    }

    public DocumentComplianceResult getCompliance(Long supplierId) {
        supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        Map<DocumentType, List<SupplierDocument>> documentsByType = documentRepository.findBySupplierId(supplierId)
                .stream()
                .collect(Collectors.groupingBy(SupplierDocument::getDocumentType));
        LocalDate today = LocalDate.now();
        List<String> missing = new ArrayList<>();
        List<String> pendingReview = new ArrayList<>();
        List<String> rejected = new ArrayList<>();
        List<String> expired = new ArrayList<>();

        for (DocumentType type : REQUIRED_COMPLIANCE_DOCUMENTS) {
            List<SupplierDocument> documents = documentsByType.getOrDefault(type, List.of());
            boolean hasValidDocument = documents.stream().anyMatch(document ->
                    document.getStatus() == DocumentStatus.APPROVED
                            && (document.getExpiryDate() == null || !document.getExpiryDate().isBefore(today)));
            if (hasValidDocument) {
                continue;
            }
            if (documents.stream().anyMatch(document -> document.getStatus() == DocumentStatus.PENDING_REVIEW)) {
                pendingReview.add(type.name());
            } else if (documents.stream().anyMatch(document -> document.getStatus() == DocumentStatus.REJECTED)) {
                rejected.add(type.name());
            } else if (documents.stream().anyMatch(document -> document.getStatus() == DocumentStatus.EXPIRED
                    || (document.getStatus() == DocumentStatus.APPROVED
                    && document.getExpiryDate() != null && document.getExpiryDate().isBefore(today)))) {
                expired.add(type.name());
            } else {
                missing.add(type.name());
            }
        }
        return new DocumentComplianceResult(supplierId,
                missing.isEmpty() && pendingReview.isEmpty() && rejected.isEmpty() && expired.isEmpty(),
                missing, pendingReview, rejected, expired);
    }

    public int expireDocumentsPastDeadline() {
        int expired = 0;
        LocalDate today = LocalDate.now();
        for (SupplierDocument document : documentRepository.findAll()) {
            if (document.getStatus() == DocumentStatus.APPROVED
                    && document.getExpiryDate() != null
                    && document.getExpiryDate().isBefore(today)) {
                document.markExpired();
                documentRepository.save(document);
                expired++;
            }
        }
        return expired;
    }
    

    private DocumentResult toResult(SupplierDocument d) {
        return DocumentResult.builder()
                .id(d.getId()).supplierId(d.getSupplierId())
                .documentType(d.getDocumentType().name())
                .fileReference(d.getFileReference())
                .uploadedAt(d.getUploadedAt()).expiryDate(d.getExpiryDate())
                .status(d.getStatus().name())
                .reviewComment(d.getReviewComment()).reviewedAt(d.getReviewedAt())
                .build();
    }
}
