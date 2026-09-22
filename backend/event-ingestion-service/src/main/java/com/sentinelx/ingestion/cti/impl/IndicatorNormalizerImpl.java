package com.sentinelx.ingestion.cti.impl;

import com.sentinelx.ingestion.cti.IndicatorNormalizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
@Component
public class IndicatorNormalizerImpl implements IndicatorNormalizer {

    private static final Pattern IPV4_PATTERN = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9a-fA-F]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public String normalize(String rawValue, String indicatorType) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("Raw indicator value cannot be null or blank");
        }
        if (indicatorType == null || indicatorType.isBlank()) {
            throw new IllegalArgumentException("Indicator type cannot be null or blank");
        }

        String type = indicatorType.toUpperCase(Locale.ROOT).trim();
        return switch (type) {
            case "IP_ADDRESS", "IP" -> normalizeIpAddress(rawValue);
            case "DOMAIN", "FQDN" -> normalizeDomain(rawValue);
            case "URL" -> normalizeUrl(rawValue);
            case "FILE_HASH", "HASH", "MD5", "SHA256", "SHA-256" -> normalizeFileHash(rawValue);
            case "EMAIL" -> normalizeEmail(rawValue);
            default -> throw new IllegalArgumentException("Unsupported indicator type: " + indicatorType);
        };
    }

    @Override
    public boolean isValid(String rawValue, String indicatorType) {
        try {
            normalize(rawValue, indicatorType);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String normalizeIpAddress(String raw) {
        String trimmed = raw.trim();

        // 1. Try IPv4
        var matcher = IPV4_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            int octet1 = Integer.parseInt(matcher.group(1));
            int octet2 = Integer.parseInt(matcher.group(2));
            int octet3 = Integer.parseInt(matcher.group(3));
            int octet4 = Integer.parseInt(matcher.group(4));

            if (octet1 <= 255 && octet2 <= 255 && octet3 <= 255 && octet4 <= 255) {
                return String.format(Locale.ROOT, "%d.%d.%d.%d", octet1, octet2, octet3, octet4);
            } else {
                throw new IllegalArgumentException("IPv4 octet out of range (0-255): " + raw);
            }
        }

        // 2. Try IPv6
        try {
            InetAddress addr = InetAddress.getByName(trimmed);
            if (addr instanceof Inet6Address inet6Address) {
                return formatRfc5952(inet6Address);
            }
        } catch (Exception ignored) {
        }

        throw new IllegalArgumentException("Invalid IP address format: " + raw);
    }

    /**
     * Formats IPv6 address per RFC 5952 (lowercase compressed).
     */
    private String formatRfc5952(Inet6Address addr) {
        byte[] bytes = addr.getAddress();
        int[] hextets = new int[8];
        for (int i = 0; i < 8; i++) {
            hextets[i] = ((bytes[i * 2] & 0xFF) << 8) | (bytes[i * 2 + 1] & 0xFF);
        }

        // Find longest sequence of zeroes (length >= 2)
        int bestZeroStart = -1;
        int bestZeroLength = 0;
        int currentZeroStart = -1;
        int currentZeroLength = 0;

        for (int i = 0; i < 8; i++) {
            if (hextets[i] == 0) {
                if (currentZeroLength == 0) {
                    currentZeroStart = i;
                }
                currentZeroLength++;
                if (currentZeroLength > bestZeroLength) {
                    bestZeroLength = currentZeroLength;
                    bestZeroStart = currentZeroStart;
                }
            } else {
                currentZeroLength = 0;
            }
        }

        // Only compress if run is >= 2
        if (bestZeroLength < 2) {
            bestZeroStart = -1;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (i == bestZeroStart) {
                sb.append("::");
                i += bestZeroLength - 1;
                continue;
            }
            if (i > 0 && !(i == bestZeroStart + bestZeroLength && sb.length() > 0 && sb.charAt(sb.length() - 1) == ':')) {
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ':') {
                    sb.append(":");
                }
            }
            sb.append(Integer.toHexString(hextets[i]));
        }

        return sb.toString();
    }

    private String normalizeDomain(String raw) {
        String cleaned = raw.trim().toLowerCase(Locale.ROOT);
        if (cleaned.startsWith("http://")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("https://")) {
            cleaned = cleaned.substring(8);
        }

        // Strip path, query, or port if present
        int slashIdx = cleaned.indexOf('/');
        if (slashIdx != -1) {
            cleaned = cleaned.substring(0, slashIdx);
        }
        int colonIdx = cleaned.indexOf(':');
        if (colonIdx != -1) {
            cleaned = cleaned.substring(0, colonIdx);
        }

        // Strip trailing dot
        if (cleaned.endsWith(".")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        if (cleaned.isBlank() || !cleaned.contains(".") || cleaned.startsWith(".") || cleaned.endsWith(".")) {
            throw new IllegalArgumentException("Invalid domain format: " + raw);
        }

        return cleaned;
    }

    private String normalizeUrl(String raw) {
        String trimmed = raw.trim();
        try {
            URI uri = URI.create(trimmed);
            String scheme = uri.getScheme();
            if (scheme == null) {
                uri = URI.create("http://" + trimmed);
                scheme = uri.getScheme();
            }

            scheme = scheme.toLowerCase(Locale.ROOT);
            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                throw new IllegalArgumentException("URL missing host: " + raw);
            }
            host = host.toLowerCase(Locale.ROOT);

            int port = uri.getPort();
            StringBuilder sb = new StringBuilder();
            sb.append(scheme).append("://").append(host);

            // Omit default ports (80 for http, 443 for https)
            if (port != -1) {
                if (!("http".equals(scheme) && port == 80) && !("https".equals(scheme) && port == 443)) {
                    sb.append(":").append(port);
                }
            }

            String path = uri.getRawPath();
            if (path != null && !path.isEmpty()) {
                sb.append(path);
            }
            String query = uri.getRawQuery();
            if (query != null && !query.isEmpty()) {
                sb.append("?").append(query);
            }
            String fragment = uri.getRawFragment();
            if (fragment != null && !fragment.isEmpty()) {
                sb.append("#").append(fragment);
            }

            return sb.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format: " + raw, e);
        }
    }

    private String normalizeFileHash(String raw) {
        String cleaned = raw.trim().toLowerCase(Locale.ROOT);
        if (!HEX_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("File hash contains non-hexadecimal characters: " + raw);
        }

        int len = cleaned.length();
        if (len != 32 && len != 40 && len != 64) {
            throw new IllegalArgumentException("Invalid file hash length (" + len + "). Expected MD5 (32), SHA-1 (40), or SHA-256 (64): " + raw);
        }

        return cleaned;
    }

    private String normalizeEmail(String raw) {
        String cleaned = raw.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + raw);
        }
        return cleaned;
    }
}
