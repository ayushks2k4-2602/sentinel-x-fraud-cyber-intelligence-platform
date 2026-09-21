-- SENTINEL-X Phase 4 Flyway Migration V3: CTI Domain & Provenance Schema

CREATE TABLE IF NOT EXISTS threat_indicators (
    id VARCHAR(64) PRIMARY KEY,
    canonical_value VARCHAR(255) NOT NULL,
    indicator_type VARCHAR(32) NOT NULL, -- IP_ADDRESS, DOMAIN, URL, FILE_HASH, EMAIL
    category VARCHAR(64) NOT NULL,       -- TOR_EXIT_NODE, PHISHING, MALWARE, BOTNET
    severity VARCHAR(32) NOT NULL,       -- CRITICAL, HIGH, MEDIUM, LOW
    aggregated_confidence INT NOT NULL,  -- 0 to 100
    campaign_name VARCHAR(128),
    mitre_technique VARCHAR(64),         -- e.g. T1110.004, T1566.002
    first_seen TIMESTAMP WITH TIME ZONE NOT NULL,
    last_seen TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_indicator_type_value UNIQUE (indicator_type, canonical_value)
);

CREATE TABLE IF NOT EXISTS threat_indicator_sources (
    id VARCHAR(64) PRIMARY KEY,
    indicator_id VARCHAR(64) NOT NULL REFERENCES threat_indicators(id) ON DELETE CASCADE,
    provider_id VARCHAR(64) NOT NULL,   -- THREAT_CONNECT, VIRUSTOTAL, ALIENVAULT
    provider_confidence INT NOT NULL,   -- 0 to 100
    source_metadata_json TEXT,
    first_seen TIMESTAMP WITH TIME ZONE NOT NULL,
    last_seen TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_indicator_provider UNIQUE (indicator_id, provider_id)
);

CREATE TABLE IF NOT EXISTS threat_alerts (
    id VARCHAR(64) PRIMARY KEY,
    event_id VARCHAR(64) NOT NULL REFERENCES events(id),
    indicator_id VARCHAR(64) NOT NULL REFERENCES threat_indicators(id),
    rule_code VARCHAR(64) NOT NULL,
    severity VARCHAR(32) NOT NULL,
    threat_score INT NOT NULL,
    threat_details_json TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_alert_event_indicator_rule UNIQUE (event_id, indicator_id, rule_code)
);

CREATE INDEX IF NOT EXISTS idx_indicators_canon_type ON threat_indicators(indicator_type, canonical_value);
CREATE INDEX IF NOT EXISTS idx_indicators_confidence ON threat_indicators(aggregated_confidence);
CREATE INDEX IF NOT EXISTS idx_threat_alerts_status ON threat_alerts(status);
