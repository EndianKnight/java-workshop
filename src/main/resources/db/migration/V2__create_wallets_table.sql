-- V2__create_wallets_table.sql
-- Create wallets table with optimistic locking

CREATE TABLE wallets (
    id BIGSERIAL PRIMARY KEY,
    address VARCHAR(16) NOT NULL UNIQUE,
    balance DECIMAL(19,4) NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_wallets_address ON wallets(address);
CREATE INDEX idx_wallets_user_id ON wallets(user_id);
