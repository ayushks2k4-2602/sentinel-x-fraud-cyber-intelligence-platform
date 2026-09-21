package com.sentinelx.ingestion;

import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import com.sentinelx.ingestion.rule.*;
import com.sentinelx.ingestion.rule.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AllRulesDetailedTest {

    private EventEnvelopeDto dummyEvent;

    @BeforeEach
    void setUp() {
        dummyEvent = EventEnvelopeDto.builder()
                .eventId("EVT-RULE-TEST")
                .eventType("TRANSACTION")
                .schemaVersion("1.0")
                .timestamp(Instant.now())
                .source("UNIT_TEST")
                .accountId("ACC-1001")
                .deviceId("DEV-881")
                .ipAddress("185.220.101.5")
                .payload(new HashMap<>())
                .build();
    }

    // 1. RUL-001: HIGH_VALUE_TRANSACTION (Threshold > 50,000)
    @Test
    @DisplayName("RUL-001 HIGH_VALUE_TRANSACTION: Boundary & Trigger Verification")
    void testRUL001_HighValueTransaction() {
        HighValueTransactionRule rule = new HighValueTransactionRule();

        // Threshold - 1 (49,999) -> Negative
        RuleContext ctxBelow = RuleContext.builder().event(dummyEvent).currentTransactionAmount(new BigDecimal("49999")).build();
        assertFalse(rule.evaluate(ctxBelow).isTriggered(), "RUL-001 must NOT trigger for 49,999");

        // Threshold (50,000) -> Boundary (Operator >) -> Negative
        RuleContext ctxExact = RuleContext.builder().event(dummyEvent).currentTransactionAmount(new BigDecimal("50000")).build();
        assertFalse(rule.evaluate(ctxExact).isTriggered(), "RUL-001 must NOT trigger for exact 50,000");

        // Threshold + 1 (50,001) -> Positive Trigger
        RuleContext ctxAbove = RuleContext.builder().event(dummyEvent).currentTransactionAmount(new BigDecimal("50001")).build();
        RuleResult res = rule.evaluate(ctxAbove);
        assertTrue(res.isTriggered(), "RUL-001 MUST trigger for 50,001");
        assertEquals(20, res.getEvidence().getScoreContribution());
    }

    // 2. RUL-002: HIGH_TRANSACTION_VELOCITY (Threshold > 5 txns in 1 min)
    @Test
    @DisplayName("RUL-002 HIGH_TRANSACTION_VELOCITY: Boundary & Trigger Verification")
    void testRUL002_HighVelocity() {
        HighVelocityRule rule = new HighVelocityRule();

        // Threshold - 1 (4 txns)
        RuleContext ctx4 = RuleContext.builder().event(dummyEvent).txnsLast1Min(4).build();
        assertFalse(rule.evaluate(ctx4).isTriggered());

        // Threshold (5 txns)
        RuleContext ctx5 = RuleContext.builder().event(dummyEvent).txnsLast1Min(5).build();
        assertFalse(rule.evaluate(ctx5).isTriggered());

        // Threshold + 1 (6 txns) -> Positive
        RuleContext ctx6 = RuleContext.builder().event(dummyEvent).txnsLast1Min(6).build();
        RuleResult res = rule.evaluate(ctx6);
        assertTrue(res.isTriggered());
        assertEquals(25, res.getEvidence().getScoreContribution());
    }

    // 3. RUL-003: NEW_DEVICE_HIGH_VALUE (Device Age < 15m AND Amount > 25,000)
    @Test
    @DisplayName("RUL-003 NEW_DEVICE_HIGH_VALUE: Boundary & Trigger Verification")
    void testRUL003_NewDeviceHighValue() {
        NewDeviceHighValueRule rule = new NewDeviceHighValueRule();

        // Device Age 16m (> threshold) -> Negative
        RuleContext ctxOldDevice = RuleContext.builder().event(dummyEvent).deviceAgeMinutes(16.0).currentTransactionAmount(new BigDecimal("30000")).build();
        assertFalse(rule.evaluate(ctxOldDevice).isTriggered());

        // Device Age 14m, Amount 24,999 (Amount < 25,000) -> Negative
        RuleContext ctxLowAmt = RuleContext.builder().event(dummyEvent).deviceAgeMinutes(14.0).currentTransactionAmount(new BigDecimal("24999")).build();
        assertFalse(rule.evaluate(ctxLowAmt).isTriggered());

        // Positive: Device Age 2m, Amount 25,001 -> Triggered
        RuleContext ctxPos = RuleContext.builder().event(dummyEvent).deviceAgeMinutes(2.0).currentTransactionAmount(new BigDecimal("25001")).build();
        RuleResult res = rule.evaluate(ctxPos);
        assertTrue(res.isTriggered());
        assertEquals("CRITICAL", res.getEvidence().getSeverity());
    }

    // 4. RUL-004: RAPID_TRANSACTIONS_BURST (Threshold >= 3 txns in micro window)
    @Test
    @DisplayName("RUL-004 RAPID_TRANSACTIONS_BURST: Boundary & Trigger Verification")
    void testRUL004_RapidBurst() {
        RapidTransactionsBurstRule rule = new RapidTransactionsBurstRule();

        RuleContext ctx2 = RuleContext.builder().event(dummyEvent).txnsLast1Min(2).build();
        assertFalse(rule.evaluate(ctx2).isTriggered());

        RuleContext ctx4 = RuleContext.builder().event(dummyEvent).txnsLast1Min(4).build();
        assertTrue(rule.evaluate(ctx4).isTriggered());
    }

    // 5. RUL-005: IMPOSSIBLE_TRAVEL
    @Test
    @DisplayName("RUL-005 IMPOSSIBLE_TRAVEL: Verification")
    void testRUL005_ImpossibleTravel() {
        ImpossibleTravelRule rule = new ImpossibleTravelRule();

        RuleContext ctxFalse = RuleContext.builder().event(dummyEvent).isImpossibleTravel(false).build();
        assertFalse(rule.evaluate(ctxFalse).isTriggered());

        RuleContext ctxTrue = RuleContext.builder().event(dummyEvent).isImpossibleTravel(true).build();
        RuleResult res = rule.evaluate(ctxTrue);
        assertTrue(res.isTriggered());
        assertEquals(30, res.getEvidence().getScoreContribution());
    }

    // 6. RUL-006: RECENT_BENEFICIARY_HIGH_VALUE (Age < 5m AND Amount > 20,000)
    @Test
    @DisplayName("RUL-006 RECENT_BENEFICIARY_HIGH_VALUE: Boundary Verification")
    void testRUL006_RecentBeneficiary() {
        RecentBeneficiaryRule rule = new RecentBeneficiaryRule();

        RuleContext ctxOldPayee = RuleContext.builder().event(dummyEvent).beneficiaryAgeMinutes(6.0).currentTransactionAmount(new BigDecimal("30000")).build();
        assertFalse(rule.evaluate(ctxOldPayee).isTriggered());

        RuleContext ctxNewPayee = RuleContext.builder().event(dummyEvent).beneficiaryAgeMinutes(2.0).currentTransactionAmount(new BigDecimal("20001")).build();
        assertTrue(rule.evaluate(ctxNewPayee).isTriggered());
    }

    // 7. RUL-007: MULTIPLE_ACCOUNTS_SAME_DEVICE (Accounts > 3)
    @Test
    @DisplayName("RUL-007 MULTIPLE_ACCOUNTS_SAME_DEVICE: Boundary Verification")
    void testRUL007_MultipleAccountsDevice() {
        MultipleAccountsDeviceRule rule = new MultipleAccountsDeviceRule();

        RuleContext ctx3 = RuleContext.builder().event(dummyEvent).uniqueAccountsOnDevice(3).build();
        assertFalse(rule.evaluate(ctx3).isTriggered());

        RuleContext ctx4 = RuleContext.builder().event(dummyEvent).uniqueAccountsOnDevice(4).build();
        assertTrue(rule.evaluate(ctx4).isTriggered());
    }

    // 8. RUL-008: SUSPICIOUS_IP_TRANSACTION
    @Test
    @DisplayName("RUL-008 SUSPICIOUS_IP_TRANSACTION: Verification")
    void testRUL008_SuspiciousIp() {
        SuspiciousIpRule rule = new SuspiciousIpRule();

        RuleContext ctxNormal = RuleContext.builder().event(dummyEvent).isSuspiciousIp(false).build();
        assertFalse(rule.evaluate(ctxNormal).isTriggered());

        RuleContext ctxTor = RuleContext.builder().event(dummyEvent).isSuspiciousIp(true).build();
        RuleResult res = rule.evaluate(ctxTor);
        assertTrue(res.isTriggered());
        assertEquals(25, res.getEvidence().getScoreContribution());
    }
}
