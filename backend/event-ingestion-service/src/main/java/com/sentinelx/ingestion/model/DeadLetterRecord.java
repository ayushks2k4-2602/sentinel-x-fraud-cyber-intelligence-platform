package com.sentinelx.ingestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "dead_letter_records")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeadLetterRecord {

    @Id
    private String id;

    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String originalTopic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String errorMessage;

    private Integer retryCount;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
