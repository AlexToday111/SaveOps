CREATE TABLE audit_events (
    event_id VARCHAR(128) PRIMARY KEY,
    event_type VARCHAR(128) NOT NULL,
    aggregate_id VARCHAR(128) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    correlation_id VARCHAR(128) NOT NULL,
    payload TEXT NOT NULL,
    saved_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_audit_events_aggregate_id ON audit_events(aggregate_id);
CREATE INDEX idx_audit_events_occurred_at ON audit_events(occurred_at);
