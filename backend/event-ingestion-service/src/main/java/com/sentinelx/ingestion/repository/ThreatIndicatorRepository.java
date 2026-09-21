package com.sentinelx.ingestion.repository;

import com.sentinelx.ingestion.model.ThreatIndicatorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThreatIndicatorRepository extends JpaRepository<ThreatIndicatorEntity, String> {
    Optional<ThreatIndicatorEntity> findByIndicatorTypeAndCanonicalValue(String indicatorType, String canonicalValue);
    boolean existsByIndicatorTypeAndCanonicalValue(String indicatorType, String canonicalValue);
    Page<ThreatIndicatorEntity> findByIndicatorTypeOrderByLastSeenDesc(String indicatorType, Pageable pageable);
    Page<ThreatIndicatorEntity> findByAggregatedConfidenceGreaterThanEqualOrderByLastSeenDesc(Integer minConfidence, Pageable pageable);
}
