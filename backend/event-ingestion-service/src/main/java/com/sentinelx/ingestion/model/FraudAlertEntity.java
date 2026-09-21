package com.sentinelx.ingestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "fraud_alerts", indexes = {
    @Index(name = "idx_alerts_account_id", columnList = "accountId"),
    @Index(name = "idx_alerts_status", columnList = "status"),
    @Index(name = "idx_alerts_risk_score", columnList = "riskScore")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlertEntity {

    @Id
    private String id; // Alert ID

    @Column(nullable = false, unique = true)
    private String eventId; // Unique FK referencing events table ensuring alert idempotency

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String alertType;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private Integer riskScore;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String triggeredRulesJson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String evidenceJson;

    @Column(nullable = false)
    private String status; // NEW, ACKNOWLEDGED, INVESTIGATING, RESOLVED, FALSE_POSITIVE

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
