package com.supplierportal.domain.appeloffre;

import com.supplierportal.domain.shared.exception.InvalidStateTransitionException;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppelOffre {
    private Long id;
    private String titre;
    private String description;
    private AppelOffreCategorie categorie;
    private BigDecimal budgetEstime;
    private LocalDateTime datePublication;
    private LocalDateTime dateCloture;
    private AppelOffreStatus status;
    private Long publishedByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void publier() {
        if (this.status != AppelOffreStatus.BROUILLON) {
            throw new InvalidStateTransitionException("Seul un appel d'offre en BROUILLON peut etre publie.");
        }
        this.status = AppelOffreStatus.PUBLIE;
        this.datePublication = LocalDateTime.now();
    }

    public void clore() {
        if (this.status != AppelOffreStatus.PUBLIE) {
            throw new InvalidStateTransitionException("Seul un appel d'offre PUBLIE peut etre clos.");
        }
        this.status = AppelOffreStatus.CLOS;
    }

    public void annuler() {
        if (this.status == AppelOffreStatus.CLOS || this.status == AppelOffreStatus.ANNULE) {
            throw new InvalidStateTransitionException("Cet appel d'offre ne peut pas etre annule.");
        }
        this.status = AppelOffreStatus.ANNULE;
    }

    public boolean isOuvert() {
        return this.status == AppelOffreStatus.PUBLIE &&
               LocalDateTime.now().isBefore(this.dateCloture);
    }
}
