package com.example.bank_account_service.controller;

import com.example.bank_account_service.api.controller.AccountApi;
import com.example.bank_account_service.api.model.Balance;
import com.example.bank_account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {
    private final AccountService accountService;
    @Override
    public ResponseEntity<List<Balance>> getBalances(String id) {
        return ResponseEntity.ok(accountService.getBalancesByAccountId(id));
    }
}
