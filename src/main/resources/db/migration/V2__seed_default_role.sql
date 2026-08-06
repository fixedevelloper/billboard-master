-- Rôle par défaut assigné à l'inscription (RegistrationService.DEFAULT_ROLE_NAME).
-- UUID_TO_BIN(UUID()) sans swap_flag conserve l'ordre canonique RFC 4122, identique
-- à l'encodage BINARY(16) que Hibernate produit pour un champ java.util.UUID.
INSERT INTO roles (id, name)
VALUES (UUID_TO_BIN(UUID()), 'USER');
