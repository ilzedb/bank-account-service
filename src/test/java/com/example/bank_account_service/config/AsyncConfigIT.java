package com.example.bank_account_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AsyncConfigIT {
    @Autowired
    private Executor auditTaskExecutor;

    @Test
    void testConfiguration() {
        assertThat(auditTaskExecutor).isInstanceOf(ThreadPoolTaskExecutor.class);
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) auditTaskExecutor;

        assertThat(executor.getCorePoolSize()).isEqualTo(5);
        assertThat(executor.getMaxPoolSize()).isEqualTo(10);
        assertThat(executor.getThreadNamePrefix()).isEqualTo("AuditThread-");
    }
}
