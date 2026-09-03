package com.supplierportal.infrastructure.web.dto.request;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateEvaluationRequest {
    private Long supplierId;
    private int overallScore;
    private String comment;
}
