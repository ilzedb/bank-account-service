package com.example.bank_account_service.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InsufficientFundsException extends RuntimeException{
    private final String currency;
    private final BigDecimal amount;

    public InsufficientFundsException(String currency, BigDecimal amount) {
        super(String.format("Balance in %s is too low for this debit of %s.", currency, amount));
        this.currency = currency;
        this.amount = amount;
    }
}
