package com.example.bank_account_service.aspect;

import com.example.bank_account_service.annotation.Auditable;
import com.example.bank_account_service.model.entity.AuditLog;
import com.example.bank_account_service.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditRepository auditRepository;

    @Async("auditTaskExecutor")
    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void audit(JoinPoint joinPoint, Auditable auditable, Object result) {

        try {
            String resolvedAction = auditable.action();

            String currentPrincipalName = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                    .map(Authentication::getName)
                    .orElse("anonymous");

            auditRepository.save(AuditLog.builder()
                    .username(currentPrincipalName)
                    .transactionId(result != null ? result.toString() : UUID.randomUUID().toString())
                    .action(resolvedAction)
                    .timestamp(OffsetDateTime.now())
                    .build());

            log.info("AUDIT EVENT: Action {} completed successfully with transaction {}", resolvedAction, result);
        } catch (Exception e){
            log.error("Failed to create audit log: {}", e.getMessage());
        }
    }
}