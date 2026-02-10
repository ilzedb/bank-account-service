package com.example.bank_account_service.controller;

import com.example.bank_account_service.api.model.CurrencyCode;
import com.example.bank_account_service.api.model.ExchangeRequest;
import com.example.bank_account_service.api.model.TransactionRequest;
import com.example.bank_account_service.exception.AccountAccessDeniedException;
import com.example.bank_account_service.exception.AccountNotFoundException;
import com.example.bank_account_service.exception.ExternalServiceUnavailableException;
import com.example.bank_account_service.exception.InsufficientFundsException;
import com.example.bank_account_service.service.AccountService;
import com.example.bank_account_service.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void testAddMoneyOK() throws Exception {
        String id = "ID1";
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(BigDecimal.ONE)
                .currency(CurrencyCode.EUR).build();
        String jsonString = "{\n" +
                "  \"currency\": \"EUR\",\n" +
                "  \"amount\": 1\n" +
                "}";
        when(accountService.processCredit(id, transactionRequest)).thenReturn("");

        mockMvc.perform(post("/accounts/{id}/add", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk());
    }

    @Test
    void testAddMoneyNotFound() throws Exception {
        String id = "ID1";

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(BigDecimal.ONE)
                .currency(CurrencyCode.EUR).build();

        String jsonString = "{\n" +
                "  \"currency\": \"EUR\",\n" +
                "  \"amount\": 1\n" +
                "}";

        when(accountService.processCredit(id, transactionRequest))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(post("/accounts/{id}/add", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddMoneyForbidden() throws Exception {
        String id = "ID1";
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(BigDecimal.ONE)
                .currency(CurrencyCode.EUR).build();

        String jsonString = "{\n" +
                "  \"currency\": \"EUR\",\n" +
                "  \"amount\": 1\n" +
                "}";

        when(accountService.processCredit(id, transactionRequest))
                .thenThrow(new AccountAccessDeniedException("Access Denied"));

        mockMvc.perform(post("/accounts/{id}/add", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void testDebitMoneyOK() throws Exception {
        String id = "ID1";
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(BigDecimal.ONE)
                .currency(CurrencyCode.EUR).build();
        String jsonString = "{\n" +
                "  \"currency\": \"EUR\",\n" +
                "  \"amount\": 1\n" +
                "}";
        when(accountService.processDebit(id, transactionRequest)).thenReturn("");

        mockMvc.perform(post("/accounts/{id}/debit", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk());
    }

    @Test
    void testDebitMoneyExternalUnavailable() throws Exception {
        String id = "ID1";

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(BigDecimal.ONE)
                .currency(CurrencyCode.EUR).build();

        String jsonString = "{\n" +
                "  \"currency\": \"EUR\",\n" +
                "  \"amount\": 1\n" +
                "}";

        when(accountService.processDebit(id, transactionRequest))
                .thenThrow(new ExternalServiceUnavailableException("Failed to connect"));

        mockMvc.perform(post("/accounts/{id}/debit", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("EXTERNAL_FAIL"));
    }

    @Test
    void testDebitInsuffiecientFunds() throws Exception {
        String id = "ID1";
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(BigDecimal.ONE)
                .currency(CurrencyCode.EUR).build();

        String jsonString = "{\n" +
                "  \"currency\": \"EUR\",\n" +
                "  \"amount\": 1\n" +
                "}";

        when(accountService.processDebit(id, transactionRequest))
                .thenThrow(new InsufficientFundsException("EUR", BigDecimal.ONE));

        mockMvc.perform(post("/accounts/{id}/debit", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"));
    }

    @Test
    void testExchangeMoneyOK() throws Exception {
        String id = "ID1";
        ExchangeRequest exchangeRequest = ExchangeRequest.builder()
                .amount(BigDecimal.ONE)
                .fromCurrency(CurrencyCode.EUR)
                .toCurrency(CurrencyCode.GBP).build();
        String jsonString = "{\n" +
                "  \"fromCurrency\": \"EUR\",\n" +
                "  \"toCurrency\": \"GBP\",\n" +
                "  \"amount\": 1\n" +
                "}";
        when(accountService.processExchange(id, exchangeRequest)).thenReturn("");

        mockMvc.perform(post("/accounts/{id}/exchange", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk());
    }

    @Test
    void testExchangeMoneyBadRequest() throws Exception {
        String id = "ID1";

        String jsonString = "{\n" +
                "  \"fromCurrency\": \"PPL\",\n" +
                "  \"toCurrency\": \"GBP\",\n" +
                "  \"amount\": 1\n" +
                "}";

        mockMvc.perform(post("/accounts/{id}/exchange", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_JSON"));
    }

    @Test
    void testExchangeMoneyBadRequestInvalid() throws Exception {
        String id = "ID1";

        String jsonString = "{\n" +
                "  \"fromCurrency\": \"EUR\",\n" +
                "  \"toCurrency\": \"GBP\",\n" +
                "  \"amount\": 0.0001\n" +
                "}";

        mockMvc.perform(post("/accounts/{id}/exchange", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }
}
