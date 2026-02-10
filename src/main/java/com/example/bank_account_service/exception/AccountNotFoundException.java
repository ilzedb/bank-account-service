package com.example.bank_account_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an account lookup fails by Account Number.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String id) {
        super("Account with ID " + id + " was not found.");
    }
}
