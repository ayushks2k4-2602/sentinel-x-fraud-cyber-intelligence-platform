package com.sentinelx.ingestion.service;

import com.sentinelx.ingestion.dto.ThreatIndicatorDto;
import com.sentinelx.ingestion.dto.ThreatIndicatorSourceDto;
import com.sentinelx.ingestion.model.ThreatIndicatorEntity;
import com.sentinelx.ingestion.model.ThreatIndicatorSourceEntity;
import com.sentinelx.ingestion.repository.ThreatIndicatorRepository;
import com.sentinelx.ingestion.repository.ThreatIndicatorSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThreatIndicatorService {

    private final ThreatIndicatorRepository threatIndicatorRepository;
    private final ThreatIndicatorSourceRepository threatIndicatorSourceRepository;

    /**
     * Saves or updates a canonical threat indicator and attaches the provider source provenance record.
     */
    @Transactional
    public ThreatIndicatorEntity saveOrUpdateIndicator(ThreatIndicatorDto dto, ThreatIndicatorSourceDto sourceDto) {
        log.info("Saving/Updating CTI Indicator [{}] of type [{}] from provider [{}]", 
                dto.getCanonicalValue(), dto.getIndicatorType(), sourceDto != null ? sourceDto.getProviderId() : "SYNTHETIC");

        Optional<ThreatIndicatorEntity> existingOpt = threatIndicatorRepository
                .findByIndicatorTypeAndCanonicalValue(dto.getIndicatorType(), dto.getCanonicalValue());

        ThreatIndicatorEntity entity;
        if (existingOpt.isPresent()) {
            entity = existingOpt.get();
            // Update lastSeen, severity if higher, and aggregated confidence
            entity.setLastSeen(dto.getLastSeen());
            if (dto.getAggregatedConfidence() > entity.getAggregatedConfidence()) {
                entity.setAggregatedConfidence(dto.getAggregatedConfidence());
            }
            if (dto.getCampaignName() != null) {
                entity.setCampaignName(dto.getCampaignName());
            }
            if (dto.getMitreTechnique() != null) {
                entity.setMitreTechnique(dto.getMitreTechnique());
            }
        } else {
            String id = dto.getId() != null ? dto.getId() : "IOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            entity = ThreatIndicatorEntity.builder()
                    .id(id)
                    .canonicalValue(dto.getCanonicalValue())
                    .indicatorType(dto.getIndicatorType())
                    .category(dto.getCategory())
                    .severity(dto.getSeverity())
                    .aggregatedConfidence(dto.getAggregatedConfidence())
                    .campaignName(dto.getCampaignName())
                    .mitreTechnique(dto.getMitreTechnique())
                    .firstSeen(dto.getFirstSeen() != null ? dto.getFirstSeen() : Instant.now())
                    .lastSeen(dto.getLastSeen() != null ? dto.getLastSeen() : Instant.now())
                    .expiresAt(dto.getExpiresAt())
                    .build();
        }

        ThreatIndicatorEntity saved = threatIndicatorRepository.save(entity);

        // Attach provider source provenance if provided
        if (sourceDto != null && sourceDto.getProviderId() != null) {
            attachProviderSource(saved, sourceDto);
        }

        return saved;
    }

    @Transactional
    public ThreatIndicatorSourceEntity attachProviderSource(ThreatIndicatorEntity indicator, ThreatIndicatorSourceDto sourceDto) {
        Optional<ThreatIndicatorSourceEntity> existingSrcOpt = threatIndicatorSourceRepository
                .findByIndicatorIdAndProviderId(indicator.getId(), sourceDto.getProviderId());

        ThreatIndicatorSourceEntity sourceEntity;
        if (existingSrcOpt.isPresent()) {
            sourceEntity = existingSrcOpt.get();
            sourceEntity.setLastSeen(sourceDto.getLastSeen() != null ? sourceDto.getLastSeen() : Instant.now());
            sourceEntity.setProviderConfidence(sourceDto.getProviderConfidence());
            if (sourceDto.getSourceMetadataJson() != null) {
                sourceEntity.setSourceMetadataJson(sourceDto.getSourceMetadataJson());
            }
        } else {
            String srcId = sourceDto.getId() != null ? sourceDto.getId() : "SRC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            sourceEntity = ThreatIndicatorSourceEntity.builder()
                    .id(srcId)
                    .indicator(indicator)
                    .providerId(sourceDto.getProviderId())
                    .providerConfidence(sourceDto.getProviderConfidence())
                    .sourceMetadataJson(sourceDto.getSourceMetadataJson())
                    .firstSeen(sourceDto.getFirstSeen() != null ? sourceDto.getFirstSeen() : Instant.now())
                    .lastSeen(sourceDto.getLastSeen() != null ? sourceDto.getLastSeen() : Instant.now())
                    .build();
        }

        ThreatIndicatorSourceEntity savedSource = threatIndicatorSourceRepository.save(sourceEntity);
        if (indicator.getSources() != null) {
            indicator.getSources().removeIf(s -> s.getId().equals(savedSource.getId()) || (s.getProviderId() != null && s.getProviderId().equals(savedSource.getProviderId())));
            indicator.getSources().add(savedSource);
        }
        return savedSource;
    }

    public Optional<ThreatIndicatorEntity> findByCanonicalValueAndType(String indicatorType, String canonicalValue) {
        return threatIndicatorRepository.findByIndicatorTypeAndCanonicalValue(indicatorType, canonicalValue);
    }

    public Page<ThreatIndicatorEntity> getIndicators(String indicatorType, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        if (indicatorType != null && !indicatorType.equalsIgnoreCase("ALL")) {
            return threatIndicatorRepository.findByIndicatorTypeOrderByLastSeenDesc(indicatorType.toUpperCase(), pageable);
        }
        return threatIndicatorRepository.findAll(pageable);
    }
}
