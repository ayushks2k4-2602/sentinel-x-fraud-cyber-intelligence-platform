package com.sentinelx.ingestion.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RedisVelocityService {

    // Timestamp-aware Sliding Window Store (Simulates Redis Sorted Sets / ZADD & ZREMRANGEBYSCORE)
    private final Map<String, TreeSet<Long>> slidingWindowStore = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> setMembershipStore = new ConcurrentHashMap<>();

    /**
     * Records a transaction event in sliding window and counts events within windowMillis.
     */
    public synchronized long recordAndCountVelocity(String accountId, long windowMillis) {
        if (accountId == null) return 0;

        String key = "sentinel:velocity:account:" + accountId + ":transactions";
        long nowMs = Instant.now().toEpochMilli();
        long minAllowedMs = nowMs - windowMillis;

        TreeSet<Long> timestamps = slidingWindowStore.computeIfAbsent(key, k -> new TreeSet<>());
        timestamps.add(nowMs);

        // Remove entries outside the required time window
        timestamps.headSet(minAllowedMs, false).clear();

        return timestamps.size();
    }

    /**
     * Records a failed authentication attempt in sliding window.
     */
    public synchronized long recordAndCountFailedAuth(String ipAddress, long windowMillis) {
        if (ipAddress == null) return 0;

        String key = "sentinel:velocity:ip:" + ipAddress + ":failed_logins";
        long nowMs = Instant.now().toEpochMilli();
        long minAllowedMs = nowMs - windowMillis;

        TreeSet<Long> timestamps = slidingWindowStore.computeIfAbsent(key, k -> new TreeSet<>());
        timestamps.add(nowMs);
        timestamps.headSet(minAllowedMs, false).clear();

        return timestamps.size();
    }

    /**
     * Tracks unique accounts per device fingerprint.
     */
    public synchronized long recordAndCountDeviceAccount(String deviceId, String accountId) {
        if (deviceId == null || accountId == null) return 0;

        String key = "sentinel:velocity:device:" + deviceId + ":accounts";
        Set<String> accounts = setMembershipStore.computeIfAbsent(key, k -> new HashSet<>());
        accounts.add(accountId);

        return accounts.size();
    }

    /**
     * Tracks unique accounts per IP address.
     */
    public synchronized long recordAndCountIpAccount(String ipAddress, String accountId) {
        if (ipAddress == null || accountId == null) return 0;

        String key = "sentinel:velocity:ip:" + ipAddress + ":accounts";
        Set<String> accounts = setMembershipStore.computeIfAbsent(key, k -> new HashSet<>());
        accounts.add(accountId);

        return accounts.size();
    }
}
