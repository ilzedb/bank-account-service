package com.example.bank_account_service.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "application.security.jwt")
@RequiredArgsConstructor
@Getter
@Validated
public class JwtProperties {
    @NotBlank
    private final String secretKey;

    private final long expiration;
}
