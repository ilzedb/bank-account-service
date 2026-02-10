package com.example.bank_account_service.repository;

import com.example.bank_account_service.model.entity.AuditLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class AuditRepositoryIT {
    private static final String USER_ID = "test-user";
    private static final String TRANSACTION_ID = "ID";
    private static final String ACTION = "my-action";
    @Autowired
    private AuditRepository auditRepository;

    @Test
    void testAuditRepositorySave() {
        AuditLog log = AuditLog.builder()
                .username(USER_ID)
                .action(ACTION)
                .transactionId(TRANSACTION_ID)
                .timestamp(OffsetDateTime.now())
                .build();

        AuditLog saved = auditRepository.save(log);

        Optional<AuditLog> found = auditRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(USER_ID, found.get().getUsername());
        assertEquals(ACTION, found.get().getAction());
        assertEquals(TRANSACTION_ID, found.get().getTransactionId());
    }

    @Test
    void testCleanup() {
        auditRepository.save(AuditLog.builder()
                .username(USER_ID)
                .action(ACTION)
                .transactionId(TRANSACTION_ID)
                .timestamp(OffsetDateTime.now().minusDays(100)).build());
        auditRepository.save(AuditLog.builder()
                .username(USER_ID)
                .action(ACTION)
                .transactionId(TRANSACTION_ID)
                .timestamp(OffsetDateTime.now()).build());

        auditRepository.deleteByTimestampBefore(OffsetDateTime.now().minusDays(90));

        assertEquals(1, auditRepository.count());
    }
}
