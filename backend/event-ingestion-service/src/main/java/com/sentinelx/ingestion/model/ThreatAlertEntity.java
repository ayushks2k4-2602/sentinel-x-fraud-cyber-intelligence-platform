package com.sentinelx.ingestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "threat_alerts",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_alert_event_indicator_rule", columnNames = {"eventId", "indicatorId", "ruleCode"})
       },
       indexes = {
           @Index(name = "idx_threat_alerts_status", columnList = "status")
       })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatAlertEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String eventId; // FK referencing events(id)

    @Column(nullable = false)
    private String indicatorId; // FK referencing threat_indicators(id)

    @Column(nullable = false)
    private String ruleCode;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private Integer threatScore;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String threatDetailsJson;

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
