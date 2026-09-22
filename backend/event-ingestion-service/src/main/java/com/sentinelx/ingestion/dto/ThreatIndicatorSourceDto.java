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
public class ThreatIndicatorSourceDto {

    private String id;

    @NotBlank(message = "indicatorId is mandatory")
    private String indicatorId;

    @NotBlank(message = "providerId is mandatory")
    private String providerId; // THREAT_CONNECT, VIRUSTOTAL, ALIENVAULT

    @NotNull(message = "providerConfidence is mandatory")
    @Min(value = 0, message = "providerConfidence must be at least 0")
    @Max(value = 100, message = "providerConfidence cannot exceed 100")
    private Integer providerConfidence;

    private String sourceMetadataJson;

    @NotNull(message = "firstSeen is mandatory")
    private Instant firstSeen;

    @NotNull(message = "lastSeen is mandatory")
    private Instant lastSeen;
}
