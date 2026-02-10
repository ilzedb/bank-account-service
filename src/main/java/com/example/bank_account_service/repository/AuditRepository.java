package com.example.bank_account_service.repository;

import com.example.bank_account_service.model.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {
    int deleteByTimestampBefore(OffsetDateTime threshold);
}
