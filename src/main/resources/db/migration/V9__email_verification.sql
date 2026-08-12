ALTER TABLE users
    ADD COLUMN verification_token            VARCHAR(255) NULL,
    ADD COLUMN verification_token_expires_at DATETIME(6)  NULL;

CREATE INDEX idx_users_verification_token ON users (verification_token);
