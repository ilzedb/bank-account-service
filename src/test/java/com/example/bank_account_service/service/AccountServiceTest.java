package com.example.bank_account_service.service;

import com.example.bank_account_service.api.model.Balance;
import com.example.bank_account_service.api.model.CurrencyCode;
import com.example.bank_account_service.api.model.ExchangeRequest;
import com.example.bank_account_service.api.model.TransactionRequest;
import com.example.bank_account_service.exception.AccountAccessDeniedException;
import com.example.bank_account_service.exception.AccountNotFoundException;
import com.example.bank_account_service.exception.InsufficientFundsException;
import com.example.bank_account_service.model.entity.Account;
import com.example.bank_account_service.model.entity.AccountBalance;
import com.example.bank_account_service.model.entity.User;
import com.example.bank_account_service.repository.AccountBalanceRepository;
import com.example.bank_account_service.repository.AccountRepository;
import com.example.bank_account_service.repository.TransactionHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final String ACCOUNT_ID = "test-id";
    private static final String USER_ID = "test-user";

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private ExternalService externalService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void testGetBalance(){
        User user = User.builder().username(USER_ID).build();
        Account account = Account.builder()
                .accountNumber(ACCOUNT_ID)
                .owner(user)
                .balances(List.of(
                        AccountBalance.builder().currency("USD").amount(new BigDecimal("100.00")).build()
                ))
                .build();
        when(securityService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByAccountNumber(ACCOUNT_ID)).thenReturn(Optional.ofNullable(account));

        List<Balance> list = accountService.getBalancesByAccountId(ACCOUNT_ID);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("USD", list.get(0).getCurrency().getValue());
        assertEquals(new BigDecimal("100.00"), list.get(0).getAmount());
    }

    @Test
    void testGetBalanceAccountNotFound(){
        User user = User.builder().username(USER_ID).build();
        Account account = Account.builder()
                .accountNumber(ACCOUNT_ID)
                .owner(user)
                .balances(List.of(
                        AccountBalance.builder().currency("USD").amount(new BigDecimal("100.00")).build()
                ))
                .build();
        when(securityService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByAccountNumber(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->  accountService.getBalancesByAccountId(ACCOUNT_ID));
    }

    @Test
    void testGetBalanceNotAllowed(){
        User user = User.builder().username(USER_ID).build();
        Account account = Account.builder()
                .accountNumber(ACCOUNT_ID)
                .owner(user)
                .balances(List.of(
                        AccountBalance.builder().currency("USD").amount(new BigDecimal("100.00")).build()
                ))
                .build();
        when(securityService.getCurrentUser()).thenReturn(User.builder().username("other").build());
        when(accountRepository.findByAccountNumber(ACCOUNT_ID)).thenReturn(Optional.ofNullable(account));

        assertThrows(AccountAccessDeniedException.class, () ->  accountService.getBalancesByAccountId(ACCOUNT_ID));
    }

    @Test
    void testProcessCredit() {
        User user = User.builder().username(USER_ID).build();
        AccountBalance balance = AccountBalance.builder()
                .amount(new BigDecimal("50.00"))
                .currency("USD")
                .build();
        Account account = Account.builder()
                .accountNumber(ACCOUNT_ID)
                .owner(user)
                .balances(List.of(balance))
                .build();

        TransactionRequest request = new TransactionRequest(CurrencyCode.USD, new BigDecimal("100.00"));
        when(securityService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByAccountNumber(ACCOUNT_ID)).thenReturn(Optional.ofNullable(account));
        when(accountBalanceRepository.findByAccountIdAndCurrencyWithLock(any(), any())).thenReturn(Optional.of(balance));

        accountService.processCredit(ACCOUNT_ID, request);

        verify(transactionHistoryRepository, times(1)).save(any());
    }
    @Test
    void testProcessDebitInsufficientFunds() {
        User user = User.builder().username(USER_ID).build();
        Account account = Account.builder().accountNumber(ACCOUNT_ID).owner(user).build();

        AccountBalance balance = AccountBalance.builder()
                .amount(new BigDecimal("50.00"))
                .currency("USD")
                .build();

        TransactionRequest request = new TransactionRequest(CurrencyCode.USD, new BigDecimal("100.00"));

        when(securityService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByAccountNumber(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(accountBalanceRepository.findByAccountIdAndCurrencyWithLock(any(), any())).thenReturn(Optional.of(balance));

        assertThrows(InsufficientFundsException.class, () -> accountService.processDebit(ACCOUNT_ID, request));
    }

    @Test
    void testProcessExchange() {
        User user = User.builder().username(USER_ID).build();
        Account account = Account.builder().id(1L).accountNumber(ACCOUNT_ID).owner(user).build();

        AccountBalance fromBalance = AccountBalance.builder().amount(new BigDecimal("100.00")).build();
        AccountBalance toBalance = AccountBalance.builder().amount(BigDecimal.ZERO).build();

        ExchangeRequest request = new ExchangeRequest(CurrencyCode.USD, CurrencyCode.EUR, new BigDecimal("10.00"));

        when(securityService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByAccountNumber(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(exchangeRateService.getRate("USD", "EUR")).thenReturn(new BigDecimal("0.85"));
        when(accountBalanceRepository.findByAccountIdAndCurrencyWithLock(any(), eq("USD"))).thenReturn(Optional.of(fromBalance));
        when(accountBalanceRepository.findByAccountIdAndCurrencyWithLock(any(), eq("EUR"))).thenReturn(Optional.of(toBalance));

        accountService.processExchange(ACCOUNT_ID, request);

        assertEquals(0, new BigDecimal("90.00").compareTo(fromBalance.getAmount()));
        assertEquals(0, new BigDecimal("8.50").compareTo(toBalance.getAmount()));
        verify(transactionHistoryRepository, times(2)).save(any());
    }
}
