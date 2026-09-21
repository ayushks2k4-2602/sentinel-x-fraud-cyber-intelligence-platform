package com.sentinelx.ingestion.rule.impl;

import com.sentinelx.ingestion.rule.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SuspiciousIpRule implements FraudRule {

    public static final String RULE_CODE = "RUL-008";
    public static final String RULE_NAME = "SUSPICIOUS_IP_TRANSACTION";

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
        Boolean isSuspicious = context.getIsSuspiciousIp();
        if (Boolean.TRUE.equals(isSuspicious)) {
            return RuleResult.builder()
                    .triggered(true)
                    .evidence(Evidence.builder()
                            .ruleCode(RULE_CODE)
                            .ruleName(RULE_NAME)
                            .triggered(true)
                            .severity("CRITICAL")
                            .scoreContribution(25)
                            .details(Map.of(
                                    "suspiciousIpMatch", true,
                                    "ipAddress", context.getEvent().getIpAddress() != null ? context.getEvent().getIpAddress() : "UNKNOWN",
                                    "category", "Tor Exit Node / Threat Intel List Match"
                            ))
                            .build())
                    .build();
        }
        return RuleResult.notTriggered(RULE_CODE, RULE_NAME);
    }
}
