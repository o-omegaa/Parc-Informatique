package com.supplierportal.infrastructure.web.dto.request;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UploadDocumentRequest {
    private Long supplierId;
    private String documentType;
    private String fileReference;
    private String expiryDate;
}
