-- Panneaux (billboard_images.url) avait déjà VARCHAR(2000) pour les URLs présignées MinIO
-- (voir V3), mais campaigns.media_url, creative_proofs.file_url et
-- installation_tasks.proof_photo_url stockent le même type d'URL et étaient restées à la
-- taille par défaut VARCHAR(255), trop courte : "Data truncation: Data too long for column".

ALTER TABLE campaigns
    MODIFY media_url VARCHAR(2000) NULL;

ALTER TABLE creative_proofs
    MODIFY file_url VARCHAR(2000) NOT NULL;

ALTER TABLE installation_tasks
    MODIFY proof_photo_url VARCHAR(2000) NULL;
