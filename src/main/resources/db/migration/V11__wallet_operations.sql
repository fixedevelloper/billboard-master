-- Cycle de vie complet d'une demande de dépôt/retrait (Mobile Money ou virement bancaire),
-- distinct du grand livre wallet_transactions : une opération existe dès PENDING, avant tout
-- mouvement de solde effectif (voir WalletOperationService).
CREATE TABLE wallet_operations
(
    id                  BINARY(16)     NOT NULL,
    user_id             BINARY(16)     NOT NULL,
    type                VARCHAR(20)    NOT NULL,
    method              VARCHAR(20)    NOT NULL,
    status              VARCHAR(20)    NOT NULL,
    amount              DECIMAL(19, 4) NOT NULL,
    currency            VARCHAR(10)    NOT NULL,
    phone_number        VARCHAR(30)    NULL,
    bank_account_holder VARCHAR(255)   NULL,
    bank_iban           VARCHAR(64)    NULL,
    bank_name           VARCHAR(255)   NULL,
    reference           VARCHAR(255)   NULL,
    failure_reason      VARCHAR(500)   NULL,
    created_at          DATETIME(6)    NULL,
    updated_at          DATETIME(6)    NULL,
    PRIMARY KEY (id),
    INDEX idx_wallet_operations_user_id (user_id),
    INDEX idx_wallet_operations_status (status)
);
