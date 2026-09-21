package com.sentinelx.ingestion.rule.impl;

import com.sentinelx.ingestion.rule.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MultipleAccountsDeviceRule implements FraudRule {

    public static final String RULE_CODE = "RUL-007";
    public static final String RULE_NAME = "MULTIPLE_ACCOUNTS_SAME_DEVICE";
    private static final long DEFAULT_DEVICE_ACCOUNTS_THRESHOLD = 3;

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
        long accountsOnDevice = context.getUniqueAccountsOnDevice();
        if (accountsOnDevice > DEFAULT_DEVICE_ACCOUNTS_THRESHOLD) {
            return RuleResult.builder()
                    .triggered(true)
                    .evidence(Evidence.builder()
                            .ruleCode(RULE_CODE)
                            .ruleName(RULE_NAME)
                            .triggered(true)
                            .severity("HIGH")
                            .scoreContribution(20)
                            .details(Map.of(
                                    "uniqueAccountsOnDevice", accountsOnDevice,
                                    "threshold", DEFAULT_DEVICE_ACCOUNTS_THRESHOLD,
                                    "operator", ">"
                            ))
                            .build())
                    .build();
        }
        return RuleResult.notTriggered(RULE_CODE, RULE_NAME);
    }
}
