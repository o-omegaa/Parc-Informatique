CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    failed_login_attempts INT NOT NULL DEFAULT 0,
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    ice VARCHAR(15) NOT NULL UNIQUE,
    commercial_register VARCHAR(50) NULL,
    tax_identifier VARCHAR(50) NULL,
    address VARCHAR(500) NULL,
    contact_person VARCHAR(255) NULL,
    contact_email VARCHAR(255) NULL,
    contact_phone VARCHAR(30) NULL,
    category VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    registered_by_user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (registered_by_user_id) REFERENCES users(id),
    INDEX idx_suppliers_status (status),
    INDEX idx_suppliers_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE supplier_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    document_type VARCHAR(40) NOT NULL,
    file_reference VARCHAR(255) NOT NULL,
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiry_date DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_REVIEW',
    reviewed_by_user_id BIGINT NULL,
    review_comment VARCHAR(1000) NULL,
    reviewed_at DATETIME NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (reviewed_by_user_id) REFERENCES users(id),
    INDEX idx_documents_status (status),
    INDEX idx_documents_expiry (status, expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE supplier_evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    evaluator_user_id BIGINT NOT NULL,
    evaluation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    overall_score INT NOT NULL,
    comment VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (evaluator_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE evaluation_criterion_scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evaluation_id BIGINT NOT NULL,
    criterion VARCHAR(30) NOT NULL,
    score INT NOT NULL,
    FOREIGN KEY (evaluation_id) REFERENCES supplier_evaluations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_user_id BIGINT NOT NULL,
    type VARCHAR(40) NOT NULL,
    message VARCHAR(500) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actor_user_id BIGINT NULL,
    action_type VARCHAR(50) NOT NULL,
    target_entity_type VARCHAR(50) NULL,
    target_entity_id BIGINT NULL,
    ip_address VARCHAR(45) NULL,
    outcome VARCHAR(10) NOT NULL,
    detail VARCHAR(1000) NULL,
    FOREIGN KEY (actor_user_id) REFERENCES users(id),
    INDEX idx_audit_timestamp (event_timestamp),
    INDEX idx_audit_actor (actor_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    device_info VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_refresh_token_hash (token_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (username, email, password_hash, role, status) VALUES
('admin', 'admin@supplierportal.ma', '$2a$12$LJ3MFl9LoUjJHCjgGKUQYeEq3jHnGLrFPBW.SnGDvPIMfL6oH6qEu', 'ADMIN', 'ACTIVE');
