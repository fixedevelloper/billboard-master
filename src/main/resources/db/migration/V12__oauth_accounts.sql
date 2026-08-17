ALTER TABLE users
    ADD COLUMN oauth_provider VARCHAR(20)  NULL,
    ADD COLUMN oauth_id       VARCHAR(255) NULL;

CREATE UNIQUE INDEX idx_users_oauth_identity ON users (oauth_provider, oauth_id);
