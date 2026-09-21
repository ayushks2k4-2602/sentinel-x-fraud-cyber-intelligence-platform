-- SENTINEL-X Phase 2 Database Schema Foundation

CREATE TABLE IF NOT EXISTS events (
    id VARCHAR(64) PRIMARY KEY,
    event_type VARCHAR(64) NOT NULL,
    schema_version VARCHAR(16) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    source VARCHAR(64) NOT NULL,
    account_id VARCHAR(64),
    device_id VARCHAR(64),
    ip_address VARCHAR(45),
    payload_json TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PROCESSED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dead_letter_records (
    id VARCHAR(64) PRIMARY KEY,
    event_id VARCHAR(64) NOT NULL,
    original_topic VARCHAR(128) NOT NULL,
    error_message TEXT NOT NULL,
    retry_count INT DEFAULT 0,
    payload_json TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexing for rapid lookup & idempotency checks
CREATE INDEX IF NOT EXISTS idx_events_account_id ON events(account_id);
CREATE INDEX IF NOT EXISTS idx_events_ip_address ON events(ip_address);
CREATE INDEX IF NOT EXISTS idx_events_timestamp ON events(timestamp);
