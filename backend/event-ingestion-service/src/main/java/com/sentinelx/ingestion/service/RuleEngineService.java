package com.sentinelx.ingestion.service;

import com.sentinelx.ingestion.rule.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEngineService {

    private final List<FraudRule> rules;

    /**
     * Evaluates all registered fraud rules deterministically against the rule context.
     */
    public RiskEvaluation evaluateRisk(RuleContext context) {
        List<Evidence> triggeredEvidence = new ArrayList<>();
        int rawScoreSum = 0;

        for (FraudRule rule : rules) {
            RuleResult result = rule.evaluate(context);
            if (result.isTriggered() && result.getEvidence() != null) {
                triggeredEvidence.add(result.getEvidence());
                rawScoreSum += result.getEvidence().getScoreContribution();
                log.info("Rule [{}] TRIGGERED (+{} pts) for event [{}]", 
                        rule.getRuleCode(), result.getEvidence().getScoreContribution(), context.getEvent().getEventId());
            }
        }

        // Clamp final score between 0 and 100
        int finalScore = Math.min(100, Math.max(0, rawScoreSum));
        String severity = mapSeverity(finalScore);

        return RiskEvaluation.builder()
                .riskScore(finalScore)
                .severity(severity)
                .triggeredEvidence(triggeredEvidence)
                .evaluatedAt(Instant.now())
                .build();
    }

    private String mapSeverity(int score) {
        if (score >= 86) return "CRITICAL";
        if (score >= 61) return "HIGH";
        if (score >= 31) return "MEDIUM";
        return "LOW";
    }
}
