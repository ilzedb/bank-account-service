package com.example.bank_account_service.service;

import com.example.bank_account_service.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String secret = Base64.getEncoder().encodeToString("12345678901234567890123456789012345".getBytes());
        when(jwtProperties.getSecretKey()).thenReturn(secret);
        when(jwtProperties.getExpiration()).thenReturn(3600000L);
        jwtService = new JwtService(jwtProperties);
    }

    @Test
    void testGenerateAndValidateToken() {
        String username = "user";
        String token = jwtService.generateToken(username);

        assertTrue(jwtService.isTokenValid(token));
        assertEquals(username, jwtService.extractUsername(token));
    }
}
