-- Schéma initial, dérivé des entités JPA de chaque module.
--
-- Conventions (alignées sur le mapping par défaut de Hibernate 7 / MySQLDialect pour ce projet) :
--   * UUID (id et colonnes de référence type "xxxId")   -> BINARY(16), assignés côté application
--     (aucune entité n'utilise @GeneratedValue ; confirmé aussi par le mapping interne
--     de Spring Modulith lui-même : JpaEventPublication#id est @Column(length = 16)).
--   * enum @Enumerated(STRING)                          -> VARCHAR(30) (le plus long libellé fait 20 car.)
--   * LocalDateTime                                      -> DATETIME(6)
--   * LocalDate                                          -> DATE
--   * boolean                                            -> BOOLEAN (alias MySQL de TINYINT(1))
--   * BigDecimal (montants/taux)                         -> DECIMAL(19,4)
--   * NOT NULL / UNIQUE uniquement là où l'entité le déclare explicitement.
--
-- Aucune contrainte FK n'est posée entre tables de modules différents : le code ne modélise
-- ces relations qu'en UUID nus (pas de @ManyToOne cross-module), conformément aux frontières
-- de module Spring Modulith. Des index simples sont ajoutés sur ces colonnes pour les requêtes.

-- =========================================================================
-- Module user : permissions, rôles, utilisateurs
-- =========================================================================

CREATE TABLE permissions
(
    id   BINARY(16)   NOT NULL,
    name VARCHAR(255) NULL,
    PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id   BINARY(16)   NOT NULL,
    name VARCHAR(255) NULL,
    PRIMARY KEY (id)
);

CREATE TABLE role_permissions
(
    role_id       BINARY(16) NOT NULL,
    permission_id BINARY(16) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    INDEX idx_role_permissions_permission_id (permission_id)
);

CREATE TABLE users
(
    id           BINARY(16)   NOT NULL,
    email        VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    first_name   VARCHAR(255) NULL,
    last_name    VARCHAR(255) NULL,
    phone_number VARCHAR(255) NULL,
    status       VARCHAR(30)  NULL,
    created_at   DATETIME(6)  NULL,
    updated_at   DATETIME(6)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
);

CREATE TABLE user_roles
(
    user_id BINARY(16) NOT NULL,
    role_id BINARY(16) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    INDEX idx_user_roles_role_id (role_id)
);

-- =========================================================================
-- Module admin
-- =========================================================================

CREATE TABLE admin_users
(
    id         BINARY(16)  NOT NULL,
    user_id    BINARY(16)  NOT NULL,
    active     BOOLEAN     NOT NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_admin_users_user_id (user_id)
);

CREATE TABLE admin_roles
(
    admin_id BINARY(16)  NOT NULL,
    role     VARCHAR(30) NOT NULL,
    PRIMARY KEY (admin_id, role)
);

CREATE TABLE audit_logs
(
    id            BINARY(16)   NOT NULL,
    admin_id      BINARY(16)   NOT NULL,
    action        VARCHAR(30)  NOT NULL,
    target_entity VARCHAR(255) NOT NULL,
    target_id     BINARY(16)   NOT NULL,
    details       VARCHAR(255) NULL,
    `timestamp`   DATETIME(6)  NULL,
    PRIMARY KEY (id),
    INDEX idx_audit_logs_admin_id (admin_id),
    INDEX idx_audit_logs_target_id (target_id)
);

-- =========================================================================
-- Module advertiser
-- =========================================================================

CREATE TABLE advertisers
(
    id            BINARY(16)   NOT NULL,
    user_id       BINARY(16)   NOT NULL,
    company_name  VARCHAR(255) NOT NULL,
    tax_number    VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NULL,
    contact_phone VARCHAR(255) NULL,
    status        VARCHAR(30)  NULL,
    created_at    DATETIME(6)  NULL,
    updated_at    DATETIME(6)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_advertisers_user_id (user_id),
    UNIQUE KEY uk_advertisers_tax_number (tax_number)
);

-- =========================================================================
-- Module owner
-- =========================================================================

CREATE TABLE billboard_owners
(
    id                   BINARY(16)     NOT NULL,
    user_id              BINARY(16)     NOT NULL,
    company_name         VARCHAR(255)   NOT NULL,
    registration_number  VARCHAR(255)   NULL,
    contact_email        VARCHAR(255)   NOT NULL,
    phone_number         VARCHAR(255)   NULL,
    revenue_share_rate   DECIMAL(19, 4) NOT NULL,
    status               VARCHAR(30)    NOT NULL,
    created_at           DATETIME(6)    NULL,
    updated_at           DATETIME(6)    NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_billboard_owners_user_id (user_id)
);

-- =========================================================================
-- Module mediabuyer
-- =========================================================================

CREATE TABLE media_buyers
(
    id            BINARY(16)     NOT NULL,
    user_id       BINARY(16)     NOT NULL,
    company_name  VARCHAR(255)   NOT NULL,
    tax_id        VARCHAR(255)   NULL,
    contact_email VARCHAR(255)   NOT NULL,
    phone_number  VARCHAR(255)   NULL,
    credit_limit  DECIMAL(19, 4) NOT NULL,
    current_spent DECIMAL(19, 4) NOT NULL,
    status        VARCHAR(30)    NOT NULL,
    created_at    DATETIME(6)    NULL,
    updated_at    DATETIME(6)    NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_media_buyers_user_id (user_id)
);

-- =========================================================================
-- Module billboard
-- =========================================================================

CREATE TABLE billboards
(
    id          BINARY(16)     NOT NULL,
    title       VARCHAR(255)   NOT NULL,
    description VARCHAR(255)   NULL,
    type        VARCHAR(30)    NULL,
    status      VARCHAR(30)    NULL,
    address     VARCHAR(255)   NULL,
    city        VARCHAR(255)   NULL,
    latitude    DOUBLE         NOT NULL,
    longitude   DOUBLE         NOT NULL,
    width       DOUBLE         NOT NULL,
    height      DOUBLE         NOT NULL,
    daily_rate  DECIMAL(19, 4) NULL,
    currency    VARCHAR(255)   NULL,
    owner_id    BINARY(16)     NULL,
    created_at  DATETIME(6)    NULL,
    updated_at  DATETIME(6)    NULL,
    PRIMARY KEY (id),
    INDEX idx_billboards_owner_id (owner_id),
    INDEX idx_billboards_status (status)
);

-- =========================================================================
-- Module booking
-- =========================================================================

CREATE TABLE bookings
(
    id             BINARY(16)     NOT NULL,
    billboard_id   BINARY(16)     NOT NULL,
    advertiser_id  BINARY(16)     NOT NULL,
    start_date     DATE           NOT NULL,
    end_date       DATE           NOT NULL,
    total_price    DECIMAL(19, 4) NOT NULL,
    currency       VARCHAR(255)   NOT NULL,
    status         VARCHAR(30)    NOT NULL,
    created_at     DATETIME(6)    NULL,
    updated_at     DATETIME(6)    NULL,
    PRIMARY KEY (id),
    INDEX idx_bookings_billboard_id (billboard_id),
    INDEX idx_bookings_advertiser_id (advertiser_id)
);

-- =========================================================================
-- Module campaign
-- =========================================================================

CREATE TABLE campaigns
(
    id                       BINARY(16)   NOT NULL,
    booking_id               BINARY(16)   NOT NULL,
    advertiser_id            BINARY(16)   NOT NULL,
    name                     VARCHAR(255) NOT NULL,
    description              VARCHAR(255) NULL,
    media_url                VARCHAR(255) NULL,
    media_file_type          VARCHAR(255) NULL,
    media_file_size_in_bytes BIGINT       NOT NULL,
    status                   VARCHAR(30)  NOT NULL,
    rejection_reason         VARCHAR(255) NULL,
    created_at               DATETIME(6)  NULL,
    updated_at               DATETIME(6)  NULL,
    PRIMARY KEY (id),
    INDEX idx_campaigns_booking_id (booking_id),
    INDEX idx_campaigns_advertiser_id (advertiser_id)
);

-- =========================================================================
-- Module contract
-- =========================================================================

CREATE TABLE contracts
(
    id                     BINARY(16)   NOT NULL,
    booking_id             BINARY(16)   NOT NULL,
    owner_id               BINARY(16)   NOT NULL,
    advertiser_id          BINARY(16)   NOT NULL,
    terms_and_conditions   TEXT         NOT NULL,
    status                 VARCHAR(30)  NOT NULL,
    owner_signed_by        VARCHAR(255) NULL,
    owner_signed_ip        VARCHAR(255) NULL,
    owner_signed_at        DATETIME(6)  NULL,
    advertiser_signed_by   VARCHAR(255) NULL,
    advertiser_signed_ip   VARCHAR(255) NULL,
    advertiser_signed_at   DATETIME(6)  NULL,
    created_at             DATETIME(6)  NULL,
    updated_at             DATETIME(6)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_contracts_booking_id (booking_id),
    INDEX idx_contracts_owner_id (owner_id),
    INDEX idx_contracts_advertiser_id (advertiser_id)
);

-- =========================================================================
-- Module creative
-- =========================================================================

CREATE TABLE creative_proofs
(
    id               BINARY(16)   NOT NULL,
    campaign_id      BINARY(16)   NOT NULL,
    version          INT          NOT NULL,
    file_url         VARCHAR(255) NOT NULL,
    width_in_pixels  INT          NOT NULL,
    height_in_pixels INT          NOT NULL,
    status           VARCHAR(30)  NOT NULL,
    feedback         VARCHAR(255) NULL,
    created_at       DATETIME(6)  NULL,
    updated_at       DATETIME(6)  NULL,
    PRIMARY KEY (id),
    INDEX idx_creative_proofs_campaign_id (campaign_id)
);

-- =========================================================================
-- Module installation
-- =========================================================================

CREATE TABLE installation_tasks
(
    id                BINARY(16)   NOT NULL,
    campaign_id       BINARY(16)   NOT NULL,
    billboard_id      BINARY(16)   NOT NULL,
    technician_id     BINARY(16)   NOT NULL,
    scheduled_date    DATETIME(6)  NOT NULL,
    status            VARCHAR(30)  NOT NULL,
    proof_photo_url   VARCHAR(255) NULL,
    proof_notes       VARCHAR(255) NULL,
    proof_uploaded_at DATETIME(6)  NULL,
    created_at        DATETIME(6)  NULL,
    updated_at        DATETIME(6)  NULL,
    PRIMARY KEY (id),
    INDEX idx_installation_tasks_campaign_id (campaign_id),
    INDEX idx_installation_tasks_billboard_id (billboard_id),
    INDEX idx_installation_tasks_technician_id (technician_id)
);

-- =========================================================================
-- Module payment
-- =========================================================================

CREATE TABLE payment_transactions
(
    id                BINARY(16)     NOT NULL,
    payer_id          BINARY(16)     NOT NULL,
    reference_id      BINARY(16)     NOT NULL,
    amount            DECIMAL(19, 4) NOT NULL,
    currency          VARCHAR(255)   NOT NULL,
    payment_method    VARCHAR(30)    NOT NULL,
    status            VARCHAR(30)    NOT NULL,
    gateway_reference VARCHAR(255)   NULL,
    failure_reason    VARCHAR(255)   NULL,
    created_at        DATETIME(6)    NULL,
    updated_at        DATETIME(6)    NULL,
    PRIMARY KEY (id),
    INDEX idx_payment_transactions_payer_id (payer_id),
    INDEX idx_payment_transactions_reference_id (reference_id)
);

-- =========================================================================
-- Module review
-- =========================================================================

CREATE TABLE billboard_reviews
(
    id                BINARY(16)    NOT NULL,
    author_id         BINARY(16)    NOT NULL,
    target_id         BINARY(16)    NOT NULL,
    rating            INT           NOT NULL,
    comment           VARCHAR(1000) NULL,
    status            VARCHAR(30)   NOT NULL,
    moderation_reason VARCHAR(255)  NULL,
    created_at        DATETIME(6)   NULL,
    updated_at        DATETIME(6)   NULL,
    PRIMARY KEY (id),
    INDEX idx_billboard_reviews_author_id (author_id),
    INDEX idx_billboard_reviews_target_id (target_id)
);

-- =========================================================================
-- Module reporting
-- =========================================================================

CREATE TABLE performance_reports
(
    id                 BINARY(16)     NOT NULL,
    target_id          BINARY(16)     NOT NULL,
    type               VARCHAR(30)    NOT NULL,
    start_date         DATETIME(6)    NOT NULL,
    end_date           DATETIME(6)    NOT NULL,
    total_impressions  BIGINT         NOT NULL,
    total_interactions BIGINT         NOT NULL,
    total_revenue      DECIMAL(19, 4) NULL,
    occupancy_rate     DECIMAL(19, 4) NULL,
    generated_at       DATETIME(6)    NULL,
    PRIMARY KEY (id),
    INDEX idx_performance_reports_target_id (target_id)
);

-- =========================================================================
-- Module notification
-- =========================================================================

CREATE TABLE notification_logs
(
    id            BINARY(16)   NOT NULL,
    recipient_id  BINARY(16)   NULL,
    destination   VARCHAR(255) NOT NULL,
    channel       VARCHAR(30)  NOT NULL,
    template_code VARCHAR(255) NOT NULL,
    content       TEXT         NULL,
    status        VARCHAR(30)  NOT NULL,
    error_message VARCHAR(255) NULL,
    sent_at       DATETIME(6)  NULL,
    created_at    DATETIME(6)  NULL,
    PRIMARY KEY (id),
    INDEX idx_notification_logs_recipient_id (recipient_id)
);

-- =========================================================================
-- Module storage
-- =========================================================================

CREATE TABLE stored_files
(
    id                BINARY(16)   NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    content_type      VARCHAR(255) NOT NULL,
    size              BIGINT       NOT NULL,
    storage_path      VARCHAR(255) NOT NULL,
    provider          VARCHAR(30)  NOT NULL,
    owner_id          BINARY(16)   NOT NULL,
    uploaded_at       DATETIME(6)  NULL,
    PRIMARY KEY (id),
    INDEX idx_stored_files_owner_id (owner_id)
);

-- =========================================================================
-- Module platformsettings
-- =========================================================================

CREATE TABLE platform_settings
(
    setting_key   VARCHAR(255)  NOT NULL,
    setting_value VARCHAR(2000) NOT NULL,
    description   VARCHAR(255)  NULL,
    type          VARCHAR(30)   NOT NULL,
    updated_at    DATETIME(6)   NULL,
    PRIMARY KEY (setting_key)
);

-- =========================================================================
-- Registre d'événements Spring Modulith (spring-modulith-events-jpa).
-- Schéma dérivé du mapping réel de org.springframework.modulith.events.jpa.JpaEventPublication
-- (id en BINARY(16), colonnes complémentaires alignées sur le schema-mysql.sql v2 officiel du
-- module spring-modulith-events-jdbc). Sans cette table, tout eventPublisher.publishEvent(...)
-- échoue au runtime (utilisé par la quasi-totalité des services applicatifs).
-- =========================================================================

CREATE TABLE event_publication
(
    id                      BINARY(16)  NOT NULL,
    listener_id             VARCHAR(512) NOT NULL,
    event_type              VARCHAR(512) NOT NULL,
    serialized_event        TEXT         NOT NULL,
    publication_date        DATETIME(6)  NOT NULL,
    completion_date         DATETIME(6)  NULL,
    status                  VARCHAR(20)  NULL,
    completion_attempts     INT          NULL,
    last_resubmission_date  DATETIME(6)  NULL,
    PRIMARY KEY (id),
    INDEX idx_event_publication_completion_date (completion_date)
);

CREATE TABLE event_publication_archive
(
    id                      BINARY(16)  NOT NULL,
    listener_id             VARCHAR(512) NOT NULL,
    event_type              VARCHAR(512) NOT NULL,
    serialized_event        TEXT         NOT NULL,
    publication_date        DATETIME(6)  NOT NULL,
    completion_date         DATETIME(6)  NULL,
    status                  VARCHAR(20)  NULL,
    completion_attempts     INT          NULL,
    last_resubmission_date  DATETIME(6)  NULL,
    PRIMARY KEY (id),
    INDEX idx_event_publication_archive_completion_date (completion_date)
);
