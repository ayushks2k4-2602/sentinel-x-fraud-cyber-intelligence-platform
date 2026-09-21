package com.sentinelx.ingestion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityPayloadDto {

    @NotBlank(message = "indicatorValue is mandatory")
    private String indicatorValue;

    @NotBlank(message = "indicatorType is mandatory")
    private String indicatorType; // IP_ADDRESS, DOMAIN, FILE_HASH

    private String severity; // CRITICAL, HIGH, MEDIUM, LOW
    private Integer confidence; // 0 - 100
    private String category;
    private String mitreTechnique;
}
