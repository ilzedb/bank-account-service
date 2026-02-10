package com.example.bank_account_service.exception;

import lombok.Getter;

@Getter
public class UnsupportedCurrencyException extends RuntimeException{
    private final String fromCurrency;
    private final String toCurrency;

    public UnsupportedCurrencyException(String fromCurrency, String toCurrency){
        super(String.format("Unsupported currencies %s, %s.", fromCurrency, toCurrency));
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }
}
