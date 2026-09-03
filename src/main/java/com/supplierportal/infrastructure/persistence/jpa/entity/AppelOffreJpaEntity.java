package com.supplierportal.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "appel_offres")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppelOffreJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 500)
    private String titre;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, length = 50)
    private String categorie;
    @Column(name = "budget_estime", precision = 15, scale = 2)
    private BigDecimal budgetEstime;
    @Column(name = "date_publication")
    private LocalDateTime datePublication;
    @Column(name = "date_cloture", nullable = false)
    private LocalDateTime dateCloture;
    @Column(nullable = false, length = 30)
    private String status;
    @Column(name = "published_by_user_id", nullable = false)
    private Long publishedByUserId;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
