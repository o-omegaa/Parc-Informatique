-- V5: Add must_change_password flag for temporary password flow
ALTER TABLE users
    ADD COLUMN must_change_password TINYINT(1) NOT NULL DEFAULT 0
        COMMENT 'Forces password change on next login (set when admin creates account or sends temp password)';
