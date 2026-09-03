package com.supplierportal.application.document.command;
import lombok.*;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UploadDocumentCommand {
    private Long supplierId;
    private String documentType;
    private String fileReference;
    private String expiryDate; // yyyy-MM-dd or null
}
