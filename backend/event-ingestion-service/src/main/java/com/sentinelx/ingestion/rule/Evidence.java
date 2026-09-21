package com.sentinelx.ingestion.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evidence {
    private String ruleCode;
    private String ruleName;
    private boolean triggered;
    private String severity;
    private int scoreContribution;
    private Map<String, Object> details;
}
