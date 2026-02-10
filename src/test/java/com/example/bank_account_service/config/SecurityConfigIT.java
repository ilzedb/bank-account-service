package com.example.bank_account_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityConfigIT {
    @Autowired
    private ApplicationContext context;

    @Test
    void testSecurityConfig() {
        assertThat(context.containsBean("securityConfig")).isTrue();
        assertThat(context.getBean(JwtProperties.class)).isNotNull();
    }
}
