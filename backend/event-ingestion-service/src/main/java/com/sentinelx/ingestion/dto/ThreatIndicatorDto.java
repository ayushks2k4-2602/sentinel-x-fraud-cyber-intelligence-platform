package com.sentinelx.ingestion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatIndicatorDto {

    private String id;

    @NotBlank(message = "canonicalValue is mandatory")
    private String canonicalValue;

    @NotBlank(message = "indicatorType is mandatory")
    private String indicatorType; // IP_ADDRESS, DOMAIN, URL, FILE_HASH, EMAIL

    @NotBlank(message = "category is mandatory")
    private String category; // TOR_EXIT_NODE, PHISHING, MALWARE, BOTNET

    @NotBlank(message = "severity is mandatory")
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW

    @NotNull(message = "aggregatedConfidence is mandatory")
    @Min(value = 0, message = "aggregatedConfidence must be at least 0")
    @Max(value = 100, message = "aggregatedConfidence cannot exceed 100")
    private Integer aggregatedConfidence;

    private String campaignName;
    private String mitreTechnique;

    @NotNull(message = "firstSeen is mandatory")
    private Instant firstSeen;

    @NotNull(message = "lastSeen is mandatory")
    private Instant lastSeen;

    private Instant expiresAt;
}
