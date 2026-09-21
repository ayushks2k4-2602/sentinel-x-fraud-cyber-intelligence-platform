// Simulated WebSocket Stream for Live Events
class EventStreamService {
  constructor() {
    this.listeners = [];
    this.intervalId = null;
    this.isStreaming = false;
  }

  subscribe(callback) {
    this.listeners.push(callback);
    if (!this.isStreaming) {
      this.startStream();
    }
    return () => {
      this.listeners = this.listeners.filter(cb => cb !== callback);
      if (this.listeners.length === 0) {
        this.stopStream();
      }
    };
  }

  startStream() {
    this.isStreaming = true;
    const sampleEvents = [
      { id: "EVT-102", type: "TRANSACTION", account: "ACC-99120", amount: "₹12,400", riskScore: 18, status: "APPROVED", ip: "103.28.14.2" },
      { id: "EVT-103", type: "AUTH_FAILURE", account: "ACC-44129", amount: "-", riskScore: 65, status: "ALERT", ip: "185.220.101.5" },
      { id: "EVT-104", type: "DEVICE_SWAP", account: "ACC-88219", amount: "-", riskScore: 72, status: "FLAGGED", ip: "45.142.120.12" },
      { id: "EVT-105", type: "TRANSACTION", account: "ACC-89214", amount: "₹50,000", riskScore: 94, status: "BLOCKED", ip: "185.220.101.5" },
      { id: "EVT-106", type: "UPI_PAYMENT", account: "ACC-30192", amount: "₹450", riskScore: 8, status: "APPROVED", ip: "14.98.112.45" },
    ];

    let counter = 0;
    this.intervalId = setInterval(() => {
      const baseEvent = sampleEvents[counter % sampleEvents.length];
      const newEvent = {
        ...baseEvent,
        id: `EVT-${Math.floor(1000 + Math.random() * 9000)}`,
        timestamp: new Date().toLocaleTimeString('en-US', { hour12: false })
      };
      this.listeners.forEach(cb => cb(newEvent));
      counter++;
    }, 2500); // New event every 2.5 seconds
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
