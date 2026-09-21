-- Flyway Migration V2: Rule Engine & Fraud Alert Schema

CREATE TABLE IF NOT EXISTS fraud_rules (
    id VARCHAR(64) PRIMARY KEY,
    rule_code VARCHAR(64) UNIQUE NOT NULL,
    name VARCHAR(128) NOT NULL,
    description TEXT,
    threshold_value NUMERIC(15,2) NOT NULL,
    score_weight INT NOT NULL,
    severity VARCHAR(32) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fraud_alerts (
    id VARCHAR(64) PRIMARY KEY,
    event_id VARCHAR(64) UNIQUE NOT NULL REFERENCES events(id),
    account_id VARCHAR(64) NOT NULL,
    alert_type VARCHAR(64) NOT NULL,
    severity VARCHAR(32) NOT NULL,
    risk_score INT NOT NULL,
    triggered_rules_json TEXT NOT NULL,
    evidence_json TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_alerts_account_id ON fraud_alerts(account_id);
CREATE INDEX IF NOT EXISTS idx_alerts_status ON fraud_alerts(status);
CREATE INDEX IF NOT EXISTS idx_alerts_risk_score ON fraud_alerts(risk_score);
