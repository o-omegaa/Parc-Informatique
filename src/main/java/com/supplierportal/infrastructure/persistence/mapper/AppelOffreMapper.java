package com.supplierportal.infrastructure.persistence.mapper;

import com.supplierportal.domain.appeloffre.*;
import com.supplierportal.infrastructure.persistence.jpa.entity.AppelOffreJpaEntity;

public class AppelOffreMapper {
    public static AppelOffre toDomain(AppelOffreJpaEntity e) {
        if (e == null) return null;
        return AppelOffre.builder()
                .id(e.getId())
                .titre(e.getTitre())
                .description(e.getDescription())
                .categorie(AppelOffreCategorie.valueOf(e.getCategorie()))
                .budgetEstime(e.getBudgetEstime())
                .datePublication(e.getDatePublication())
                .dateCloture(e.getDateCloture())
                .status(AppelOffreStatus.valueOf(e.getStatus()))
                .publishedByUserId(e.getPublishedByUserId())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
    public static AppelOffreJpaEntity toEntity(AppelOffre d) {
        if (d == null) return null;
        return AppelOffreJpaEntity.builder()
                .id(d.getId())
                .titre(d.getTitre())
                .description(d.getDescription())
                .categorie(d.getCategorie().name())
                .budgetEstime(d.getBudgetEstime())
                .datePublication(d.getDatePublication())
                .dateCloture(d.getDateCloture())
                .status(d.getStatus().name())
                .publishedByUserId(d.getPublishedByUserId())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
