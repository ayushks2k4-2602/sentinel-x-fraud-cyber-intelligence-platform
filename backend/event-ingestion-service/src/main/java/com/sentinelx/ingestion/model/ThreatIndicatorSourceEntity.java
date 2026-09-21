package com.sentinelx.ingestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "threat_indicator_sources",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_indicator_provider", columnNames = {"indicator_id", "providerId"})
       })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatIndicatorSourceEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id", nullable = false)
    private ThreatIndicatorEntity indicator;

    @Column(nullable = false)
    private String providerId; // THREAT_CONNECT, VIRUSTOTAL, ALIENVAULT

    @Column(nullable = false)
    private Integer providerConfidence; // 0 - 100

    @Column(columnDefinition = "TEXT")
    private String sourceMetadataJson;

    @Column(nullable = false)
    private Instant firstSeen;

    @Column(nullable = false)
    private Instant lastSeen;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
