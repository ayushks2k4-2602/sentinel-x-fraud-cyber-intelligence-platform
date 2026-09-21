package com.sentinelx.ingestion.rule.impl;

import com.sentinelx.ingestion.rule.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class RecentBeneficiaryRule implements FraudRule {

    public static final String RULE_CODE = "RUL-006";
    public static final String RULE_NAME = "RECENT_BENEFICIARY_HIGH_VALUE";
    private static final double BENEFICIARY_AGE_THRESHOLD_MINUTES = 5.0;
    private static final BigDecimal AMOUNT_THRESHOLD = new BigDecimal("20000");

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
        Double payeeAge = context.getBeneficiaryAgeMinutes();
        BigDecimal amount = context.getCurrentTransactionAmount();

        if (payeeAge != null && amount != null) {
            if (payeeAge < BENEFICIARY_AGE_THRESHOLD_MINUTES && amount.compareTo(AMOUNT_THRESHOLD) > 0) {
                return RuleResult.builder()
                        .triggered(true)
                        .evidence(Evidence.builder()
                                .ruleCode(RULE_CODE)
                                .ruleName(RULE_NAME)
                                .triggered(true)
                                .severity("CRITICAL")
                                .scoreContribution(25)
                                .details(Map.of(
                                        "beneficiaryAgeMinutes", payeeAge,
                                        "ageThreshold", BENEFICIARY_AGE_THRESHOLD_MINUTES,
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
