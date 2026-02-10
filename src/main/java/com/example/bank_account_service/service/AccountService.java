package com.example.bank_account_service.service;

import com.example.bank_account_service.annotation.Auditable;
import com.example.bank_account_service.api.model.Balance;
import com.example.bank_account_service.api.model.CurrencyCode;
import com.example.bank_account_service.api.model.ExchangeRequest;
import com.example.bank_account_service.api.model.TransactionRequest;
import com.example.bank_account_service.exception.AccountAccessDeniedException;
import com.example.bank_account_service.exception.AccountNotFoundException;
import com.example.bank_account_service.exception.InsufficientFundsException;
import com.example.bank_account_service.model.entity.Account;
import com.example.bank_account_service.model.entity.AccountBalance;
import com.example.bank_account_service.model.entity.TransactionHistory;
import com.example.bank_account_service.model.entity.TransactionType;
import com.example.bank_account_service.model.entity.User;
import com.example.bank_account_service.repository.AccountBalanceRepository;
import com.example.bank_account_service.repository.AccountRepository;
import com.example.bank_account_service.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountBalanceRepository balanceRepository;
    private final SecurityService securityService;
    private final ExchangeRateService exchangeRateService;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final ExternalService externalService;

    @Transactional(readOnly = true)
    public List<Balance> getBalancesByAccountId(String accountId) {
        User currentUser = securityService.getCurrentUser();

        Account account = accountRepository.findByAccountNumber(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!account.getOwner().equals(currentUser)) {
            throw new AccountAccessDeniedException("Access Denied");
        }

        return account.getBalances().stream()
                .map(balance -> Balance.builder()
                        .amount(balance.getAmount())
                        .currency(CurrencyCode.fromValue(balance.getCurrency()))
                        .updatedAt(balance.getUpdatedAt())
                        .build())
                .toList();
    }

    @Transactional
    @Auditable(action = "CREDIT")
    public String processCredit(String accountId, TransactionRequest request){
        String transactionId = UUID.randomUUID().toString();
        User user = securityService.getCurrentUser();
        processTransaction(accountId, request, TransactionType.CREDIT, user.getUsername(), transactionId);
        return transactionId;
    }

    @Transactional
    @Auditable(action = "DEBIT")
    public String processDebit(String accountId, TransactionRequest request){
        User user = securityService.getCurrentUser();
        String transactionId = UUID.randomUUID().toString();
        externalService.logExternalPreDebit(accountId);
        processTransaction(accountId, request, TransactionType.DEBIT, user.getUsername(), transactionId);
        return transactionId;
    }

    @Transactional
    @Auditable(action = "EXCHANGE")
    public String processExchange(String accountId, ExchangeRequest request) {
        User user = securityService.getCurrentUser();
        String transactionId = UUID.randomUUID().toString();
        // Debit the From currency
        processTransaction(accountId, new TransactionRequest(request.getFromCurrency(), request.getAmount()), TransactionType.DEBIT, user.getUsername(), transactionId);

        // Calculate rate and amount
        BigDecimal rate = exchangeRateService.getRate(request.getFromCurrency().getValue(), request.getToCurrency().getValue());
        BigDecimal convertedAmount = request.getAmount().multiply(rate);
        // Add to
        processTransaction(accountId, new TransactionRequest(request.getToCurrency(), convertedAmount), TransactionType.CREDIT, user.getUsername(), transactionId);
        return transactionId;
    }

    private void processTransaction(String accountId, TransactionRequest request, TransactionType type, String username, String transactionId) {
        Account account = accountRepository.findByAccountNumber(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!account.getOwner().getUsername().equals(username)) {
            throw new AccountAccessDeniedException("Access Denied");
        }

        AccountBalance balance = balanceRepository.findByAccountIdAndCurrencyWithLock(account.getId(), request.getCurrency().name())
                .orElseGet(() -> AccountBalance.builder()
                        .account(account)
                        .currency(request.getCurrency().getValue())
                        .amount(BigDecimal.ZERO)
                        .build());

        BigDecimal amount = request.getAmount();
        if (type == TransactionType.DEBIT && balance.getAmount().compareTo(amount) < 0) {
            throw new InsufficientFundsException(request.getCurrency().getValue(), amount);
        }

        BigDecimal newBalance = (type == TransactionType.CREDIT)
                ? balance.getAmount().add(amount)
                : balance.getAmount().subtract(amount);

        balance.setAmount(newBalance);
        balanceRepository.save(balance);
        logTransaction(account, amount, request.getCurrency().name(), type, transactionId);
    }

    private void logTransaction(Account account, BigDecimal amount, String currency, TransactionType type, String transactionId) {
        TransactionHistory transaction = TransactionHistory.builder()
                .transactionId(transactionId)
                .account(account)
                .amount(amount)
                .currency(currency)
                .type(type)
                .build();
        account.addTransaction(transaction);
        transactionHistoryRepository.save(transaction);
    }
}
