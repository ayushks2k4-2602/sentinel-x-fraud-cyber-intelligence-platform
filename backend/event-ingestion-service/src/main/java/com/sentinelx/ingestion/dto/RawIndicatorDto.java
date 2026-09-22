package com.sentinelx.ingestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawIndicatorDto {
    private String rawValue;
    private String indicatorType;   // IP_ADDRESS, DOMAIN, URL, FILE_HASH, EMAIL
    private String category;        // TOR_EXIT_NODE, PHISHING, MALWARE, BOTNET
    private String severity;        // CRITICAL, HIGH, MEDIUM, LOW
    private Integer confidence;     // 0 - 100
    private String campaignName;
    private String mitreTechnique;
    private Instant firstSeen;
    private Instant lastSeen;
    private Instant expiresAt;
    private String sourceMetadataJson;
}
