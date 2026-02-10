package com.example.bank_account_service.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExchangeRateServiceTest {
    private final ExchangeRateService service = new ExchangeRateService();

    @Test
    void testGetRateUSDtoEUR() {
        BigDecimal rate = service.getRate("USD", "EUR");
        assertEquals(new BigDecimal("0.850000"), rate);
    }

    @Test
    void testGetRateGBPtoUSD() {
        BigDecimal rate = service.getRate("GBP", "USD");
        assertEquals(new BigDecimal("1.352941"), rate);
    }
}
