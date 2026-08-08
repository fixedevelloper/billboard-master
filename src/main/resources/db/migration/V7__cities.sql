CREATE TABLE cities
(
    id         BINARY(16)     NOT NULL,
    name       VARCHAR(255)   NOT NULL,
    country    VARCHAR(255)   NULL,
    latitude   DOUBLE         NOT NULL,
    longitude  DOUBLE         NOT NULL,
    created_at DATETIME(6)    NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX uk_cities_name (name)
);

INSERT INTO cities (id, name, country, latitude, longitude, created_at) VALUES
    (UUID_TO_BIN(UUID()), 'Paris', 'France', 48.8566, 2.3522, NOW()),
    (UUID_TO_BIN(UUID()), 'Marseille', 'France', 43.2965, 5.3698, NOW()),
    (UUID_TO_BIN(UUID()), 'Lyon', 'France', 45.7640, 4.8357, NOW()),
    (UUID_TO_BIN(UUID()), 'Toulouse', 'France', 43.6047, 1.4442, NOW()),
    (UUID_TO_BIN(UUID()), 'Nice', 'France', 43.7102, 7.2620, NOW()),
    (UUID_TO_BIN(UUID()), 'Bordeaux', 'France', 44.8378, -0.5792, NOW()),
    (UUID_TO_BIN(UUID()), 'Lille', 'France', 50.6292, 3.0573, NOW()),
    (UUID_TO_BIN(UUID()), 'Strasbourg', 'France', 48.5734, 7.7521, NOW()),
    (UUID_TO_BIN(UUID()), 'Douala', 'Cameroun', 4.0511, 9.7679, NOW()),
    (UUID_TO_BIN(UUID()), 'Yaoundé', 'Cameroun', 3.8480, 11.5021, NOW()),
    (UUID_TO_BIN(UUID()), 'Abidjan', 'Côte d''Ivoire', 5.3600, -4.0083, NOW()),
    (UUID_TO_BIN(UUID()), 'Yamoussoukro', 'Côte d''Ivoire', 6.8276, -5.2893, NOW()),
    (UUID_TO_BIN(UUID()), 'Dakar', 'Sénégal', 14.7167, -17.4677, NOW()),
    (UUID_TO_BIN(UUID()), 'Cotonou', 'Bénin', 6.3703, 2.3912, NOW()),
    (UUID_TO_BIN(UUID()), 'Lomé', 'Togo', 6.1725, 1.2314, NOW()),
    (UUID_TO_BIN(UUID()), 'Kinshasa', 'RD Congo', -4.4419, 15.2663, NOW()),
    (UUID_TO_BIN(UUID()), 'Libreville', 'Gabon', 0.4162, 9.4673, NOW()),
    (UUID_TO_BIN(UUID()), 'Bamako', 'Mali', 12.6392, -8.0029, NOW()),
    (UUID_TO_BIN(UUID()), 'Ouagadougou', 'Burkina Faso', 12.3714, -1.5197, NOW()),
    (UUID_TO_BIN(UUID()), 'Conakry', 'Guinée', 9.6412, -13.5784, NOW());
