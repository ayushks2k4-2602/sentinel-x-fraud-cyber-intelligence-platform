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
public class ThreatAlertDto {

    private String id;

    @NotBlank(message = "eventId is mandatory")
    private String eventId;

    @NotBlank(message = "indicatorId is mandatory")
    private String indicatorId;

    @NotBlank(message = "ruleCode is mandatory")
    private String ruleCode;

    @NotBlank(message = "severity is mandatory")
    private String severity;

    @NotNull(message = "threatScore is mandatory")
    @Min(value = 0, message = "threatScore must be at least 0")
    @Max(value = 100, message = "threatScore cannot exceed 100")
    private Integer threatScore;

    @NotBlank(message = "threatDetailsJson is mandatory")
    private String threatDetailsJson;

    @NotBlank(message = "status is mandatory")
    private String status; // NEW, ACKNOWLEDGED, INVESTIGATING, RESOLVED, FALSE_POSITIVE

    private Instant createdAt;
}
