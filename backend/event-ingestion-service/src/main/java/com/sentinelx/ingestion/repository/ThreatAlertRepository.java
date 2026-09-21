package com.sentinelx.ingestion.repository;

import com.sentinelx.ingestion.model.ThreatAlertEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreatAlertRepository extends JpaRepository<ThreatAlertEntity, String> {
    boolean existsByEventIdAndIndicatorIdAndRuleCode(String eventId, String indicatorId, String ruleCode);
    List<ThreatAlertEntity> findByEventId(String eventId);
    Page<ThreatAlertEntity> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    Page<ThreatAlertEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
