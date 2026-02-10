package com.example.bank_account_service.exception;

public final class ExceptionCodes {
    private ExceptionCodes(){
        // Private constructor to prevent instantiation
    }

    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String INSUFFICIENT_FUNDS = "INSUFFICIENT_FUNDS";
    public static final String EXTERNAL_FAIL = "EXTERNAL_FAIL";
    public static final String MALFORMED_JSON = "MALFORMED_JSON";
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
}
