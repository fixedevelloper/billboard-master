-- Table dédiée pour les images d'un billboard (plusieurs photos par panneau), plutôt
-- qu'un simple champ image_url unique sur billboards.

CREATE TABLE billboard_images
(
    id           BINARY(16)    NOT NULL,
    billboard_id BINARY(16)    NOT NULL,
    url          VARCHAR(2000) NOT NULL,
    created_at   DATETIME(6)   NULL,
    PRIMARY KEY (id),
    INDEX idx_billboard_images_billboard_id (billboard_id)
);
