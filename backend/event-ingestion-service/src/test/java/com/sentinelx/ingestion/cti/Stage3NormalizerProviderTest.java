package com.sentinelx.ingestion.cti;

import com.sentinelx.ingestion.cti.impl.IndicatorNormalizerImpl;
import com.sentinelx.ingestion.cti.impl.SyntheticThreatProvider;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class Stage3NormalizerProviderTest {

    @Autowired
    private IndicatorNormalizerImpl normalizer;

    @Autowired
    private SyntheticThreatProvider syntheticProvider;

    @Autowired
    private ProvenanceDeduplicationService provenanceDeduplicationService;

    @Autowired
    private ThreatIndicatorRepository threatIndicatorRepository;

    @Autowired
    private ThreatIndicatorSourceRepository threatIndicatorSourceRepository;

    // --- 1. IndicatorNormalizer Tests ---

    @Test
    @DisplayName("ST3-01: IPv4 Normalization & Validation")
    void testIpv4Normalization() {
        String raw = " 185.220.101.005 ";
        String normalized = normalizer.normalize(raw, "IP_ADDRESS");
        assertEquals("185.220.101.5", normalized);
        assertTrue(normalizer.isValid(raw, "IP_ADDRESS"));

        // Invalid IPv4
        assertFalse(normalizer.isValid("256.1.1.1", "IP_ADDRESS"));
        assertFalse(normalizer.isValid("185.220.101", "IP_ADDRESS"));
        assertThrows(IllegalArgumentException.class, () -> normalizer.normalize("invalid_ip", "IP_ADDRESS"));
    }

    @Test
    @DisplayName("ST3-02: IPv6 Normalization RFC 5952")
    void testIpv6Normalization() {
        String raw = "2001:0DB8:0000:0000:0000:FF00:0420:8329";
        String normalized = normalizer.normalize(raw, "IP_ADDRESS");
        assertEquals("2001:db8::ff00:420:8329", normalized);

        String loopback = "0:0:0:0:0:0:0:1";
        assertEquals("::1", normalizer.normalize(loopback, "IP_ADDRESS"));

        assertFalse(normalizer.isValid("2001:xyz::1", "IP_ADDRESS"));
    }

    @Test
    @DisplayName("ST3-03: DOMAIN Normalization")
    void testDomainNormalization() {
        String raw = "Auth-Verify.Bank-Update.COM.";
        String normalized = normalizer.normalize(raw, "DOMAIN");
        assertEquals("auth-verify.bank-update.com", normalized);

        String rawUrlDomain = "https://phish-bank.com/login";
        assertEquals("phish-bank.com", normalizer.normalize(rawUrlDomain, "DOMAIN"));

        assertFalse(normalizer.isValid("invalid_domain_no_dot", "DOMAIN"));
    }

    @Test
    @DisplayName("ST3-04: URL Normalization")
    void testUrlNormalization() {
        String raw = "HTTP://Example.COM:80/Phish?User=A";
        String normalized = normalizer.normalize(raw, "URL");
        assertEquals("http://example.com/Phish?User=A", normalized);

        String httpsDefault = "HTTPS://Test.COM:443/login#ref";
        assertEquals("https://test.com/login#ref", normalizer.normalize(httpsDefault, "URL"));

        String customPort = "HTTPS://Test.COM:8443/login";
        assertEquals("https://test.com:8443/login", normalizer.normalize(customPort, "URL"));
    }

    @Test
    @DisplayName("ST3-05: FILE_HASH Normalization (MD5 & SHA-256)")
    void testFileHashNormalization() {
        String md5Raw = "E3B0C44298FC1C149AFBF4C8996FB924";
        assertEquals("e3b0c44298fc1c149afbf4c8996fb924", normalizer.normalize(md5Raw, "FILE_HASH"));

        String sha256Raw = "E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855";
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", normalizer.normalize(sha256Raw, "FILE_HASH"));

        // Invalid non-hex & invalid length
        assertFalse(normalizer.isValid("E3B0C44298FC1C149AFBF4C8996FB924XYZ", "FILE_HASH"));
        assertFalse(normalizer.isValid("E3B0C44", "FILE_HASH"));
    }

    @Test
    @DisplayName("ST3-06: EMAIL Normalization")
    void testEmailNormalization() {
        String raw = " VictimUser@ENTERPRISE.com ";
        assertEquals("victimuser@enterprise.com", normalizer.normalize(raw, "EMAIL"));

        assertFalse(normalizer.isValid("invalid_email_no_at.com", "EMAIL"));
    }

    // --- 2. ThreatIntelProvider Tests ---

    @Test
    @DisplayName("ST3-07: SyntheticThreatProvider Operations")
    void testSyntheticThreatProvider() {
        assertEquals("SYNTHETIC", syntheticProvider.getProviderId());
        assertTrue(syntheticProvider.supportsType("IP_ADDRESS"));
        assertTrue(syntheticProvider.supportsType("DOMAIN"));
        assertTrue(syntheticProvider.supportsType("URL"));

        List<RawIndicatorDto> indicators = syntheticProvider.fetchIndicators();
        assertNotNull(indicators);
        assertEquals(5, indicators.size());
    }

    // --- 3. ProvenanceDeduplicationService Tests ---

    @Test
    @DisplayName("ST3-08: Multi-Source Provenance & Aggregated Confidence Consensus")
    void testMultiSourceProvenanceDeduplication() {
        RawIndicatorDto raw1 = RawIndicatorDto.builder()
                .rawValue(" 185.220.101.005 ")
                .indicatorType("IP_ADDRESS")
                .category("TOR_EXIT_NODE")
                .severity("HIGH")
                .confidence(80)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        // 1. First provider ingestion (ALIENVAULT)
        ThreatIndicatorEntity ind1 = provenanceDeduplicationService.processAndDeduplicate(raw1, "ALIENVAULT");
        assertEquals("185.220.101.5", ind1.getCanonicalValue());
        assertEquals(80, ind1.getAggregatedConfidence());

        // 2. Second provider ingestion for SAME canonical indicator (VIRUSTOTAL with conf 85)
        RawIndicatorDto raw2 = RawIndicatorDto.builder()
                .rawValue("185.220.101.5")
                .indicatorType("IP_ADDRESS")
                .category("TOR_EXIT_NODE")
                .severity("CRITICAL")
                .confidence(85)
                .firstSeen(Instant.now())
                .lastSeen(Instant.now())
                .build();

        ThreatIndicatorEntity ind2 = provenanceDeduplicationService.processAndDeduplicate(raw2, "VIRUSTOTAL");
        
        // Single canonical indicator record in DB
        assertEquals(ind1.getId(), ind2.getId());

        // Consensus formula: max(80, 85) + 5*(2-1) = 85 + 5 = 90
        assertEquals(90, ind2.getAggregatedConfidence());

        // Provenance sources count = 2
        List<ThreatIndicatorSourceEntity> sources = threatIndicatorSourceRepository.findByIndicatorId(ind2.getId());
        assertEquals(2, sources.size());
    }

    @Test
    @DisplayName("ST3-09: Time Decay Penalty Calculation")
    void testTimeDecayPenalty() {
        Instant oldLastSeen = Instant.now().minus(45, ChronoUnit.DAYS); // 45 days ago -> 15 days past 30 days -> 15 / 7 = 2 penalty points

        RawIndicatorDto rawOld = RawIndicatorDto.builder()
                .rawValue("45.142.120.10")
                .indicatorType("IP_ADDRESS")
                .category("BOTNET")
                .severity("MEDIUM")
                .confidence(80)
                .firstSeen(oldLastSeen)
                .lastSeen(oldLastSeen)
                .build();

        ThreatIndicatorEntity entity = provenanceDeduplicationService.processAndDeduplicate(rawOld, "THREAT_CONNECT");
        // Base conf 80 - decay penalty 2 = 78
        assertEquals(78, entity.getAggregatedConfidence());
    }
}
