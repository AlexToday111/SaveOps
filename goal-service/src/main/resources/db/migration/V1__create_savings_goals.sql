CREATE TABLE savings_goals (
    id UUID PRIMARY KEY,
    owner_id VARCHAR(128) NOT NULL,
    account_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    target_amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_savings_goals_owner_id ON savings_goals(owner_id);
CREATE INDEX idx_savings_goals_account_id ON savings_goals(account_id);
