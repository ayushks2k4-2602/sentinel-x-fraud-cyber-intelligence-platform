package com.sentinelx.ingestion.rule.impl;

import com.sentinelx.ingestion.rule.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class HighValueTransactionRule implements FraudRule {

    public static final String RULE_CODE = "RUL-001";
    public static final String RULE_NAME = "HIGH_VALUE_TRANSACTION";
    private static final BigDecimal DEFAULT_THRESHOLD = new BigDecimal("50000");

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
        BigDecimal amount = context.getCurrentTransactionAmount();
        if (amount != null && amount.compareTo(DEFAULT_THRESHOLD) > 0) {
            return RuleResult.builder()
                    .triggered(true)
                    .evidence(Evidence.builder()
                            .ruleCode(RULE_CODE)
                            .ruleName(RULE_NAME)
                            .triggered(true)
                            .severity("HIGH")
                            .scoreContribution(20)
                            .details(Map.of(
                                    "transactionAmount", amount,
                                    "threshold", DEFAULT_THRESHOLD,
                                    "operator", ">"
                            ))
                            .build())
                    .build();
        }
        return RuleResult.notTriggered(RULE_CODE, RULE_NAME);
    }
}
