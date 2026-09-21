package com.sentinelx.ingestion.rule;

import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleContext {
    private EventEnvelopeDto event;
    
    // Velocity Signals
    private long txnsLast1Min;
    private long txnsLast5Min;
    private long txnsLast1Hour;
    private long failedLoginsLast5Min;
    private long uniqueAccountsOnDevice;
    private long uniqueAccountsOnIp;

    // Entity Metadata Context
    private Double deviceAgeMinutes;
    private Double beneficiaryAgeMinutes;
    private Boolean isSuspiciousIp;
    private Boolean isImpossibleTravel;
    private BigDecimal historicalAverageAmount;
    private BigDecimal currentTransactionAmount;
}
