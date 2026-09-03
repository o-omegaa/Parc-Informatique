package com.supplierportal.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DemandeJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "appel_offre_id", nullable = false)
    private Long appelOffreId;
    @Column(name = "fournisseur_user_id", nullable = false)
    private Long fournisseurUserId;
    @Column(name = "proposition_technique", nullable = false, columnDefinition = "TEXT")
    private String propositionTechnique;
    @Column(name = "montant_propose", precision = 15, scale = 2)
    private BigDecimal montantPropose;
    @Column(name = "date_submission")
    private LocalDateTime dateSubmission;
    @Column(nullable = false, length = 30)
    private String status;
    @Column(name = "commentaire_service_achat", columnDefinition = "TEXT")
    private String commentaireServiceAchat;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
