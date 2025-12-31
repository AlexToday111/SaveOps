CREATE TABLE tracked_accounts (
    account_id UUID PRIMARY KEY,
    owner_id VARCHAR(128) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    tracked_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE interest_accruals (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    accrual_date DATE NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_interest_accrual_account_date UNIQUE (account_id, accrual_date)
);

CREATE INDEX idx_interest_accruals_account_id ON interest_accruals(account_id);
