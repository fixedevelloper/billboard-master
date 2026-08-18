-- Même défaut que billboard_images.url (voir V13) : installation_tasks.proof_photo_url stockait
-- une URL présignée MinIO permanente, expirant après 1h. On référence désormais stored_files et
-- on régénère l'URL à la lecture.
ALTER TABLE installation_tasks
    DROP COLUMN proof_photo_url,
    ADD COLUMN proof_file_id BINARY(16) NULL AFTER status,
    ADD CONSTRAINT fk_installation_tasks_proof_file FOREIGN KEY (proof_file_id) REFERENCES stored_files (id);
