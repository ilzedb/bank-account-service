package com.example.bank_account_service.aspect;

import com.example.bank_account_service.annotation.Auditable;
import com.example.bank_account_service.model.entity.AuditLog;
import com.example.bank_account_service.repository.AuditRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {AuditAspect.class, AuditAspectIT.TestService.class})
@EnableAspectJAutoProxy
class AuditAspectIT {

    @Autowired
    private TestService testService;

    @MockitoBean
    private AuditRepository auditRepository;

    // A dummy service to simulate a real-world trigger for @Auditable
    @Service
    public static class TestService {
        @Auditable(action = "TEST_ACTION")
        public String performAction() {
            return "TX-999";
        }
    }

    @Test
    void testAspectAuditableAnnotation() {
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(auth.getName()).thenReturn("integration-user");
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        testService.performAction();

        verify(auditRepository, timeout(1000).times(1)).save(any(AuditLog.class));

        SecurityContextHolder.clearContext();
    }
}
