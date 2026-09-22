package com.sentinelx.ingestion.cti;

import com.sentinelx.ingestion.dto.RawIndicatorDto;
import com.sentinelx.ingestion.model.ThreatIndicatorEntity;
import com.sentinelx.ingestion.model.ThreatIndicatorSourceEntity;
import com.sentinelx.ingestion.repository.ThreatIndicatorRepository;
import com.sentinelx.ingestion.repository.ThreatIndicatorSourceRepository;
import com.sentinelx.ingestion.service.ProvenanceDeduplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class Stage4ProvenanceConfidenceTest {

    @Autowired
    private ProvenanceDeduplicationService provenanceDeduplicationService;

    @Autowired
    private ThreatIndicatorRepository threatIndicatorRepository;

    @Autowired
    private ThreatIndicatorSourceRepository threatIndicatorSourceRepository;

    @Test
    @DisplayName("ST4-01: Multi-Provider Consensus Boost Across 3 Providers")
    void testThreeProviderConsensusBoost() {
        String rawIp = " 198.51.100.045 ";

        // 1. Provider 1: ALIENVAULT (conf 75)
        RawIndicatorDto raw1 = RawIndicatorDto.builder()
                .rawValue(rawIp)
                .indicatorType("IP_ADDRESS")
                .category("BOTNET")
                .severity("MEDIUM")
                .confidence(75)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();
        ThreatIndicatorEntity ind1 = provenanceDeduplicationService.processAndDeduplicate(raw1, "ALIENVAULT");
        assertEquals(75, ind1.getAggregatedConfidence());

        // 2. Provider 2: VIRUSTOTAL (conf 80)
        RawIndicatorDto raw2 = RawIndicatorDto.builder()
                .rawValue("198.51.100.45")
                .indicatorType("IP_ADDRESS")
                .category("BOTNET")
                .severity("HIGH")
                .confidence(80)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();
        ThreatIndicatorEntity ind2 = provenanceDeduplicationService.processAndDeduplicate(raw2, "VIRUSTOTAL");
        // max(75, 80) + 5*(2-1) = 80 + 5 = 85
        assertEquals(85, ind2.getAggregatedConfidence());

        // 3. Provider 3: THREAT_CONNECT (conf 85)
        RawIndicatorDto raw3 = RawIndicatorDto.builder()
                .rawValue("198.51.100.45")
                .indicatorType("IP_ADDRESS")
                .category("BOTNET")
                .severity("CRITICAL")
                .confidence(85)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();
        ThreatIndicatorEntity ind3 = provenanceDeduplicationService.processAndDeduplicate(raw3, "THREAT_CONNECT");
        // max(75, 80, 85) + 5*(3-1) = 85 + 10 = 95
        assertEquals(95, ind3.getAggregatedConfidence());

        // Assert single canonical indicator identity retained
        assertEquals(ind1.getId(), ind3.getId());
        List<ThreatIndicatorSourceEntity> sources = threatIndicatorSourceRepository.findByIndicatorId(ind3.getId());
        assertEquals(3, sources.size());
    }

    @Test
    @DisplayName("ST4-02: Aggregated Confidence Clamping at Ceiling 100")
    void testAggregatedConfidenceCeilingClamping() {
        String rawDomain = "MALWARE-C2-NODE.NET.";

        for (int i = 1; i <= 4; i++) {
            RawIndicatorDto raw = RawIndicatorDto.builder()
                    .rawValue(rawDomain)
                    .indicatorType("DOMAIN")
                    .category("MALWARE")
                    .severity("CRITICAL")
                    .confidence(95)
                    .firstSeen(Instant.now())
                    .lastSeen(Instant.now())
                    .build();
            provenanceDeduplicationService.processAndDeduplicate(raw, "PROVIDER_" + i);
        }

        // max(95) + 5*(4-1) = 110 -> Must be clamped to 100
        ThreatIndicatorEntity found = threatIndicatorRepository
                .findByIndicatorTypeAndCanonicalValue("DOMAIN", "malware-c2-node.net")
                .orElseThrow();
        assertEquals(100, found.getAggregatedConfidence());
    }

    @Test
    @DisplayName("ST4-03: Provider Metadata Update and Source Resubmission")
    void testProviderResubmissionUpdate() {
        String rawHash = "d41d8cd98f00b204e9800998ecf8427e"; // MD5 hash

        RawIndicatorDto initial = RawIndicatorDto.builder()
                .rawValue(rawHash)
                .indicatorType("FILE_HASH")
                .category("SUSPICIOUS")
                .severity("LOW")
                .confidence(60)
                .sourceMetadataJson("{\"scanCount\": 1}")
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        provenanceDeduplicationService.processAndDeduplicate(initial, "VIRUSTOTAL");

        // Resubmission from same provider 2 days later with updated metadata and higher confidence
        RawIndicatorDto update = RawIndicatorDto.builder()
                .rawValue(rawHash)
                .indicatorType("FILE_HASH")
                .category("MALWARE")
                .severity("HIGH")
                .confidence(85)
                .sourceMetadataJson("{\"scanCount\": 45}")
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        ThreatIndicatorEntity updatedEntity = provenanceDeduplicationService.processAndDeduplicate(update, "VIRUSTOTAL");

        assertEquals(85, updatedEntity.getAggregatedConfidence());
        List<ThreatIndicatorSourceEntity> sources = threatIndicatorSourceRepository.findByIndicatorId(updatedEntity.getId());
        assertEquals(1, sources.size(), "Duplicate submission from same provider MUST NOT create duplicate source rows");
        assertEquals("{\"scanCount\": 45}", sources.get(0).getSourceMetadataJson());
    }

    @Test
    @DisplayName("ST4-04: Time Decay Calculation Across Multiple Age Tiers")
    void testTimeDecayTiers() {
        // Tier 1: 10 days old -> past 30 days = 0 -> decay = 0
        Instant t1 = Instant.now().minus(10, ChronoUnit.DAYS);
        RawIndicatorDto dto1 = RawIndicatorDto.builder()
                .rawValue("203.0.113.10")
                .indicatorType("IP_ADDRESS")
                .category("TOR_EXIT_NODE")
                .severity("MEDIUM")
                .confidence(90)
                .firstSeen(t1)
                .lastSeen(t1)
                .build();
        ThreatIndicatorEntity e1 = provenanceDeduplicationService.processAndDeduplicate(dto1, "P1");
        assertEquals(90, e1.getAggregatedConfidence());

        // Tier 2: 37 days old -> 7 days past 30 -> decay = 1
        Instant t2 = Instant.now().minus(37, ChronoUnit.DAYS);
        RawIndicatorDto dto2 = RawIndicatorDto.builder()
                .rawValue("203.0.113.20")
                .indicatorType("IP_ADDRESS")
                .category("TOR_EXIT_NODE")
                .severity("MEDIUM")
                .confidence(90)
                .firstSeen(t2)
                .lastSeen(t2)
                .build();
        ThreatIndicatorEntity e2 = provenanceDeduplicationService.processAndDeduplicate(dto2, "P1");
        assertEquals(89, e2.getAggregatedConfidence());

        // Tier 3: 51 days old -> 21 days past 30 -> decay = 3
        Instant t3 = Instant.now().minus(51, ChronoUnit.DAYS);
        RawIndicatorDto dto3 = RawIndicatorDto.builder()
                .rawValue("203.0.113.30")
                .indicatorType("IP_ADDRESS")
                .category("TOR_EXIT_NODE")
                .severity("MEDIUM")
                .confidence(90)
                .firstSeen(t3)
                .lastSeen(t3)
                .build();
        ThreatIndicatorEntity e3 = provenanceDeduplicationService.processAndDeduplicate(dto3, "P1");
        assertEquals(87, e3.getAggregatedConfidence());
    }

    @Test
    @DisplayName("ST4-05: Confidence Clamping at Floor 0")
    void testConfidenceFloorClamping() {
        Instant veryOld = Instant.now().minus(800, ChronoUnit.DAYS); // ~770 days past 30 -> decay ~110 points
        RawIndicatorDto oldDto = RawIndicatorDto.builder()
                .rawValue("203.0.113.99")
                .indicatorType("IP_ADDRESS")
                .category("PHISHING")
                .severity("LOW")
                .confidence(50)
                .firstSeen(veryOld)
                .lastSeen(veryOld)
                .build();

        ThreatIndicatorEntity entity = provenanceDeduplicationService.processAndDeduplicate(oldDto, "P1");
        assertEquals(0, entity.getAggregatedConfidence(), "Confidence must be clamped to floor of 0");
    }

    @Test
    @DisplayName("ST4-06: Null and Invalid Input Exception Guards")
    void testInputValidationGuards() {
        assertThrows(IllegalArgumentException.class, () -> provenanceDeduplicationService.processAndDeduplicate(null, "P1"));

        RawIndicatorDto nullValDto = RawIndicatorDto.builder().rawValue(null).indicatorType("IP_ADDRESS").build();
        assertThrows(IllegalArgumentException.class, () -> provenanceDeduplicationService.processAndDeduplicate(nullValDto, "P1"));

        RawIndicatorDto validDto = RawIndicatorDto.builder().rawValue("1.1.1.1").indicatorType("IP_ADDRESS").build();
        assertThrows(IllegalArgumentException.class, () -> provenanceDeduplicationService.processAndDeduplicate(validDto, "  "));
    }
}
