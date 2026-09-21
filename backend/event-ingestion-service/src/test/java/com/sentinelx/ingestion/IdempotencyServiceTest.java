package com.sentinelx.ingestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import com.sentinelx.ingestion.model.EventEntity;
import com.sentinelx.ingestion.repository.EventRepository;
import com.sentinelx.ingestion.service.IdempotencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private IdempotencyService idempotencyService;

    @Test
    void testNewEvent_ProcessedAndPersisted() throws Exception {
        EventEnvelopeDto dto = EventEnvelopeDto.builder()
                .eventId("EVT-1001")
                .eventType("TRANSACTION")
                .schemaVersion("1.0")
                .timestamp(Instant.now())
                .source("TEST")
                .payload(new HashMap<>())
                .build();

        when(eventRepository.existsById("EVT-1001")).thenReturn(false);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        boolean result = idempotencyService.processAndPersist(dto);

        assertTrue(result, "New event should be successfully processed and persisted");
        verify(eventRepository, times(1)).save(any(EventEntity.class));
    }

    @Test
    void testDuplicateEvent_Skipped() {
        EventEnvelopeDto dto = EventEnvelopeDto.builder()
                .eventId("EVT-1001")
                .eventType("TRANSACTION")
                .schemaVersion("1.0")
                .timestamp(Instant.now())
                .source("TEST")
                .payload(new HashMap<>())
                .build();

        when(eventRepository.existsById("EVT-1001")).thenReturn(true);

        boolean result = idempotencyService.processAndPersist(dto);

        assertFalse(result, "Duplicate event must return false and skip saving");
        verify(eventRepository, never()).save(any());
    }
}
