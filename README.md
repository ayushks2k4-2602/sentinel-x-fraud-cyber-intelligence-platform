# SENTINEL-X — Real-Time Financial Fraud & Cyber Threat Intelligence Platform

**SENTINEL-X** is an enterprise-grade, real-time security intelligence platform engineered to detect, correlate, and investigate cross-domain security incidents combining **financial transaction fraud** and **cyber threat intelligence**. By unifying real-time event streaming (Apache Kafka), timestamp-aware sliding window velocity tracking (Redis Sorted Sets), multi-model rule evaluation, and explainable risk scoring, SENTINEL-X detects complex multi-stage attack chains in sub-50ms latency.

---

## 🚀 Key Architectural Features

- **Real-Time Event Ingestion**: High-throughput validation & Kafka event streaming for financial card/IMPS/NEFT transfers and cyber authentication telemetry.
- **Timestamp-Aware Redis Velocity Engine**: True sliding-window velocity calculation (1m, 5m, 1h windows) via Redis Sorted Sets (`ZADD`, `ZREMRANGEBYSCORE`, `ZCARD`).
- **Modular Fraud Rule Engine**: 8 decoupled, Spring-managed fraud rules evaluating transaction context, device age, payee cool-downs, impossible travel, and threat indicators.
- **Explainable Risk Scoring**: Deterministic, mathematical risk evaluation ($0 - 100$ scale) producing structured evidence factor weights.
- **Idempotency & DLQ Protection**: Atomic database-level primary key guards (`eventId`) and Dead Letter Queue (`sentinel.events.dlq`) preventing duplicate alert processing or silent event loss.
- **Security Operations (SOC) Dashboard**: High-density React interface featuring Command Center overview, real-time Recharts telemetry, interactive Neo4j entity topology visualizer, 5-stage attack chain sequence, and AI Investigator assistant.

---

## 🛠️ Technology Stack

| Layer | Technologies Used |
| :--- | :--- |
| **Frontend SOC UI** | React 18, Vite 6, Tailwind CSS, Lucide React, Recharts, `@tanstack/react-query`, React Router 7 |
| **Backend Service** | Java 21 LTS, Spring Boot 3.2, Spring Web, Spring Validation, Spring Kafka, Spring Data JPA |
| **Database & Migration** | PostgreSQL 16, Flyway Migrations, H2 Database (Test Profile) |
| **Event Streaming** | Apache Kafka 7.5 (Partitions: `accountId`, `ipAddress`), STOMP WebSockets |
| **Velocity Cache** | Redis Sorted Sets (Sliding Window ZSET Store) |
| **Documentation** | OpenAPI 3.0 / Swagger UI |

---

## 📋 Rule Engine Catalog

| Rule Code | Rule Name | Condition Criteria | Weight | Severity |
| :--- | :--- | :--- | :---: | :---: |
| `RUL-001` | `HIGH_VALUE_TRANSACTION` | Transaction Amount $> \text{₹50,000}$ | `+20` | `HIGH` |
| `RUL-002` | `HIGH_TRANSACTION_VELOCITY` | Transactions in 1 minute $> 5$ | `+25` | `HIGH` |
| `RUL-003` | `NEW_DEVICE_HIGH_VALUE` | Device Age $< 15\text{ mins}$ AND Txn $> \text{₹25,000}$ | `+25` | `CRITICAL` |
| `RUL-004` | `RAPID_TRANSACTIONS_BURST` | $> 3\text{ transactions}$ in $10\text{ seconds}$ | `+20` | `HIGH` |
| `RUL-005` | `IMPOSSIBLE_TRAVEL` | Geo distance change $> 500\text{ km}$ within $15\text{ mins}$ | `+30` | `CRITICAL` |
| `RUL-006` | `RECENT_BENEFICIARY_HIGH_VALUE` | Payee added $< 5\text{ mins}$ ago AND Txn $> \text{₹20,000}$ | `+25` | `CRITICAL` |
| `RUL-007` | `MULTIPLE_ACCOUNTS_SAME_DEVICE` | $> 3\text{ unique accounts}$ associated with device fingerprint | `+20` | `HIGH` |
| `RUL-008` | `SUSPICIOUS_IP_TRANSACTION` | IP matches Tor Exit Node / Threat Intel list | `+25` | `CRITICAL` |

---

## ⚡ Quick Start & Setup Instructions

### 1. Start Infrastructure (PostgreSQL & Kafka)

```bash
cd infrastructure/docker
docker compose up -d
```

### 2. Run Spring Boot Backend

```bash
cd backend/event-ingestion-service
./mvnw spring-boot:run
```

### 3. Launch React SOC Dashboard

```bash
# In project root
npm install
npm run dev
```

The frontend will start at `http://localhost:3000` (or Vite assigned port).

---

## 📊 Benchmark & Synthetic Event Generation

To generate 1,000+ synthetic financial transactions and benchmark ingestion throughput:

```bash
python scripts/synthetic_benchmark.py
```

---

## 📡 Key REST API Endpoints

- `POST /api/v1/events/financial` — Ingest financial transaction telemetry.
- `POST /api/v1/events/security` — Ingest cyber security events.
- `GET /api/v1/rules` — List active rule catalog.
- `GET /api/v1/alerts/fraud` — Retrieve paginated fraud alerts with status filter.
- `PATCH /api/v1/alerts/{alertId}/status` — Update alert lifecycle status (`NEW`, `ACKNOWLEDGED`, `INVESTIGATING`, `RESOLVED`, `FALSE_POSITIVE`).
- `GET /swagger-ui.html` — Interactive OpenAPI Swagger UI documentation.
