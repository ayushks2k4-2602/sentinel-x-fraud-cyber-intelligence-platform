package com.sentinelx.ingestion.rule.impl;

import com.sentinelx.ingestion.rule.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HighVelocityRule implements FraudRule {

    public static final String RULE_CODE = "RUL-002";
    public static final String RULE_NAME = "HIGH_TRANSACTION_VELOCITY";
    private static final long DEFAULT_THRESHOLD = 5; // > 5 txns in 1 minute

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
        long velocity1m = context.getTxnsLast1Min();
        if (velocity1m > DEFAULT_THRESHOLD) {
            return RuleResult.builder()
                    .triggered(true)
                    .evidence(Evidence.builder()
                            .ruleCode(RULE_CODE)
                            .ruleName(RULE_NAME)
                            .triggered(true)
                            .severity("HIGH")
                            .scoreContribution(25)
                            .details(Map.of(
                                    "txnsLast1Min", velocity1m,
                                    "threshold", DEFAULT_THRESHOLD,
                                    "operator", ">"
                            ))
                            .build())
                    .build();
        }
        return RuleResult.notTriggered(RULE_CODE, RULE_NAME);
    }
}
