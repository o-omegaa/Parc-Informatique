package com.supplierportal.domain.supplier;

/**
 * Catégories d'activité des fournisseurs — Portail ONEE Branche Eau
 */
public enum SupplierCategory {
    // ── Travaux ──────────────────────────────
    TRAVAUX_GENIE_CIVIL,
    TRAVAUX_HYDRAULIQUE,
    TRAVAUX_ELECTRIQUE,
    TRAVAUX_BATIMENT,
    TRAVAUX_ASSAINISSEMENT,

    // ── Fournitures & Équipements ─────────────
    FOURNITURES_ELECTRIQUES,
    FOURNITURES_MECANIQUES,
    FOURNITURES_INFORMATIQUES,
    MATERIAUX_CONSTRUCTION,
    PRODUITS_CHIMIQUES,
    EQUIPEMENTS_POMPAGE,
    EQUIPEMENTS_MESURE,
    TUYAUTERIE_ROBINETTERIE,

    // ── Services ─────────────────────────────
    SERVICES_INGENIERIE,
    SERVICES_MAINTENANCE,
    SERVICES_INFORMATIQUES,
    SERVICES_FORMATION,
    ETUDES_TOPOGRAPHIE,
    CONTROLE_QUALITE,
    TRANSPORT_LOGISTIQUE,

    // ── Général ──────────────────────────────
    MIXTE,
    AUTRE
}
