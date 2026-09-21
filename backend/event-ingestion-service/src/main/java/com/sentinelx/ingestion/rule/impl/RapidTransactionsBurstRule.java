package com.sentinelx.ingestion.rule.impl;

import com.sentinelx.ingestion.rule.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RapidTransactionsBurstRule implements FraudRule {

    public static final String RULE_CODE = "RUL-004";
    public static final String RULE_NAME = "RAPID_TRANSACTIONS_BURST";
    private static final long DEFAULT_BURST_THRESHOLD = 3; // > 3 txns in 10s

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
        long burstVelocity = context.getTxnsLast1Min(); // Evaluated against micro window
        if (burstVelocity >= DEFAULT_BURST_THRESHOLD && context.getTxnsLast1Min() > 3) {
            return RuleResult.builder()
                    .triggered(true)
                    .evidence(Evidence.builder()
                            .ruleCode(RULE_CODE)
                            .ruleName(RULE_NAME)
                            .triggered(true)
                            .severity("HIGH")
                            .scoreContribution(20)
                            .details(Map.of(
                                    "burstTxnCount", burstVelocity,
                                    "threshold", DEFAULT_BURST_THRESHOLD,
                                    "operator", ">="
                            ))
                            .build())
                    .build();
        }
        return RuleResult.notTriggered(RULE_CODE, RULE_NAME);
    }
}
