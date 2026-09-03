package com.supplierportal.infrastructure.persistence.mapper;

import com.supplierportal.domain.demande.*;
import com.supplierportal.infrastructure.persistence.jpa.entity.DemandeJpaEntity;

public class DemandeMapper {
    public static Demande toDomain(DemandeJpaEntity e) {
        if (e == null) return null;
        return Demande.builder()
                .id(e.getId())
                .appelOffreId(e.getAppelOffreId())
                .fournisseurUserId(e.getFournisseurUserId())
                .propositionTechnique(e.getPropositionTechnique())
                .montantPropose(e.getMontantPropose())
                .dateSubmission(e.getDateSubmission())
                .status(DemandeStatus.valueOf(e.getStatus()))
                .commentaireServiceAchat(e.getCommentaireServiceAchat())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
    public static DemandeJpaEntity toEntity(Demande d) {
        if (d == null) return null;
        return DemandeJpaEntity.builder()
                .id(d.getId())
                .appelOffreId(d.getAppelOffreId())
                .fournisseurUserId(d.getFournisseurUserId())
                .propositionTechnique(d.getPropositionTechnique())
                .montantPropose(d.getMontantPropose())
                .dateSubmission(d.getDateSubmission())
                .status(d.getStatus().name())
                .commentaireServiceAchat(d.getCommentaireServiceAchat())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
