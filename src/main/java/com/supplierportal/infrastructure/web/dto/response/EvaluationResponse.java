package com.supplierportal.infrastructure.web.dto.response;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EvaluationResponse {
    private Long id;
    private Long supplierId;
    private Long evaluatorUserId;
    private int overallScore;
    private String comment;
    private String evaluationDate;
    private String createdAt;
}
