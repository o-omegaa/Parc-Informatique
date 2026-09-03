package com.supplierportal.infrastructure.persistence.jpa.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "supplier_documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "supplier_id", nullable = false) private Long supplierId;
    @Column(name = "document_type", nullable = false) private String documentType;
    @Column(name = "file_reference", nullable = false) private String fileReference;
    @Column(name = "uploaded_at") private LocalDateTime uploadedAt;
    @Column(name = "expiry_date") private LocalDate expiryDate;
    @Column(nullable = false) private String status;
    @Column(name = "reviewed_by_user_id") private Long reviewedByUserId;
    @Column(name = "review_comment") private String reviewComment;
    @Column(name = "reviewed_at") private LocalDateTime reviewedAt;
}
