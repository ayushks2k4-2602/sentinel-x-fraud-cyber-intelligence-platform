package com.sentinelx.ingestion.repository;

import com.sentinelx.ingestion.model.ThreatIndicatorSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThreatIndicatorSourceRepository extends JpaRepository<ThreatIndicatorSourceEntity, String> {
    List<ThreatIndicatorSourceEntity> findByIndicatorId(String indicatorId);
    Optional<ThreatIndicatorSourceEntity> findByIndicatorIdAndProviderId(String indicatorId, String providerId);
    boolean existsByIndicatorIdAndProviderId(String indicatorId, String providerId);
}
