package com.example.bank_account_service.exception;

public class ExternalServiceUnavailableException extends RuntimeException{
    public ExternalServiceUnavailableException(String message){
        super(message);
    }
}
