package com.sentinelx.ingestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "threat_indicators", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_indicator_type_value", columnNames = {"indicatorType", "canonicalValue"})
       },
       indexes = {
           @Index(name = "idx_indicators_canon_type", columnList = "indicatorType, canonicalValue"),
           @Index(name = "idx_indicators_confidence", columnList = "aggregatedConfidence")
       })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatIndicatorEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String canonicalValue;

    @Column(nullable = false)
    private String indicatorType; // IP_ADDRESS, DOMAIN, URL, FILE_HASH, EMAIL

    @Column(nullable = false)
    private String category; // TOR_EXIT_NODE, PHISHING, MALWARE, BOTNET

    @Column(nullable = false)
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW

    @Column(nullable = false)
    private Integer aggregatedConfidence; // 0 - 100

    private String campaignName;
    private String mitreTechnique;

    @Column(nullable = false)
    private Instant firstSeen;

    @Column(nullable = false)
    private Instant lastSeen;

    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "indicator", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ThreatIndicatorSourceEntity> sources = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
