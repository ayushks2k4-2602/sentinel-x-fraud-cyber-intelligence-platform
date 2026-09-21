package com.sentinelx.ingestion.repository;

import com.sentinelx.ingestion.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, String> {
    boolean existsById(String id);
    List<EventEntity> findTop50ByOrderByTimestampDesc();
}
