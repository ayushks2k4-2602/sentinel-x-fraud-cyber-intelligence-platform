package com.sentinelx.ingestion.service;

import com.sentinelx.ingestion.cti.IndicatorNormalizer;
import com.sentinelx.ingestion.dto.RawIndicatorDto;
import com.sentinelx.ingestion.dto.ThreatIndicatorDto;
import com.sentinelx.ingestion.dto.ThreatIndicatorSourceDto;
import com.sentinelx.ingestion.model.ThreatIndicatorEntity;
import com.sentinelx.ingestion.model.ThreatIndicatorSourceEntity;
import com.sentinelx.ingestion.repository.ThreatIndicatorRepository;
import com.sentinelx.ingestion.repository.ThreatIndicatorSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProvenanceDeduplicationService {

    private final IndicatorNormalizer indicatorNormalizer;
    private final ThreatIndicatorService threatIndicatorService;
    private final ThreatIndicatorRepository threatIndicatorRepository;
    private final ThreatIndicatorSourceRepository threatIndicatorSourceRepository;

    /**
     * Ingests a raw indicator from a provider, normalizes it, aggregates provenance across sources,
     * computes consensus confidence score with time decay, and persists the canonical record.
     */
    @Transactional
    public ThreatIndicatorEntity processAndDeduplicate(RawIndicatorDto rawDto, String providerId) {
        if (rawDto == null || rawDto.getRawValue() == null) {
            throw new IllegalArgumentException("Raw indicator cannot be null");
        }
        if (providerId == null || providerId.isBlank()) {
            throw new IllegalArgumentException("Provider ID cannot be null or blank");
        }

        // 1. Normalize & Validate
        String canonicalValue = indicatorNormalizer.normalize(rawDto.getRawValue(), rawDto.getIndicatorType());

        Instant now = Instant.now();
        Instant firstSeen = rawDto.getFirstSeen() != null ? rawDto.getFirstSeen() : now;
        Instant lastSeen = rawDto.getLastSeen() != null ? rawDto.getLastSeen() : now;

        // 2. Check if canonical indicator already exists
        Optional<ThreatIndicatorEntity> existingOpt = threatIndicatorRepository
                .findByIndicatorTypeAndCanonicalValue(rawDto.getIndicatorType(), canonicalValue);

        ThreatIndicatorEntity indicatorEntity;
        if (existingOpt.isPresent()) {
            indicatorEntity = existingOpt.get();
        } else {
            // Create base indicator DTO
            ThreatIndicatorDto indDto = ThreatIndicatorDto.builder()
                    .canonicalValue(canonicalValue)
                    .indicatorType(rawDto.getIndicatorType())
                    .category(rawDto.getCategory())
                    .severity(rawDto.getSeverity() != null ? rawDto.getSeverity() : "MEDIUM")
                    .aggregatedConfidence(rawDto.getConfidence() != null ? rawDto.getConfidence() : 50)
                    .campaignName(rawDto.getCampaignName())
                    .mitreTechnique(rawDto.getMitreTechnique())
                    .firstSeen(firstSeen)
                    .lastSeen(lastSeen)
                    .expiresAt(rawDto.getExpiresAt())
                    .build();

            indicatorEntity = threatIndicatorService.saveOrUpdateIndicator(indDto, null);
        }

        // 3. Attach/Update Provider Source Record
        int providerConf = rawDto.getConfidence() != null ? rawDto.getConfidence() : 50;
        ThreatIndicatorSourceDto sourceDto = ThreatIndicatorSourceDto.builder()
                .providerId(providerId)
                .providerConfidence(providerConf)
                .sourceMetadataJson(rawDto.getSourceMetadataJson())
                .firstSeen(firstSeen)
                .lastSeen(lastSeen)
                .build();

        threatIndicatorService.attachProviderSource(indicatorEntity, sourceDto);

        // 4. Fetch all sources for this canonical indicator to recalculate aggregated confidence and timestamps
        List<ThreatIndicatorSourceEntity> sources = threatIndicatorSourceRepository
                .findByIndicatorId(indicatorEntity.getId());

        int maxConf = 0;
        Instant minFirstSeen = indicatorEntity.getFirstSeen();
        Instant maxLastSeen = indicatorEntity.getLastSeen();

        for (ThreatIndicatorSourceEntity src : sources) {
            if (src.getProviderConfidence() > maxConf) {
                maxConf = src.getProviderConfidence();
            }
            if (src.getFirstSeen().isBefore(minFirstSeen)) {
                minFirstSeen = src.getFirstSeen();
            }
            if (src.getLastSeen().isAfter(maxLastSeen)) {
                maxLastSeen = src.getLastSeen();
            }
        }

        int numSources = sources.size();
        int baseConfidence = Math.min(100, maxConf + 5 * (numSources - 1));

        // Compute decay penalty (1 point per 7 days past 30 days of lastSeen)
        long daysElapsed = Duration.between(maxLastSeen, now).toDays();
        int decayPenalty = 0;
        if (daysElapsed > 30) {
            decayPenalty = (int) ((daysElapsed - 30) / 7);
        }

        int aggregatedConfidence = Math.max(0, Math.min(100, baseConfidence - decayPenalty));

        // Update entity with recalculated aggregated confidence and metadata
        indicatorEntity.setAggregatedConfidence(aggregatedConfidence);
        indicatorEntity.setFirstSeen(minFirstSeen);
        indicatorEntity.setLastSeen(maxLastSeen);
        if (rawDto.getCampaignName() != null) {
            indicatorEntity.setCampaignName(rawDto.getCampaignName());
        }
        if (rawDto.getMitreTechnique() != null) {
            indicatorEntity.setMitreTechnique(rawDto.getMitreTechnique());
        }

        return threatIndicatorRepository.save(indicatorEntity);
    }
}
