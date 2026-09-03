package com.supplierportal.domain.evaluation;
import lombok.*;
import java.time.Instant;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evaluation {
    private Long id;
    private Long supplierId;
    private Long evaluatorUserId;
    private Instant evaluationDate;
    private int overallScore;
    private String comment;
    private Instant createdAt;
}
