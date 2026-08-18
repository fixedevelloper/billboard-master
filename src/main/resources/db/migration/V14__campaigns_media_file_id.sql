-- campaigns.media_url avait le même défaut que billboard_images.url (voir V13) : une URL
-- présignée MinIO générée côté client et stockée telle quelle, expirant après 1h. On référence
-- désormais le fichier stocké (stored_files) et on régénère l'URL à la lecture.
ALTER TABLE campaigns
    DROP COLUMN media_url,
    ADD COLUMN media_file_id BINARY(16) NULL AFTER description,
    ADD CONSTRAINT fk_campaigns_media_file FOREIGN KEY (media_file_id) REFERENCES stored_files (id);
