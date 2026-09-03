package com.supplierportal.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FactureJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "demande_id", nullable = false)
    private Long demandeId;
    @Column(name = "fournisseur_user_id", nullable = false)
    private Long fournisseurUserId;
    @Column(name = "reference_facture", nullable = false, length = 100)
    private String referenceFacture;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;
    @Column(name = "date_emission")
    private LocalDateTime dateEmission;
    @Column(name = "date_echeance", nullable = false)
    private LocalDateTime dateEcheance;
    @Column(nullable = false, length = 30)
    private String status;
    @Column(columnDefinition = "TEXT")
    private String commentaire;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
