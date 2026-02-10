package com.example.bank_account_service.service;

import com.example.bank_account_service.model.entity.AuditLog;
import com.example.bank_account_service.repository.AuditRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "application.retention=7")
class AuditCleanupServiceIT {
    private static final int COUNT_RECORDS = 3;
    @Autowired
    private AuditCleanupService auditCleanupService;

    @MockitoBean
    private AuditRepository auditRepository;

    @Test
    void testCleanup() {
        when(auditRepository.deleteByTimestampBefore(any(OffsetDateTime.class))).thenReturn(COUNT_RECORDS);

        auditCleanupService.cleanupOldLogs();

        verify(auditRepository, times(1)).deleteByTimestampBefore(any(OffsetDateTime.class));
    }
}
