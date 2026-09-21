package com.sentinelx.ingestion.repository;

import com.sentinelx.ingestion.model.DeadLetterRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeadLetterRepository extends JpaRepository<DeadLetterRecord, String> {
}
