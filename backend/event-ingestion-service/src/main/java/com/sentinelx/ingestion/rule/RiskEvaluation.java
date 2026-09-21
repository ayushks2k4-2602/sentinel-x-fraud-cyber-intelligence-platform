package com.sentinelx.ingestion.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluation {
    private int riskScore; // Clamped 0-100
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private List<Evidence> triggeredEvidence;
    private Instant evaluatedAt;
}
