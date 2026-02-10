package com.example.bank_account_service.controller;

import com.example.bank_account_service.api.controller.TransactionsApi;
import com.example.bank_account_service.api.model.ExchangeRequest;
import com.example.bank_account_service.api.model.TransactionRequest;
import com.example.bank_account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionController implements TransactionsApi {
    private final AccountService accountService;

    @Override
    public ResponseEntity<Void> addMoney(String id, TransactionRequest transactionRequest){
        accountService.processCredit(id, transactionRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> debitMoney(String id, TransactionRequest transactionRequest){
        accountService.processDebit(id, transactionRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> exchangeMoney(String id, ExchangeRequest exchangeRequest){
        accountService.processExchange(id, exchangeRequest);
        return ResponseEntity.ok().build();
    }
}
