package com.sentinelx.ingestion.rule;

public interface FraudRule {
    String getRuleCode();
    String getRuleName();
    RuleResult evaluate(RuleContext context);
}
