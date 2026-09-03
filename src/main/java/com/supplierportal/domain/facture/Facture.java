package com.supplierportal.domain.facture;

import com.supplierportal.domain.shared.exception.InvalidStateTransitionException;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Facture {
    private Long id;
    private Long demandeId;
    private Long fournisseurUserId;
    private String referenceFacture;
    private BigDecimal montant;
    private LocalDateTime dateEmission;
    private LocalDateTime dateEcheance;
    private FactureStatus status;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void valider(String commentaire) {
        if (this.status != FactureStatus.EN_ATTENTE) {
            throw new InvalidStateTransitionException("Seule une facture EN_ATTENTE peut etre validee.");
        }
        this.status = FactureStatus.VALIDEE;
        this.commentaire = commentaire;
    }

    public void marquerPayee() {
        if (this.status != FactureStatus.VALIDEE) {
            throw new InvalidStateTransitionException("Seule une facture VALIDEE peut etre marquee payee.");
        }
        this.status = FactureStatus.PAYEE;
    }

    public void rejeter(String commentaire) {
        if (this.status != FactureStatus.EN_ATTENTE) {
            throw new InvalidStateTransitionException("Seule une facture EN_ATTENTE peut etre rejetee.");
        }
        this.status = FactureStatus.REJETEE;
        this.commentaire = commentaire;
    }
}
