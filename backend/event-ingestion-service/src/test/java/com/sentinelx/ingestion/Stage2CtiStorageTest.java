package com.sentinelx.ingestion;

import com.sentinelx.ingestion.dto.ThreatAlertDto;
import com.sentinelx.ingestion.dto.ThreatIndicatorDto;
import com.sentinelx.ingestion.dto.ThreatIndicatorSourceDto;
import com.sentinelx.ingestion.model.EventEntity;
import com.sentinelx.ingestion.model.ThreatAlertEntity;
import com.sentinelx.ingestion.model.ThreatIndicatorEntity;
import com.sentinelx.ingestion.model.ThreatIndicatorSourceEntity;
import com.sentinelx.ingestion.repository.EventRepository;
import com.sentinelx.ingestion.repository.ThreatIndicatorRepository;
import com.sentinelx.ingestion.service.ThreatAlertStorageService;
import com.sentinelx.ingestion.service.ThreatIndicatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class Stage2CtiStorageTest {

    @Autowired
    private ThreatIndicatorService threatIndicatorService;

    @Autowired
    private ThreatAlertStorageService threatAlertStorageService;

    @Autowired
    private ThreatIndicatorRepository threatIndicatorRepository;

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("ST2-01: Save New Threat Indicator with Source Provenance")
    void testSaveNewIndicatorWithSource() {
        ThreatIndicatorDto dto = ThreatIndicatorDto.builder()
                .canonicalValue("185.220.101.99")
                .indicatorType("IP_ADDRESS")
                .category("TOR_EXIT_NODE")
                .severity("CRITICAL")
                .aggregatedConfidence(95)
                .campaignName("Operation DarkLoom")
                .mitreTechnique("T1110.004")
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        ThreatIndicatorSourceDto srcDto = ThreatIndicatorSourceDto.builder()
                .providerId("ALIENVAULT")
                .providerConfidence(95)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        ThreatIndicatorEntity saved = threatIndicatorService.saveOrUpdateIndicator(dto, srcDto);
        assertNotNull(saved.getId());
        assertEquals("185.220.101.99", saved.getCanonicalValue());

        Optional<ThreatIndicatorEntity> found = threatIndicatorService
                .findByCanonicalValueAndType("IP_ADDRESS", "185.220.101.99");
        assertTrue(found.isPresent());
        assertEquals(95, found.get().getAggregatedConfidence());
    }

    @Test
    @DisplayName("ST2-02: Update Existing Indicator and Attach Second Provider Source")
    void testUpdateIndicatorWithSecondSource() {
        String iocValue = "auth-phish-domain.com";
        ThreatIndicatorDto dto1 = ThreatIndicatorDto.builder()
                .canonicalValue(iocValue)
                .indicatorType("DOMAIN")
                .category("PHISHING")
                .severity("HIGH")
                .aggregatedConfidence(85)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        ThreatIndicatorSourceDto src1 = ThreatIndicatorSourceDto.builder()
                .providerId("VIRUSTOTAL")
                .providerConfidence(85)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        ThreatIndicatorEntity ind = threatIndicatorService.saveOrUpdateIndicator(dto1, src1);

        // Update with higher confidence from second provider
        ThreatIndicatorDto dto2 = ThreatIndicatorDto.builder()
                .canonicalValue(iocValue)
                .indicatorType("DOMAIN")
                .category("PHISHING")
                .severity("CRITICAL")
                .aggregatedConfidence(95) // Higher confidence
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        ThreatIndicatorSourceDto src2 = ThreatIndicatorSourceDto.builder()
                .providerId("PHISHTANK")
                .providerConfidence(95)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        ThreatIndicatorEntity updated = threatIndicatorService.saveOrUpdateIndicator(dto2, src2);
        assertEquals(95, updated.getAggregatedConfidence());

        // Verify indicator sources size
        assertEquals(2, updated.getSources().size());
    }

    @Test
    @DisplayName("ST2-03: Save Threat Alert and Verify Composite Idempotency Guard")
    void testThreatAlertStorageIdempotency() {
        // Save dummy event
        EventEntity event = EventEntity.builder()
                .id("EVT-ST2-101")
                .eventType("SECURITY_EVENT")
                .schemaVersion("1.0")
                .timestamp(Instant.now())
                .source("TEST")
                .payloadJson("{}")
                .status("PROCESSED")
                .build();
        eventRepository.saveAndFlush(event);

        // Save dummy indicator
        ThreatIndicatorEntity indicator = ThreatIndicatorEntity.builder()
                .id("IOC-ST2-101")
                .canonicalValue("45.142.120.12")
                .indicatorType("IP_ADDRESS")
                .category("BOTNET")
                .severity("HIGH")
                .aggregatedConfidence(90)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();
        threatIndicatorRepository.saveAndFlush(indicator);

        ThreatAlertDto alertDto = ThreatAlertDto.builder()
                .eventId("EVT-ST2-101")
                .indicatorId("IOC-ST2-101")
                .ruleCode("RUL-CTI-001")
                .severity("CRITICAL")
                .threatScore(30)
                .threatDetailsJson("{}")
                .status("NEW")
                .build();

        // First Save -> Must succeed
        Optional<ThreatAlertEntity> savedOpt = threatAlertStorageService.saveThreatAlert(alertDto);
        assertTrue(savedOpt.isPresent());

        // Second Save (Same eventId + indicatorId + ruleCode) -> Must return empty optional due to idempotency guard
        Optional<ThreatAlertEntity> duplicateOpt = threatAlertStorageService.saveThreatAlert(alertDto);
        assertFalse(duplicateOpt.isPresent(), "Duplicate composite threat alert submission MUST return empty Optional");
    }
}
