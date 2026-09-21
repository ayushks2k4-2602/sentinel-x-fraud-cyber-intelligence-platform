package com.sentinelx.ingestion.rule.impl;

import com.sentinelx.ingestion.rule.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ImpossibleTravelRule implements FraudRule {

    public static final String RULE_CODE = "RUL-005";
    public static final String RULE_NAME = "IMPOSSIBLE_TRAVEL";

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
        Boolean isImpossibleTravel = context.getIsImpossibleTravel();
        if (Boolean.TRUE.equals(isImpossibleTravel)) {
            return RuleResult.builder()
                    .triggered(true)
                    .evidence(Evidence.builder()
                            .ruleCode(RULE_CODE)
                            .ruleName(RULE_NAME)
                            .triggered(true)
                            .severity("CRITICAL")
                            .scoreContribution(30)
                            .details(Map.of(
                                    "impossibleTravelDetected", true,
                                    "reason", "Geo distance change > 500km in < 15 minutes"
                            ))
                            .build())
                    .build();
        }
        return RuleResult.notTriggered(RULE_CODE, RULE_NAME);
    }
}
