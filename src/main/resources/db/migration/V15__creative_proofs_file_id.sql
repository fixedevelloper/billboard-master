-- Même défaut que billboard_images.url (voir V13) : creative_proofs.file_url stockait une URL
-- présignée MinIO permanente, expirant après 1h. On référence désormais stored_files et on
-- régénère l'URL à la lecture.
ALTER TABLE creative_proofs
    DROP COLUMN file_url,
    ADD COLUMN file_id BINARY(16) NOT NULL AFTER version,
    ADD CONSTRAINT fk_creative_proofs_file FOREIGN KEY (file_id) REFERENCES stored_files (id);
