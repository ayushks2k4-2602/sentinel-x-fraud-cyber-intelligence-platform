package com.sentinelx.ingestion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import com.sentinelx.ingestion.model.EventEntity;
import com.sentinelx.ingestion.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Verifies idempotency and persists event atomically.
     * Returns true if newly persisted, false if duplicate skipped.
     */
    @Transactional
    public boolean processAndPersist(EventEnvelopeDto event) {
        if (eventRepository.existsById(event.getEventId())) {
            log.warn("Idempotency Guard: Event [{}] already exists in PostgreSQL database. Skipping duplicate processing.", event.getEventId());
            return false;
        }

        try {
            String payloadJson = objectMapper.writeValueAsString(event.getPayload());
            EventEntity entity = EventEntity.builder()
                    .id(event.getEventId())
                    .eventType(event.getEventType())
                    .schemaVersion(event.getSchemaVersion())
                    .timestamp(event.getTimestamp())
                    .source(event.getSource())
                    .accountId(event.getAccountId())
                    .deviceId(event.getDeviceId())
                    .ipAddress(event.getIpAddress())
                    .payloadJson(payloadJson)
                    .status("PROCESSED")
                    .build();

            eventRepository.save(entity);
            log.info("Event [{}] persisted successfully to PostgreSQL database.", event.getEventId());
            return true;
        } catch (DataIntegrityViolationException ex) {
            log.warn("Race condition idempotency catch: Event [{}] already saved concurrently.", event.getEventId());
            return false;
        } catch (Exception ex) {
            log.error("Error serializing/persisting event [{}]", event.getEventId(), ex);
            throw new RuntimeException("Failed to persist event", ex);
        }
    }
}
