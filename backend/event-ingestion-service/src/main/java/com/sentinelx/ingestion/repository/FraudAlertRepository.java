package com.sentinelx.ingestion.repository;

import com.sentinelx.ingestion.model.FraudAlertEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlertEntity, String> {
    boolean existsByEventId(String eventId);
    Optional<FraudAlertEntity> findByEventId(String eventId);
    Page<FraudAlertEntity> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    Page<FraudAlertEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
