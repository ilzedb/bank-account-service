package com.example.bank_account_service.service;

import com.example.bank_account_service.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditCleanupService {

    private final AuditRepository auditRepository;

    @Value("${application.retention:90}")
    private long retentionDays;

    @Scheduled(cron = "0 0 2 * * SUN")
    @Transactional
    public void cleanupOldLogs() {
        OffsetDateTime retentionLimit = OffsetDateTime.now().minusDays(retentionDays);

        log.info("Starting audit log cleanup for records older than {}", retentionLimit);

        try {
            int deletedCount = auditRepository.deleteByTimestampBefore(retentionLimit);
            log.info("Cleanup successful. Removed {} audit records.", deletedCount);
        } catch (Exception e) {
            log.error("Failed to cleanup audit logs", e);
        }
    }
}
