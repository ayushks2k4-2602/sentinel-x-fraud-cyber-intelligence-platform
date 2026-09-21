package com.sentinelx.ingestion.controller;

import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import com.sentinelx.ingestion.model.EventEntity;
import com.sentinelx.ingestion.repository.EventRepository;
import com.sentinelx.ingestion.service.KafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Event Ingestion API", description = "Endpoints for real-time financial transaction and cyber security event telemetry")
@CrossOrigin(origins = "*")
public class EventIngestionController {

    private final KafkaProducerService kafkaProducerService;
    private final EventRepository eventRepository;

    @PostMapping("/financial")
    @Operation(summary = "Ingest Financial Event", description = "Validates and publishes financial card/IMPS/NEFT payment telemetry to Kafka")
    public ResponseEntity<Map<String, Object>> ingestFinancialEvent(@Valid @RequestBody EventEnvelopeDto event) {
        log.info("Received REST Financial Event [{}] of type [{}]", event.getEventId(), event.getEventType());
        
        kafkaProducerService.sendFinancialEvent(event);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ACCEPTED");
        response.put("eventId", event.getEventId());
        response.put("message", "Event successfully validated and published to sentinel.events.financial");

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/security")
    @Operation(summary = "Ingest Security Event", description = "Validates and publishes cyber security telemetry (logins, IP alerts) to Kafka")
    public ResponseEntity<Map<String, Object>> ingestSecurityEvent(@Valid @RequestBody EventEnvelopeDto event) {
        log.info("Received REST Security Event [{}] of type [{}]", event.getEventId(), event.getEventType());

        kafkaProducerService.sendSecurityEvent(event);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ACCEPTED");
        response.put("eventId", event.getEventId());
        response.put("message", "Event successfully validated and published to sentinel.events.security");

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get Recent Events", description = "Returns top 50 recent ingested events from PostgreSQL for frontend telemetry stream")
    public ResponseEntity<List<EventEntity>> getRecentEvents() {
        return ResponseEntity.ok(eventRepository.findTop50ByOrderByTimestampDesc());
    }
}
