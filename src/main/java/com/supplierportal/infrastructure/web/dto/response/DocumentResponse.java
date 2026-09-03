package com.supplierportal.infrastructure.web.dto.response;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentResponse {
    private Long id;
    private Long supplierId;
    private String documentType;
    private String fileReference;
    private String uploadedAt;
    private String expiryDate;
    private String status;
    private String reviewComment;
    private String reviewedAt;
}
