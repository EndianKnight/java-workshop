-- V3__create_transactions_table.sql
-- Create transactions table with idempotency key

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    from_wallet_address VARCHAR(16) NOT NULL,
    to_wallet_address VARCHAR(16) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    error_message TEXT
);

CREATE INDEX idx_transaction_from_wallet ON transactions(from_wallet_address);
CREATE INDEX idx_transaction_to_wallet ON transactions(to_wallet_address);
CREATE INDEX idx_transaction_idempotency ON transactions(idempotency_key);
CREATE INDEX idx_transaction_status ON transactions(status);
