package com.sentinelx.ingestion.rule.impl;

import com.sentinelx.ingestion.rule.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class NewDeviceHighValueRule implements FraudRule {

    public static final String RULE_CODE = "RUL-003";
    public static final String RULE_NAME = "NEW_DEVICE_HIGH_VALUE";
    private static final double DEVICE_AGE_THRESHOLD_MINUTES = 15.0;
    private static final BigDecimal AMOUNT_THRESHOLD = new BigDecimal("25000");

    @Override
    public String getRuleCode() {
        return RULE_CODE;
    }

    @Override
    public String getRuleName() {
        return RULE_NAME;
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        Double deviceAge = context.getDeviceAgeMinutes();
        BigDecimal amount = context.getCurrentTransactionAmount();

        if (deviceAge != null && amount != null) {
            if (deviceAge < DEVICE_AGE_THRESHOLD_MINUTES && amount.compareTo(AMOUNT_THRESHOLD) > 0) {
                return RuleResult.builder()
                        .triggered(true)
                        .evidence(Evidence.builder()
                                .ruleCode(RULE_CODE)
                                .ruleName(RULE_NAME)
                                .triggered(true)
                                .severity("CRITICAL")
                                .scoreContribution(25)
                                .details(Map.of(
                                        "deviceAgeMinutes", deviceAge,
                                        "deviceAgeThreshold", DEVICE_AGE_THRESHOLD_MINUTES,
                                        "transactionAmount", amount,
                                        "amountThreshold", AMOUNT_THRESHOLD
                                ))
                                .build())
                        .build();
            }
        }
        return RuleResult.notTriggered(RULE_CODE, RULE_NAME);
    }
}
