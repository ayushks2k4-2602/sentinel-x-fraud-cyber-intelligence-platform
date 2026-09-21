package com.sentinelx.ingestion.exception;

public class DuplicateEventException extends RuntimeException {
    public DuplicateEventException(String eventId) {
        super("Event with ID " + eventId + " has already been processed.");
    }
}
