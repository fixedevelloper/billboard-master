-- billboard_images.url stockait une URL présignée MinIO générée une seule fois à l'upload
-- (voir MinioStorageServiceImpl.generatePublicUrl, expiry 1h) : passé ce délai, la photo ne
-- s'affiche plus jamais côté frontend. On référence désormais le fichier stocké (stored_files)
-- et on régénère l'URL présignée à chaque lecture (StorageService.getFilePresignedUrl), comme
-- le fait déjà le module storage pour les fichiers propriétaire.
DELETE FROM billboard_images;

ALTER TABLE billboard_images
    DROP COLUMN url,
    ADD COLUMN file_id BINARY(16) NOT NULL AFTER billboard_id,
    ADD CONSTRAINT fk_billboard_images_file FOREIGN KEY (file_id) REFERENCES stored_files (id);
