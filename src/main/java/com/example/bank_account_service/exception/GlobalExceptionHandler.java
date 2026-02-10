package com.example.bank_account_service.exception;

import com.example.bank_account_service.api.model.ErrorResponse;
import io.jsonwebtoken.JwtException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import static com.example.bank_account_service.exception.ExceptionCodes.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String TRACE_ID_KEY = "traceId";

    @ExceptionHandler(AccountAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccountAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(FORBIDDEN, ex.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(buildErrorResponse(INSUFFICIENT_FUNDS, ex.getMessage()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleJsonErrors(ExternalServiceUnavailableException ex) {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(buildErrorResponse(EXTERNAL_FAIL, ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonErrors(HttpMessageNotReadableException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(MALFORMED_JSON,
                "Invalid input format. Check your enum values (EUR, USD, etc.) or JSON syntax."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(VALIDATION_FAILED, details));
    }

    // Internal issues
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    private ErrorResponse buildErrorResponse(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .traceId(MDC.get(TRACE_ID_KEY))
                .build();
    }
}
