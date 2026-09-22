package com.sentinelx.ingestion.service;

import com.sentinelx.ingestion.dto.ThreatAlertDto;
import com.sentinelx.ingestion.model.ThreatAlertEntity;
import com.sentinelx.ingestion.repository.ThreatAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThreatAlertStorageService {

    private final ThreatAlertRepository threatAlertRepository;

    /**
     * Stores a cyber threat alert with composite idempotency guard (eventId + indicatorId + ruleCode).
     */
    @Transactional
    public Optional<ThreatAlertEntity> saveThreatAlert(ThreatAlertDto dto) {
        if (threatAlertRepository.existsByEventIdAndIndicatorIdAndRuleCode(dto.getEventId(), dto.getIndicatorId(), dto.getRuleCode())) {
            log.warn("Composite Idempotency Guard: Threat Alert for eventId [{}], indicatorId [{}], ruleCode [{}] already exists.",
                    dto.getEventId(), dto.getIndicatorId(), dto.getRuleCode());
            return Optional.empty();
        }

        try {
            String alertId = dto.getId() != null ? dto.getId() : "ALT-THREAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            ThreatAlertEntity entity = ThreatAlertEntity.builder()
                    .id(alertId)
                    .eventId(dto.getEventId())
                    .indicatorId(dto.getIndicatorId())
                    .ruleCode(dto.getRuleCode())
                    .severity(dto.getSeverity())
                    .threatScore(dto.getThreatScore())
                    .threatDetailsJson(dto.getThreatDetailsJson())
                    .status(dto.getStatus() != null ? dto.getStatus() : "NEW")
                    .build();

            ThreatAlertEntity saved = threatAlertRepository.save(entity);
            log.info("Threat Alert [{}] stored successfully.", alertId);
            return Optional.of(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Race condition composite idempotency catch for threat alert eventId [{}]", dto.getEventId());
            return Optional.empty();
        }
    }

    public Page<ThreatAlertEntity> getThreatAlerts(String status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            return threatAlertRepository.findByStatusOrderByCreatedAtDesc(status.toUpperCase(), pageable);
        }
        return threatAlertRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
