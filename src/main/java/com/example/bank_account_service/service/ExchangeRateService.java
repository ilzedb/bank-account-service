package com.example.bank_account_service.service;

import com.example.bank_account_service.exception.UnsupportedCurrencyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ExchangeRateService {

    private static final Map<String, BigDecimal> TO_EUR_RATES = Map.of(
            "EUR", BigDecimal.ONE,
            "USD", new BigDecimal("0.85"),
            "GBP", new BigDecimal("1.15"),
            "SEK", new BigDecimal("0.094")
    );

    public BigDecimal getRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        BigDecimal fromToEur = TO_EUR_RATES.get(fromCurrency);
        BigDecimal toToEur = TO_EUR_RATES.get(toCurrency);

        if (fromToEur == null || toToEur == null) {
            throw new UnsupportedCurrencyException(fromCurrency, toCurrency);
        }

        // Rate = (From -> Base) / (To -> Base)
        return fromToEur.divide(toToEur, 6, java.math.RoundingMode.HALF_UP);
    }

}
