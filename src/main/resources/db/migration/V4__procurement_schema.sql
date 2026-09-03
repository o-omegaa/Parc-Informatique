-- ============================================================
-- V4: Portail Procurement - Nouveau schema ONEE Branche Eau
-- ============================================================

-- Supprimer les anciennes tables (ordre pour FK)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS product_reviews;
DROP TABLE IF EXISTS product_images;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS supplier_follows;
DROP TABLE IF EXISTS evaluations;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS password_reset_tokens;
SET FOREIGN_KEY_CHECKS = 1;

-- Mettre a jour les roles
UPDATE users SET role = 'SERVICE_ACHAT' WHERE role = 'PROCUREMENT_OFFICER';
UPDATE users SET role = 'SUPPLIER' WHERE role = 'AUDITOR';

-- Les fournisseurs s'inscrivent avec statut DISABLED jusqu'a validation admin
-- La table users supporte deja DISABLED

-- Table appel_offres
CREATE TABLE IF NOT EXISTS appel_offres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(500) NOT NULL,
    description TEXT NOT NULL,
    categorie VARCHAR(50) NOT NULL,
    budget_estime DECIMAL(15,2) NULL,
    date_publication DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_cloture DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'BROUILLON',
    published_by_user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (published_by_user_id) REFERENCES users(id),
    INDEX idx_ao_status (status),
    INDEX idx_ao_categorie (categorie),
    INDEX idx_ao_date_cloture (date_cloture)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table demandes (candidatures fournisseurs)
CREATE TABLE IF NOT EXISTS demandes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appel_offre_id BIGINT NOT NULL,
    fournisseur_user_id BIGINT NOT NULL,
    proposition_technique TEXT NOT NULL,
    montant_propose DECIMAL(15,2) NULL,
    date_submission DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) NOT NULL DEFAULT 'SOUMISE',
    commentaire_service_achat TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (appel_offre_id) REFERENCES appel_offres(id),
    FOREIGN KEY (fournisseur_user_id) REFERENCES users(id),
    INDEX idx_demandes_status (status),
    INDEX idx_demandes_fournisseur (fournisseur_user_id),
    UNIQUE KEY uk_demande_ao_fournisseur (appel_offre_id, fournisseur_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table factures
CREATE TABLE IF NOT EXISTS factures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    demande_id BIGINT NOT NULL,
    fournisseur_user_id BIGINT NOT NULL,
    reference_facture VARCHAR(100) NOT NULL,
    montant DECIMAL(15,2) NOT NULL,
    date_emission DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_echeance DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    commentaire TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (demande_id) REFERENCES demandes(id),
    FOREIGN KEY (fournisseur_user_id) REFERENCES users(id),
    INDEX idx_factures_status (status),
    INDEX idx_factures_fournisseur (fournisseur_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
