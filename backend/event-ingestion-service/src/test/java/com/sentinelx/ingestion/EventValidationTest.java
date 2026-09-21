package com.sentinelx.ingestion;

import com.sentinelx.ingestion.dto.EventEnvelopeDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidEventEnvelope() {
        EventEnvelopeDto dto = EventEnvelopeDto.builder()
                .eventId("EVT-TEST-001")
                .eventType("TRANSACTION")
                .schemaVersion("1.0")
                .timestamp(Instant.now())
                .source("SYNTHETIC_GENERATOR")
                .accountId("ACC-1001")
                .payload(new HashMap<>())
                .build();

        Set<ConstraintViolation<EventEnvelopeDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid envelope should have zero validation violations");
    }

    @Test
    void testMissingEventId_FailsValidation() {
        EventEnvelopeDto dto = EventEnvelopeDto.builder()
                .eventId("") // Blank
                .eventType("TRANSACTION")
                .schemaVersion("1.0")
                .timestamp(Instant.now())
                .source("SYNTHETIC_GENERATOR")
                .payload(new HashMap<>())
                .build();

        Set<ConstraintViolation<EventEnvelopeDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Blank eventId must fail validation");
    }
}
