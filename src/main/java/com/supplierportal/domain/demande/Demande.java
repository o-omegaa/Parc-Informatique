package com.supplierportal.domain.demande;

import com.supplierportal.domain.shared.exception.InvalidStateTransitionException;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Demande {
    private Long id;
    private Long appelOffreId;
    private Long fournisseurUserId;
    private String propositionTechnique;
    private BigDecimal montantPropose;
    private LocalDateTime dateSubmission;
    private DemandeStatus status;
    private String commentaireServiceAchat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void accepter(String commentaire) {
        if (this.status == DemandeStatus.ACCEPTEE || this.status == DemandeStatus.REJETEE) {
            throw new InvalidStateTransitionException("La demande ne peut pas etre acceptee dans son etat actuel.");
        }
        this.status = DemandeStatus.ACCEPTEE;
        this.commentaireServiceAchat = commentaire;
    }

    public void rejeter(String commentaire) {
        if (this.status == DemandeStatus.ACCEPTEE || this.status == DemandeStatus.REJETEE) {
            throw new InvalidStateTransitionException("La demande ne peut pas etre rejetee dans son etat actuel.");
        }
        this.status = DemandeStatus.REJETEE;
        this.commentaireServiceAchat = commentaire;
    }
}
