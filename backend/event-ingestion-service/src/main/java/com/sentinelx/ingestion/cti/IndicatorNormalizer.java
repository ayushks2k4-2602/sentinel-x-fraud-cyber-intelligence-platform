package com.sentinelx.ingestion.cti;

public interface IndicatorNormalizer {
    /**
     * Deterministically canonicalizes raw indicator strings across supported types:
     * IPv4, IPv6, FQDN/Domain, URL, MD5, SHA-256, and Email.
     */
    String normalize(String rawValue, String indicatorType);
    
    /**
     * Validates whether the raw string matches the format constraints of the indicatorType.
     */
    boolean isValid(String rawValue, String indicatorType);
}
