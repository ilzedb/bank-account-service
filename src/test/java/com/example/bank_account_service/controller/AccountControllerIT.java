package com.example.bank_account_service.controller;

import com.example.bank_account_service.api.model.Balance;
import com.example.bank_account_service.exception.AccountAccessDeniedException;
import com.example.bank_account_service.exception.AccountNotFoundException;
import com.example.bank_account_service.service.AccountService;
import com.example.bank_account_service.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void testGetBalanceOK() throws Exception {
        String id = "ID1";
        when(accountService.getBalancesByAccountId(id)).thenReturn(List.of(Balance.builder().build()));

        mockMvc.perform(get("/accounts/{id}/balances", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetBalanceNotFound() throws Exception {
        String id = "ID1";

        when(accountService.getBalancesByAccountId(id))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(get("/accounts/{id}/balances", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGetBalanceForbidden() throws Exception {
        String id = "ID1";

        when(accountService.getBalancesByAccountId(id))
                .thenThrow(new AccountAccessDeniedException("Access Denied"));

        mockMvc.perform(get("/accounts/{id}/balances", id))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }
}
