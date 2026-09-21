package com.sentinelx.ingestion;

import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import com.sentinelx.ingestion.rule.*;
import com.sentinelx.ingestion.rule.impl.*;
import com.sentinelx.ingestion.service.RuleEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FraudRuleEngineTest {

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
    void testHighValueTransactionRule_PositiveTrigger() {
        EventEnvelopeDto event = EventEnvelopeDto.builder()
                .eventId("EVT-TEST-HV")
                .eventType("TRANSACTION")
                .schemaVersion("1.0")
                .timestamp(Instant.now())
                .source("TEST")
                .accountId("ACC-101")
                .payload(new HashMap<>())
                .build();

        RuleContext context = RuleContext.builder()
                .event(event)
                .currentTransactionAmount(new BigDecimal("85000")) // > 50,000 threshold
                .build();

        RiskEvaluation evaluation = ruleEngineService.evaluateRisk(context);

        assertTrue(evaluation.getRiskScore() >= 20, "High value transaction should contribute +20 pts");
        assertTrue(evaluation.getTriggeredEvidence().stream().anyMatch(e -> e.getRuleCode().equals("RUL-001")));
    }

    @Test
    void testHighValueTransactionRule_BoundaryAndNegative() {
        EventEnvelopeDto event = EventEnvelopeDto.builder().eventId("EVT-TEST-HV-NEG").eventType("TRANSACTION").schemaVersion("1.0").timestamp(Instant.now()).source("TEST").payload(new HashMap<>()).build();

        // Boundary: exact 50,000 should NOT trigger (> operator)
        RuleContext boundaryContext = RuleContext.builder().event(event).currentTransactionAmount(new BigDecimal("50000")).build();
        RiskEvaluation boundaryEval = ruleEngineService.evaluateRisk(boundaryContext);
        assertFalse(boundaryEval.getTriggeredEvidence().stream().anyMatch(e -> e.getRuleCode().equals("RUL-001")));

        // Below threshold: 49,999
        RuleContext negContext = RuleContext.builder().event(event).currentTransactionAmount(new BigDecimal("49999")).build();
        RiskEvaluation negEval = ruleEngineService.evaluateRisk(negContext);
        assertFalse(negEval.getTriggeredEvidence().stream().anyMatch(e -> e.getRuleCode().equals("RUL-001")));
    }

    @Test
    void testNewDeviceHighValueRule_PositiveTrigger() {
        EventEnvelopeDto event = EventEnvelopeDto.builder().eventId("EVT-TEST-ND").eventType("TRANSACTION").schemaVersion("1.0").timestamp(Instant.now()).source("TEST").payload(new HashMap<>()).build();

        RuleContext context = RuleContext.builder()
                .event(event)
                .deviceAgeMinutes(2.5) // < 15 mins
                .currentTransactionAmount(new BigDecimal("30000")) // > 25,000
                .build();

        RiskEvaluation evaluation = ruleEngineService.evaluateRisk(context);
        assertTrue(evaluation.getTriggeredEvidence().stream().anyMatch(e -> e.getRuleCode().equals("RUL-003")));
    }

    @Test
    void testSuspiciousIpRule_PositiveTrigger() {
        EventEnvelopeDto event = EventEnvelopeDto.builder().eventId("EVT-TEST-IP").eventType("TRANSACTION").schemaVersion("1.0").timestamp(Instant.now()).source("TEST").ipAddress("185.220.101.5").payload(new HashMap<>()).build();

        RuleContext context = RuleContext.builder()
                .event(event)
                .isSuspiciousIp(true)
                .build();

        RiskEvaluation evaluation = ruleEngineService.evaluateRisk(context);
        assertTrue(evaluation.getTriggeredEvidence().stream().anyMatch(e -> e.getRuleCode().equals("RUL-008")));
    }
}
