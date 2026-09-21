package com.sentinelx.ingestion;

import com.sentinelx.ingestion.model.*;
import com.sentinelx.ingestion.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CtiDomainModelTest {

    @Autowired
    private ThreatIndicatorRepository threatIndicatorRepository;

    @Autowired
    private ThreatIndicatorSourceRepository threatIndicatorSourceRepository;

    @Autowired
    private ThreatAlertRepository threatAlertRepository;

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("ST1-01: Create and Persist Canonical Threat Indicator")
    void testCreateThreatIndicator() {
        ThreatIndicatorEntity indicator = ThreatIndicatorEntity.builder()
                .id("IOC-" + UUID.randomUUID().toString().substring(0, 8))
                .canonicalValue("185.220.101.5")
                .indicatorType("IP_ADDRESS")
                .category("TOR_EXIT_NODE")
                .severity("CRITICAL")
                .aggregatedConfidence(96)
                .campaignName("Operation DarkLoom")
                .mitreTechnique("T1110.004")
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        ThreatIndicatorEntity saved = threatIndicatorRepository.save(indicator);
        assertNotNull(saved.getId());
        assertTrue(threatIndicatorRepository.existsByIndicatorTypeAndCanonicalValue("IP_ADDRESS", "185.220.101.5"));
    }

    @Test
    @DisplayName("ST1-02: Enforce Canonical Uniqueness (indicatorType + canonicalValue)")
    void testCanonicalUniquenessConstraint() {
        String iocVal = "auth-verify-phish.com";
        ThreatIndicatorEntity ind1 = ThreatIndicatorEntity.builder()
                .id("IOC-UNQ-1")
                .canonicalValue(iocVal)
                .indicatorType("DOMAIN")
                .category("PHISHING")
                .severity("HIGH")
                .aggregatedConfidence(90)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();
        threatIndicatorRepository.saveAndFlush(ind1);

        ThreatIndicatorEntity ind2 = ThreatIndicatorEntity.builder()
                .id("IOC-UNQ-2")
                .canonicalValue(iocVal)
                .indicatorType("DOMAIN") // Duplicate type + value
                .category("PHISHING")
                .severity("HIGH")
                .aggregatedConfidence(85)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            threatIndicatorRepository.saveAndFlush(ind2);
        }, "Inserting duplicate canonical indicator value + type MUST throw DataIntegrityViolationException");
    }

    @Test
    @DisplayName("ST1-03: Enforce Multi-Source Provider Uniqueness (indicatorId + providerId)")
    void testSourceProviderUniqueness() {
        ThreatIndicatorEntity indicator = ThreatIndicatorEntity.builder()
                .id("IOC-SRC-1")
                .canonicalValue("45.142.120.12")
                .indicatorType("IP_ADDRESS")
                .category("BOTNET")
                .severity("HIGH")
                .aggregatedConfidence(88)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();
        threatIndicatorRepository.saveAndFlush(indicator);

        ThreatIndicatorSourceEntity src1 = ThreatIndicatorSourceEntity.builder()
                .id("SRC-1")
                .indicator(indicator)
                .providerId("VIRUSTOTAL")
                .providerConfidence(88)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();
        threatIndicatorSourceRepository.saveAndFlush(src1);

        ThreatIndicatorSourceEntity src2 = ThreatIndicatorSourceEntity.builder()
                .id("SRC-2")
                .indicator(indicator)
                .providerId("VIRUSTOTAL") // Duplicate provider for same indicator
                .providerConfidence(90)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            threatIndicatorSourceRepository.saveAndFlush(src2);
        }, "Duplicate provider for the same indicator MUST throw DataIntegrityViolationException");
    }

    @Test
    @DisplayName("ST1-04: Enforce Threat Alert Composite Uniqueness (eventId + indicatorId + ruleCode)")
    void testThreatAlertCompositeUniqueness() {
        // Save dummy event
        EventEntity event = EventEntity.builder()
                .id("EVT-CTI-901")
                .eventType("SECURITY_EVENT")
                .schemaVersion("1.0")
                .timestamp(Instant.now())
                .source("TEST")
                .payloadJson("{}")
                .status("PROCESSED")
                .build();
        eventRepository.saveAndFlush(event);

        // Save indicator
        ThreatIndicatorEntity indicator = ThreatIndicatorEntity.builder()
                .id("IOC-ALT-1")
                .canonicalValue("185.220.101.99")
                .indicatorType("IP_ADDRESS")
                .category("TOR_EXIT_NODE")
                .severity("CRITICAL")
                .aggregatedConfidence(95)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();
        threatIndicatorRepository.saveAndFlush(indicator);

        // Save Alert 1
        ThreatAlertEntity alert1 = ThreatAlertEntity.builder()
                .id("ALT-CTI-1")
                .eventId("EVT-CTI-901")
                .indicatorId("IOC-ALT-1")
                .ruleCode("RUL-CTI-001")
                .severity("CRITICAL")
                .threatScore(30)
                .threatDetailsJson("{}")
                .status("NEW")
                .build();
        threatAlertRepository.saveAndFlush(alert1);

        // Duplicate Alert (same eventId + indicatorId + ruleCode)
        ThreatAlertEntity alertDuplicate = ThreatAlertEntity.builder()
                .id("ALT-CTI-2")
                .eventId("EVT-CTI-901")
                .indicatorId("IOC-ALT-1")
                .ruleCode("RUL-CTI-001")
                .severity("CRITICAL")
                .threatScore(30)
                .threatDetailsJson("{}")
                .status("NEW")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            threatAlertRepository.saveAndFlush(alertDuplicate);
        }, "Duplicate (eventId, indicatorId, ruleCode) threat alert MUST throw DataIntegrityViolationException");
    }
}
