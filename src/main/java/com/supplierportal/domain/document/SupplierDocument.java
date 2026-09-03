package com.supplierportal.domain.document;

import com.supplierportal.domain.shared.exception.InvalidStateTransitionException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDocument {
    private Long id;
    private Long supplierId;
    private DocumentType documentType;
    private String fileReference;
    private Instant uploadedAt;
    private LocalDate expiryDate;
    private DocumentStatus status;
    private Long reviewedByUserId;
    private String reviewComment;
    private Instant reviewedAt;

    public void approve(Long reviewerUserId, String comment) {
        if (this.status != DocumentStatus.PENDING_REVIEW) {
            throw new InvalidStateTransitionException("Only PENDING_REVIEW documents can be approved");
        }
        this.status = DocumentStatus.APPROVED;
        this.reviewedByUserId = reviewerUserId;
        this.reviewComment = comment;
        this.reviewedAt = Instant.now();
    }

    public void reject(Long reviewerUserId, String comment) {
        if (this.status != DocumentStatus.PENDING_REVIEW) {
            throw new InvalidStateTransitionException("Only PENDING_REVIEW documents can be rejected");
        }
        this.status = DocumentStatus.REJECTED;
        this.reviewedByUserId = reviewerUserId;
        this.reviewComment = comment;
        this.reviewedAt = Instant.now();
    }

    public void markExpired() {
        if (this.status != DocumentStatus.APPROVED) {
            throw new InvalidStateTransitionException("Only APPROVED documents can be marked expired");
        }
        this.status = DocumentStatus.EXPIRED;
    }
}
