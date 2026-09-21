package com.sentinelx.ingestion;

import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import com.sentinelx.ingestion.rule.*;
import com.sentinelx.ingestion.rule.impl.*;
import com.sentinelx.ingestion.service.RuleEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeterminismTest {

    private RuleEngineService ruleEngineService;

    @BeforeEach
    void setUp() {
        List<FraudRule> rules = List.of(
                new HighValueTransactionRule(),
                new HighVelocityRule(),
                new NewDeviceHighValueRule(),
                new RapidTransactionsBurstRule(),
                new ImpossibleTravelRule(),
                new RecentBeneficiaryRule(),
                new MultipleAccountsDeviceRule(),
                new SuspiciousIpRule()
        );
        ruleEngineService = new RuleEngineService(rules);
    }

    @Test
    @DisplayName("Deterministic Risk Score Verification for Multi-Rule Event EVT-BENCH-90241")
    void testExactDeterministicScoreCalculation() {
        EventEnvelopeDto event = EventEnvelopeDto.builder()
                .eventId("EVT-BENCH-90241")
                .eventType("TRANSACTION")
                .schemaVersion("1.0")
                .timestamp(Instant.now())
                .source("SYNTHETIC_GENERATOR")
                .accountId("ACC-892140")
                .deviceId("dev_89a2b1c4")
                .ipAddress("185.220.101.5")
                .payload(new HashMap<>())
                .build();

        // Scenario: High Value (50,001) + New Device (2.5m) + Recent Beneficiary (2m) + Tor IP
        RuleContext context = RuleContext.builder()
                .event(event)
                .currentTransactionAmount(new BigDecimal("50001")) // RUL-001: +20
                .txnsLast1Min(2)                                   // RUL-002: 0
                .deviceAgeMinutes(2.5)                             // RUL-003: +25
                .beneficiaryAgeMinutes(2.0)                        // RUL-006: +25
                .isSuspiciousIp(true)                              // RUL-008: +25
                .build();

        // 1st Evaluation
        RiskEvaluation eval1 = ruleEngineService.evaluateRisk(context);
        assertEquals(95, eval1.getRiskScore(), "Exact raw sum: 20 + 25 + 25 + 25 = 95");
        assertEquals("CRITICAL", eval1.getSeverity());
        assertEquals(4, eval1.getTriggeredEvidence().size());

        // 2nd Evaluation (Proves strict determinism)
        RiskEvaluation eval2 = ruleEngineService.evaluateRisk(context);
        assertEquals(eval1.getRiskScore(), eval2.getRiskScore(), "Same input context MUST produce identical score");
        assertEquals(eval1.getSeverity(), eval2.getSeverity(), "Same input context MUST produce identical severity");
    }
}
