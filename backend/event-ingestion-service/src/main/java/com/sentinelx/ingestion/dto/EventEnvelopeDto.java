package com.sentinelx.ingestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelopeDto {

    @NotBlank(message = "eventId is mandatory")
    private String eventId;

    @NotBlank(message = "eventType is mandatory")
    private String eventType; // TRANSACTION, LOGIN, DEVICE_CHANGE, BENEFICIARY_ADDED, SECURITY_EVENT

    @NotBlank(message = "schemaVersion is mandatory")
    private String schemaVersion; // e.g. "1.0"

    @NotNull(message = "timestamp is mandatory")
    private Instant timestamp;

    @NotBlank(message = "source is mandatory")
    private String source; // e.g. "SYNTHETIC_GENERATOR", "API_GATEWAY"

    private String accountId;
    private String deviceId;
    private String ipAddress;

    @NotNull(message = "payload is mandatory")
    private Map<String, Object> payload;
}
