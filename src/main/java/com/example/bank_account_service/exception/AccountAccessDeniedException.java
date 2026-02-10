package com.example.bank_account_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user attempts to access an account they do not own.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountAccessDeniedException extends RuntimeException{
    public AccountAccessDeniedException(String message) {
        super(message);
    }
}
