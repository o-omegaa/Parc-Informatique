package com.supplierportal.infrastructure.persistence.mapper;

import com.supplierportal.domain.facture.*;
import com.supplierportal.infrastructure.persistence.jpa.entity.FactureJpaEntity;

public class FactureMapper {
    public static Facture toDomain(FactureJpaEntity e) {
        if (e == null) return null;
        return Facture.builder()
                .id(e.getId()).demandeId(e.getDemandeId()).fournisseurUserId(e.getFournisseurUserId())
                .referenceFacture(e.getReferenceFacture()).montant(e.getMontant())
                .dateEmission(e.getDateEmission()).dateEcheance(e.getDateEcheance())
                .status(FactureStatus.valueOf(e.getStatus())).commentaire(e.getCommentaire())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }
    public static FactureJpaEntity toEntity(Facture d) {
        if (d == null) return null;
        return FactureJpaEntity.builder()
                .id(d.getId()).demandeId(d.getDemandeId()).fournisseurUserId(d.getFournisseurUserId())
                .referenceFacture(d.getReferenceFacture()).montant(d.getMontant())
                .dateEmission(d.getDateEmission()).dateEcheance(d.getDateEcheance())
                .status(d.getStatus().name()).commentaire(d.getCommentaire())
                .createdAt(d.getCreatedAt()).updatedAt(d.getUpdatedAt()).build();
    }
}
