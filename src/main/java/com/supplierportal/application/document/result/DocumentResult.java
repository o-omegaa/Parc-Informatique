package com.supplierportal.application.document.result;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class DocumentResult {
    private Long id;
    private Long supplierId;
    private String documentType;
    private String fileReference;
    private Instant uploadedAt;
    private LocalDate expiryDate;
    private String status;
    private String reviewComment;
    private Instant reviewedAt;
}
