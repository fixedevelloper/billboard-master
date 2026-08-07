-- Module wallet : portefeuille utilisateur (dépôts / retraits).

CREATE TABLE wallets
(
    id         BINARY(16)     NOT NULL,
    user_id    BINARY(16)     NOT NULL,
    balance    DECIMAL(19, 4) NOT NULL,
    currency   VARCHAR(10)    NOT NULL,
    created_at DATETIME(6)    NULL,
    updated_at DATETIME(6)    NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_wallets_user_id (user_id)
);

CREATE TABLE wallet_transactions
(
    id         BINARY(16)     NOT NULL,
    wallet_id  BINARY(16)     NOT NULL,
    type       VARCHAR(30)    NOT NULL,
    amount     DECIMAL(19, 4) NOT NULL,
    currency   VARCHAR(10)    NOT NULL,
    reference  VARCHAR(255)   NULL,
    created_at DATETIME(6)    NULL,
    PRIMARY KEY (id),
    INDEX idx_wallet_transactions_wallet_id (wallet_id)
);
