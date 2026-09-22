package com.sentinelx.ingestion.cti.impl;

import com.sentinelx.ingestion.cti.ThreatIntelProvider;
import com.sentinelx.ingestion.dto.RawIndicatorDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Component
public class SyntheticThreatProvider implements ThreatIntelProvider {

    private Instant lastSyncTimestamp = Instant.now();

    @Override
    public String getProviderId() {
        return "SYNTHETIC";
    }

    @Override
    public List<RawIndicatorDto> fetchIndicators() {
        lastSyncTimestamp = Instant.now();
        return List.of(
                RawIndicatorDto.builder()
                        .rawValue(" 185.220.101.005 ")
                        .indicatorType("IP_ADDRESS")
                        .category("TOR_EXIT_NODE")
                        .severity("CRITICAL")
                        .confidence(90)
                        .campaignName("Operation DarkLoom")
                        .mitreTechnique("T1110.004")
                        .firstSeen(Instant.now())
                        .lastSeen(Instant.now())
                        .build(),

                RawIndicatorDto.builder()
                        .rawValue("Auth-Verify.Bank-Update.COM.")
                        .indicatorType("DOMAIN")
                        .category("PHISHING")
                        .severity("HIGH")
                        .confidence(85)
                        .campaignName("PhishBank")
                        .mitreTechnique("T1566.002")
                        .firstSeen(Instant.now())
                        .lastSeen(Instant.now())
                        .build(),

                RawIndicatorDto.builder()
                        .rawValue("HTTP://Example.COM:80/Phish?User=A")
                        .indicatorType("URL")
                        .category("PHISHING")
                        .severity("HIGH")
                        .confidence(80)
                        .campaignName("PhishUrlCampaign")
                        .mitreTechnique("T1566.002")
                        .firstSeen(Instant.now())
                        .lastSeen(Instant.now())
                        .build(),

                RawIndicatorDto.builder()
                        .rawValue("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855")
                        .indicatorType("FILE_HASH")
                        .category("MALWARE")
                        .severity("CRITICAL")
                        .confidence(95)
                        .campaignName("ZeroDayWorm")
                        .mitreTechnique("T1204.002")
                        .firstSeen(Instant.now())
                        .lastSeen(Instant.now())
                        .build(),

                RawIndicatorDto.builder()
                        .rawValue(" Attacker@PhishDomain.com ")
                        .indicatorType("EMAIL")
                        .category("PHISHING")
                        .severity("MEDIUM")
                        .confidence(75)
                        .campaignName("SpearPhish2026")
                        .mitreTechnique("T1566.001")
                        .firstSeen(Instant.now())
                        .lastSeen(Instant.now())
                        .build()
        );
    }

    @Override
    public boolean supportsType(String indicatorType) {
        if (indicatorType == null || indicatorType.isBlank()) {
            return false;
        }
        String type = indicatorType.toUpperCase(Locale.ROOT).trim();
        return List.of("IP_ADDRESS", "IP", "DOMAIN", "FQDN", "URL", "FILE_HASH", "HASH", "MD5", "SHA256", "SHA-256", "EMAIL")
                .contains(type);
    }

    @Override
    public Instant getLastSyncTimestamp() {
        return lastSyncTimestamp;
    }

    @Override
    public int getDefaultConfidence() {
        return 80;
    }
}
