package com.sentinelx.ingestion.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleResult {
    private boolean triggered;
    private Evidence evidence;

    public static RuleResult notTriggered(String ruleCode, String ruleName) {
        return RuleResult.builder()
                .triggered(false)
                .evidence(Evidence.builder()
                        .ruleCode(ruleCode)
                        .ruleName(ruleName)
                        .triggered(false)
                        .scoreContribution(0)
                        .severity("NONE")
                        .build())
                .build();
    }
}
