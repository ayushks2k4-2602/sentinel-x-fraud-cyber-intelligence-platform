import { MOCK_MODE } from './api';

class EventStreamService {
  constructor() {
    this.telemetryListeners = [];
    this.alertListeners = [];
    this.intervalId = null;
    this.isStreaming = false;
  }

  subscribeTelemetry(callback) {
    this.telemetryListeners.push(callback);
    if (!this.isStreaming && MOCK_MODE) {
      this.startMockStream();
    }
    return () => {
      this.telemetryListeners = this.telemetryListeners.filter(cb => cb !== callback);
      if (this.telemetryListeners.length === 0 && this.alertListeners.length === 0) {
        this.stopStream();
      }
    };
  }

  subscribeAlerts(callback) {
    this.alertListeners.push(callback);
    if (!this.isStreaming && MOCK_MODE) {
      this.startMockStream();
    }
    return () => {
      this.alertListeners = this.alertListeners.filter(cb => cb !== callback);
      if (this.telemetryListeners.length === 0 && this.alertListeners.length === 0) {
        this.stopStream();
      }
    };
  }

  startMockStream() {
    this.isStreaming = true;
    const sampleEvents = [
      { id: "EVT-9001", type: "TRANSACTION", account: "ACC-892140", amount: "₹50,000", riskScore: 94, status: "BLOCKED", ip: "185.220.101.5" },
      { id: "EVT-9000", type: "AUTH_FAILURE", account: "ACC-109923", amount: "-", riskScore: 58, status: "ALERT", ip: "185.220.101.5" },
      { id: "EVT-8999", type: "DEVICE_SWAP", account: "ACC-892140", amount: "-", riskScore: 72, status: "FLAGGED", ip: "45.142.120.12" },
      { id: "EVT-8998", type: "UPI_PAYMENT", account: "ACC-410293", amount: "₹1,250", riskScore: 12, status: "APPROVED", ip: "103.21.124.90" }
    ];

    let counter = 0;
    this.intervalId = setInterval(() => {
      const baseEvent = sampleEvents[counter % sampleEvents.length];
      const newEvent = {
        ...baseEvent,
        id: `EVT-${Math.floor(1000 + Math.random() * 9000)}`,
        timestamp: new Date().toLocaleTimeString('en-US', { hour12: false })
      };
      this.telemetryListeners.forEach(cb => cb(newEvent));

      // Also trigger fraud alert if critical score
      if (newEvent.riskScore >= 85 && this.alertListeners.length > 0) {
        this.alertListeners.forEach(cb => cb({
          id: `ALT-2026-${Math.floor(8800 + Math.random() * 100)}`,
          title: "Real-Time Correlated Fraud Alert",
          category: "CORRELATED_ATTACK_CHAIN",
          severity: "CRITICAL",
          riskScore: newEvent.riskScore,
          status: "NEW",
          assignedTo: "Unassigned",
          affectedAccount: newEvent.account,
          affectedUser: "Vikram Sharma",
          timestamp: newEvent.timestamp,
          summary: `High risk transaction event [${newEvent.id}] triggered STOMP alert broadcast.`
        }));
      }
      counter++;
    }, 2500);
  }

  stopStream() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
    this.isStreaming = false;
  }
}

export const eventStreamService = new EventStreamService();
