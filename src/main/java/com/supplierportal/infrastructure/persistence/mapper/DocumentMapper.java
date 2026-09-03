package com.supplierportal.infrastructure.persistence.mapper;
import com.supplierportal.domain.document.*;
import com.supplierportal.infrastructure.persistence.jpa.entity.DocumentJpaEntity;
import org.springframework.stereotype.Component;
import java.time.*;

@Component
public class DocumentMapper {
    public SupplierDocument toDomain(DocumentJpaEntity e) {
        if (e == null) return null;
        return SupplierDocument.builder()
                .id(e.getId()).supplierId(e.getSupplierId())
                .documentType(DocumentType.valueOf(e.getDocumentType()))
                .fileReference(e.getFileReference())
                .uploadedAt(e.getUploadedAt() != null ? e.getUploadedAt().toInstant(ZoneOffset.UTC) : null)
                .expiryDate(e.getExpiryDate())
                .status(DocumentStatus.valueOf(e.getStatus()))
                .reviewedByUserId(e.getReviewedByUserId())
                .reviewComment(e.getReviewComment())
                .reviewedAt(e.getReviewedAt() != null ? e.getReviewedAt().toInstant(ZoneOffset.UTC) : null)
                .build();
    }
    public DocumentJpaEntity toJpa(SupplierDocument d) {
        if (d == null) return null;
        return DocumentJpaEntity.builder()
                .id(d.getId()).supplierId(d.getSupplierId())
                .documentType(d.getDocumentType().name())
                .fileReference(d.getFileReference())
                .uploadedAt(d.getUploadedAt() != null ? LocalDateTime.ofInstant(d.getUploadedAt(), ZoneOffset.UTC) : null)
                .expiryDate(d.getExpiryDate())
                .status(d.getStatus().name())
                .reviewedByUserId(d.getReviewedByUserId())
                .reviewComment(d.getReviewComment())
                .reviewedAt(d.getReviewedAt() != null ? LocalDateTime.ofInstant(d.getReviewedAt(), ZoneOffset.UTC) : null)
                .build();
    }
}
