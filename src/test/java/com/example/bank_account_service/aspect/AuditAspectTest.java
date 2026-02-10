package com.example.bank_account_service.aspect;

import com.example.bank_account_service.annotation.Auditable;
import com.example.bank_account_service.model.entity.AuditLog;
import com.example.bank_account_service.repository.AuditRepository;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {
    private static final String USER_ID = "test-user";
    private static final String TRANSACTION_ID = "ID";
    private static final String ACTION = "my-action";

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Auditable auditable;

    @InjectMocks
    private AuditAspect auditAspect;

    @Test
    void testAudit() {

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(auth.getName()).thenReturn(USER_ID);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(auditable.action()).thenReturn(ACTION);

        auditAspect.audit(joinPoint, auditable, TRANSACTION_ID);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertEquals(USER_ID, savedLog.getUsername());
        assertEquals(ACTION, savedLog.getAction());
        assertEquals(TRANSACTION_ID, savedLog.getTransactionId());
        assertNotNull(savedLog.getTimestamp());

        SecurityContextHolder.clearContext();
    }
}
