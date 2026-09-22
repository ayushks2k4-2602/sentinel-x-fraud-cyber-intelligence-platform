package com.sentinelx.ingestion.cti;

import com.sentinelx.ingestion.dto.RawIndicatorDto;

import java.time.Instant;
import java.util.List;

public interface ThreatIntelProvider {
    /**
     * Unique identifier for the threat intelligence provider (e.g. ALIENVAULT, VIRUSTOTAL, SYNTHETIC).
     */
    String getProviderId();

    /**
     * Fetches or generates the latest batch of raw threat indicators from this provider.
     */
    List<RawIndicatorDto> fetchIndicators();

    /**
     * Checks if this provider supports the given indicator type.
     */
    boolean supportsType(String indicatorType);

    /**
     * Timestamp of the last sync operation.
     */
    Instant getLastSyncTimestamp();

    /**
     * Default confidence score (0-100) assigned to indicators from this provider if unassigned.
     */
    int getDefaultConfidence();
}
