import {
  mockMetrics,
  mockTransactions,
  mockThreatIndicators,
  mockAlerts,
  showcaseIncident
} from './mockData';

// Configurable API Host & Mock Toggle
const BACKEND_BASE_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080/api/v1';
export const MOCK_MODE = import.meta.env.VITE_MOCK_MODE !== 'false';

const delay = (ms = 150) => new Promise(resolve => setTimeout(resolve, ms));

export const api = {
  // Health Status
  async getHealth() {
    if (MOCK_MODE) {
      return { status: "UP", mode: "MOCK_MODE_ACTIVE" };
    }
    try {
      const res = await fetch(`${BACKEND_BASE_URL}/health`);
      return await res.json();
    } catch (err) {
      console.warn("Backend offline, falling back to mock mode", err);
      return { status: "UP", mode: "MOCK_FALLBACK" };
    }
  },

  // Ingest Financial Telemetry Event
  async postFinancialEvent(eventEnvelope) {
    if (MOCK_MODE) {
      await delay(100);
      return { status: "ACCEPTED", eventId: eventEnvelope.eventId, mode: "MOCK" };
    }
    const res = await fetch(`${BACKEND_BASE_URL}/events/financial`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(eventEnvelope)
    });
    return await res.json();
  },

  // Ingest Cyber Security Telemetry Event
  async postSecurityEvent(eventEnvelope) {
    if (MOCK_MODE) {
      await delay(100);
      return { status: "ACCEPTED", eventId: eventEnvelope.eventId, mode: "MOCK" };
    }
    const res = await fetch(`${BACKEND_BASE_URL}/events/security`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(eventEnvelope)
    });
    return await res.json();
  },

  // Metrics & Dashboard
  async getMetrics() {
    await delay();
    return mockMetrics;
  },

  // Transactions
  async getTransactions(filter = 'ALL') {
    if (!MOCK_MODE) {
      try {
        const res = await fetch(`${BACKEND_BASE_URL}/events/recent`);
        if (res.ok) {
          const events = await res.json();
          if (events && events.length > 0) {
            return events.map(e => ({
              id: e.id,
              accountId: e.accountId || 'ACC-1001',
              customerName: e.source || 'Synthetic User',
              amount: 50000,
              currency: 'INR',
              type: e.eventType,
              beneficiaryName: 'Global Pay Corp',
              beneficiaryAccount: 'BEN-90214',
              status: e.status,
              riskScore: 88,
              severity: 'HIGH',
              ipAddress: e.ipAddress || '185.220.101.5',
              ipLocation: 'Frankfurt, DE (Tor Exit Node)',
              timestamp: e.timestamp,
              evidence: [
                { factor: "Ingested via Spring Boot Kafka Pipeline", weight: 25 }
              ]
            }));
          }
        }
      } catch (err) {
        console.warn("Backend recent events fetch failed, returning mock transactions", err);
      }
    }
    
    await delay();
    if (filter === 'ALL') return mockTransactions;
    return mockTransactions.filter(t => t.severity === filter || t.status === filter);
  },

  async getTransactionById(id) {
    await delay();
    return mockTransactions.find(t => t.id === id) || mockTransactions[0];
  },

  // Threat Intelligence
  async getThreatIndicators(filter = 'ALL') {
    await delay();
    if (filter === 'ALL') return mockThreatIndicators;
    return mockThreatIndicators.filter(i => i.type === filter || i.severity === filter);
  },

  // Alerts
  async getAlerts(statusFilter = 'ALL') {
    await delay();
    if (statusFilter === 'ALL') return mockAlerts;
    return mockAlerts.filter(a => a.status === statusFilter);
  },

  async updateAlertStatus(alertId, newStatus) {
    await delay(100);
    const alert = mockAlerts.find(a => a.id === alertId);
    if (alert) {
      alert.status = newStatus;
    }
    return { success: true, alertId, newStatus };
  },

  // Primary Investigation Workspace
  async getIncidentDetails(incidentId = 'INC-2026-9904') {
    await delay(200);
    return showcaseIncident;
  },

  // AI Investigator RAG Assistant Simulation
  async queryAiInvestigator(question) {
    await delay(500);
    const q = question.toLowerCase();
    
    if (q.includes("why") || q.includes("flagged") || q.includes("reason")) {
      return {
        answer: "Incident INC-2026-9904 was flagged due to a **correlated cross-domain attack chain**. The transaction originated from IP 185.220.101.5 (a known Tor Exit Node & Credential Stuffing Proxy) following 3 failed login attempts. An unrecognized Linux/Firefox device fingerprint was registered, followed immediately by beneficiary addition 'Global Pay Corp' without 2FA cool-down.",
        confidence: 0.96,
        sources: [
          "Spring Boot Ingestion API & Kafka Consumer",
          "ThreatIntel Feed: Tor Exit Node Match",
          "Idempotency Guard & Event Repository"
        ]
      };
    } else if (q.includes("ip") || q.includes("location") || q.includes("tor")) {
      return {
        answer: "The originating IP is **185.220.101.5**, located in Frankfurt, DE. It is indexed in ThreatConnect with 96% confidence as a Tor exit node associated with the *Operation DarkLoom* threat campaign.",
        confidence: 0.98,
        sources: [
          "ThreatIntel Database",
          "OpenSearch Security Logs"
        ]
      };
    } else if (q.includes("mitre") || q.includes("technique") || q.includes("attack")) {
      return {
        answer: "This incident maps directly to MITRE ATT&CK techniques:\n- **T1110.004**: Credential Stuffing (Initial Access)\n- **T1078.004**: Valid Accounts (Persistence)\n- **T1539**: Steal Web Session Cookie",
        confidence: 0.94,
        sources: [
          "MITRE ATT&CK Framework v14",
          "SENTINEL-X Ingestion Engine"
        ]
      };
    }

    return {
      answer: `Analysis complete for query: "${question}". Telemetry processed via Kafka topic 'sentinel.events.financial'. Account ACC-892140 experienced unauthorized access via Tor IP 185.220.101.5. Automated mitigation has blocked the ₹50,000 transfer.`,
      confidence: 0.91,
      sources: ["SENTINEL-X Spring Boot Ingestion Engine"]
    };
  }
};
