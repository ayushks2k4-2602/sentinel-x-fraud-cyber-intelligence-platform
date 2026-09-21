// Synthetic Data Generator & Mock Store for SENTINEL-X SOC Platform

export const mockMetrics = {
  eventsProcessed: 248391,
  fraudAlerts: 1284,
  cyberThreats: 427,
  criticalIncidents: 18,
  systemStatus: "OPERATIONAL",
  kafkaLagMs: 12,
  mlInferenceP99Ms: 14.2,
  riskDistribution: [
    { name: 'Low (0-30)', count: 214500, color: '#10B981' },
    { name: 'Medium (31-60)', count: 28400, color: '#3B82F6' },
    { name: 'High (61-85)', count: 4207, color: '#F59E0B' },
    { name: 'Critical (86-100)', count: 1284, color: '#F43F5E' },
  ],
  riskTimeline: [
    { time: '13:00', fraudEvents: 42, cyberEvents: 18, riskScoreAvg: 24 },
    { time: '13:10', fraudEvents: 58, cyberEvents: 22, riskScoreAvg: 28 },
    { time: '13:20', fraudEvents: 95, cyberEvents: 45, riskScoreAvg: 48 },
    { time: '13:30', fraudEvents: 140, cyberEvents: 89, riskScoreAvg: 82 }, // Spike
    { time: '13:40', fraudEvents: 82, cyberEvents: 54, riskScoreAvg: 62 },
    { time: '13:50', fraudEvents: 49, cyberEvents: 31, riskScoreAvg: 35 },
  ]
};

export const mockTransactions = [
  {
    id: "TXN-902418",
    accountId: "ACC-892140",
    customerName: "Vikram Sharma",
    amount: 50000,
    currency: "INR",
    type: "IMPS_TRANSFER",
    beneficiaryName: "Global Pay Corp",
    beneficiaryAccount: "BEN-90214",
    status: "BLOCKED",
    riskScore: 94,
    severity: "CRITICAL",
    ipAddress: "185.220.101.5",
    ipLocation: "Frankfurt, DE (Tor Exit Node)",
    deviceFingerprint: "dev_89a2b1c4",
    deviceModel: "Linux / Firefox 122.0 (Unrecognized)",
    timestamp: "2026-09-17 13:32:05",
    evidence: [
      { factor: "Known Tor Exit / Malicious IP Reputation", weight: 25 },
      { factor: "Unrecognized Device Fingerprint", weight: 20 },
      { factor: "Beneficiary Added < 3m Before Transaction", weight: 15 },
      { factor: "Transaction Velocity Deviation (4.5x Avg)", weight: 15 },
      { factor: "XGBoost ML Fraud Probability (0.91)", weight: 19 }
    ]
  },
  {
    id: "TXN-902417",
    accountId: "ACC-410293",
    customerName: "Ananya Roy",
    amount: 1250,
    currency: "INR",
    type: "UPI_PAYMENT",
    beneficiaryName: "Swiggy Foods",
    beneficiaryAccount: "MERCH-77182",
    status: "APPROVED",
    riskScore: 12,
    severity: "LOW",
    ipAddress: "103.21.124.90",
    ipLocation: "Mumbai, IN",
    deviceFingerprint: "dev_33b8a9f1",
    deviceModel: "iPhone 15 Pro / iOS 17.4",
    timestamp: "2026-09-17 13:31:40",
    evidence: [
      { factor: "Trusted Location & Recognized Device", weight: 0 },
      { factor: "Within Standard Spending Pattern", weight: 0 }
    ]
  },
  {
    id: "TXN-902416",
    accountId: "ACC-781920",
    customerName: "Rahul Verma",
    amount: 85000,
    currency: "INR",
    type: "NEFT_TRANSFER",
    beneficiaryName: "CryptoExchange Ltd",
    beneficiaryAccount: "BEN-33912",
    status: "FLAGGED",
    riskScore: 78,
    severity: "HIGH",
    ipAddress: "45.142.120.12",
    ipLocation: "Moscow, RU",
    deviceFingerprint: "dev_991823ab",
    deviceModel: "Windows 11 / Chrome 121",
    timestamp: "2026-09-17 13:28:12",
    evidence: [
      { factor: "Impossible Travel (Logged from IN 10m ago)", weight: 30 },
      { factor: "High-Risk Merchant Category (Crypto)", weight: 25 },
      { factor: "First Time Transfer to Beneficiary", weight: 23 }
    ]
  },
  {
    id: "TXN-902415",
    accountId: "ACC-109283",
    customerName: "Priya Patel",
    amount: 4500,
    currency: "INR",
    type: "CARD_PAYMENT",
    beneficiaryName: "Amazon India",
    beneficiaryAccount: "MERCH-00192",
    status: "APPROVED",
    riskScore: 22,
    severity: "LOW",
    ipAddress: "14.98.112.45",
    ipLocation: "Bengaluru, IN",
    deviceFingerprint: "dev_772189ff",
    deviceModel: "Android 14 / Chrome",
    timestamp: "2026-09-17 13:25:01",
    evidence: [
      { factor: "Verified Device & Habitual Merchant", weight: 0 }
    ]
  }
];

export const mockThreatIndicators = [
  {
    value: "185.220.101.5",
    type: "IP_ADDRESS",
    confidence: 96,
    severity: "CRITICAL",
    category: "Tor Exit Node / Credential Stuffing Proxy",
    firstSeen: "2026-08-12",
    lastSeen: "2026-09-17 13:32:05",
    source: "ThreatConnect / Internal Honeypot",
    campaign: "Operation DarkLoom",
    mitreTechnique: "T1110.004 - Credential Stuffing"
  },
  {
    value: "45.142.120.12",
    type: "IP_ADDRESS",
    confidence: 88,
    severity: "HIGH",
    category: "Bulletproof Hosting Provider",
    firstSeen: "2026-09-01",
    lastSeen: "2026-09-17 13:28:12",
    source: "AlienVault OTX",
    campaign: "FinStealer V2",
    mitreTechnique: "T1071.001 - Web Protocols"
  },
  {
    value: "auth-verify-bank-update.com",
    type: "DOMAIN",
    confidence: 99,
    severity: "CRITICAL",
    category: "Phishing Infrastructure",
    firstSeen: "2026-09-15",
    lastSeen: "2026-09-17 12:10:00",
    source: "PhishTank / VirusTotal",
    campaign: "BankerPhish-IN",
    mitreTechnique: "T1566.002 - Spearphishing Link"
  },
  {
    value: "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    type: "FILE_HASH",
    confidence: 92,
    severity: "HIGH",
    category: "Banking Trojan Dropper",
    firstSeen: "2026-09-10",
    lastSeen: "2026-09-16 18:40:11",
    source: "Mandiant Intelligence",
    campaign: "Anubis Mobile Malware",
    mitreTechnique: "T1204.002 - Malicious File"
  }
];

export const mockAlerts = [
  {
    id: "ALT-2026-8801",
    title: "Account Takeover & Fraudulent Transfer",
    category: "CORRELATED_ATTACK_CHAIN",
    severity: "CRITICAL",
    riskScore: 94,
    status: "INVESTIGATING",
    assignedTo: "SOC Analyst Alpha",
    affectedAccount: "ACC-892140",
    affectedUser: "Vikram Sharma",
    timestamp: "2026-09-17 13:32:05",
    summary: "Suspicious German IP compromised account via failed login brute force, registered new Linux device, added beneficiary, and attempted ₹50,000 transfer."
  },
  {
    id: "ALT-2026-8800",
    title: "Impossible Travel Authentication",
    category: "CYBER_THREAT",
    severity: "HIGH",
    riskScore: 78,
    status: "NEW",
    assignedTo: "Unassigned",
    affectedAccount: "ACC-781920",
    affectedUser: "Rahul Verma",
    timestamp: "2026-09-17 13:28:12",
    summary: "User authenticated from Moscow, RU 10 minutes after initiating transaction in Mumbai, IN."
  },
  {
    id: "ALT-2026-8799",
    title: "Brute Force Authentication Burst",
    category: "AUTHENTICATION_ANOMALY",
    severity: "MEDIUM",
    riskScore: 58,
    status: "ACKNOWLEDGED",
    assignedTo: "Tier 1 Analyst",
    affectedAccount: "ACC-109923",
    affectedUser: "Sanjay Kumar",
    timestamp: "2026-09-17 13:15:22",
    summary: "15 consecutive failed login attempts detected from single IP 185.220.101.5."
  },
  {
    id: "ALT-2026-8798",
    title: "High Velocity Card Testing",
    category: "FINANCIAL_FRAUD",
    severity: "HIGH",
    riskScore: 82,
    status: "RESOLVED",
    assignedTo: "Fraud Ops Team",
    affectedAccount: "ACC-552109",
    affectedUser: "Neha Gupta",
    timestamp: "2026-09-17 12:45:00",
    summary: "8 micro-transactions within 45 seconds targeting international e-commerce endpoints."
  }
];

// Complete Showcase Investigation Incident Scenario
export const showcaseIncident = {
  incidentId: "INC-2026-9904",
  alertId: "ALT-2026-8801",
  title: "Correlated Account Takeover & Fraudulent Exfiltration Chain",
  severity: "CRITICAL",
  riskScore: 94,
  status: "INVESTIGATING",
  createdTime: "2026-09-17 13:32:05",
  account: {
    id: "ACC-892140",
    owner: "Vikram Sharma",
    email: "v.sharma@enterprise.com",
    creationDate: "2023-04-12",
    balance: "₹1,45,000",
    baselineLocation: "Mumbai, IN"
  },
  attackChain: [
    {
      step: 1,
      time: "13:25:10",
      stage: "AUTHENTICATION_BURST",
      title: "Brute-force / Credential Stuffing",
      description: "3 failed login attempts from IP 185.220.101.5 (Tor Exit Node).",
      status: "FAILED",
      severity: "MEDIUM",
      icon: "ShieldAlert"
    },
    {
      step: 2,
      time: "13:27:44",
      stage: "CREDENTIAL_COMPROMISE",
      title: "Successful Authentication",
      description: "Session established via valid password hash from IP 185.220.101.5.",
      status: "SUCCESS",
      severity: "HIGH",
      icon: "Key"
    },
    {
      step: 3,
      time: "13:29:02",
      stage: "PERSISTENCE_DEVICE",
      title: "New Unrecognized Device Registered",
      description: "Device fingerprint 'dev_89a2b1c4' (Firefox/Linux) associated with account.",
      status: "ALERT",
      severity: "HIGH",
      icon: "Laptop"
    },
    {
      step: 4,
      time: "13:30:15",
      stage: "BENEFICIARY_CREATION",
      title: "Rapid Beneficiary Addition",
      description: "Beneficiary 'Global Pay Corp' (BEN-90214) added without secondary 2FA cool-down.",
      status: "SUSPICIOUS",
      severity: "CRITICAL",
      icon: "UserPlus"
    },
    {
      step: 5,
      time: "13:32:05",
      stage: "MONETARY_EXFILTRATION",
      title: "High-Value IMPS Transfer Attempt",
      description: "Attempted ₹50,000 IMPS transfer. Triggered automated execution block.",
      status: "BLOCKED",
      severity: "CRITICAL",
      icon: "DollarSign"
    }
  ],
  evidenceBreakdown: [
    { factor: "Malicious IP Reputation (Tor Exit / Threat Intel Match)", weight: 25, source: "ThreatIntel Engine" },
    { factor: "Unrecognized Device Fingerprint (First seen)", weight: 20, source: "Device Resolution Engine" },
    { factor: "Beneficiary Created < 3m Before Transaction", weight: 15, source: "Rules Engine" },
    { factor: "Transaction Velocity Deviation (4.5x Historical Avg)", weight: 15, source: "Redis Velocity Counter" },
    { factor: "XGBoost ML Fraud Classifier Output (0.91)", weight: 19, source: "ML Scoring Microservice" }
  ],
  entityGraph: {
    nodes: [
      { id: "ACC-892140", label: "Account: ACC-892140", type: "ACCOUNT", risk: "CRITICAL" },
      { id: "USER-102", label: "User: Vikram Sharma", type: "USER", risk: "LOW" },
      { id: "IP-185.220.101.5", label: "IP: 185.220.101.5", type: "IP", risk: "CRITICAL" },
      { id: "DEV-89A2B1C4", label: "Device: dev_89a2b1c4", type: "DEVICE", risk: "HIGH" },
      { id: "BEN-90214", label: "Payee: Global Pay Corp", type: "BENEFICIARY", risk: "HIGH" },
      { id: "TXN-902418", label: "Txn: ₹50,000 (IMPS)", type: "TRANSACTION", risk: "CRITICAL" },
      { id: "THREAT-TOR", label: "Threat: DarkLoom Campaign", type: "THREAT", risk: "CRITICAL" }
    ],
    edges: [
      { source: "USER-102", target: "ACC-892140", label: "OWNS" },
      { source: "IP-185.220.101.5", target: "DEV-89A2B1C4", label: "CONNECTED_TO" },
      { source: "IP-185.220.101.5", target: "THREAT-TOR", label: "MATCHES_INDICATOR" },
      { source: "DEV-89A2B1C4", target: "ACC-892140", label: "AUTHENTICATED_AS" },
      { source: "ACC-892140", target: "TXN-902418", label: "INITIATED" },
      { source: "TXN-902418", target: "BEN-90214", label: "TRANSFER_TO" }
    ]
  }
};
