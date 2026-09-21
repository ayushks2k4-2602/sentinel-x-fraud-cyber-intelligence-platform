package com.sentinelx.ingestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_events_account_id", columnList = "accountId"),
    @Index(name = "idx_events_ip_address", columnList = "ipAddress"),
    @Index(name = "idx_events_timestamp", columnList = "timestamp")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

    @Id
    private String id; // Primary Key mapped to eventId for atomic idempotency

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String schemaVersion;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private String source;

    private String accountId;
    private String deviceId;
    private String ipAddress;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @Column(nullable = false)
    private String status; // RECEIVED, PROCESSED, DUPLICATE_SKIPPED, FAILED

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
