package com.example.bank_account_service.repository;

import com.example.bank_account_service.model.entity.Account;
import com.example.bank_account_service.model.entity.AccountBalance;
import com.example.bank_account_service.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AccountBalanceRepositoryIT {
    @Autowired
    private AccountBalanceRepository accountBalanceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    void testFindBalance() {
        User user = new User();
        user.setUsername("testuser");
        userRepository.save(user);

        Account account = new Account();
        account.setOwner(user);
        account.setAccountNumber("12345");
        accountRepository.save(account);

        AccountBalance balance = new AccountBalance();
        balance.setAccount(account);
        balance.setCurrency("USD");
        balance.setAmount(new BigDecimal("100.00"));
        accountBalanceRepository.save(balance);

        Optional<AccountBalance> found = accountBalanceRepository
                .findByAccountIdAndCurrencyWithLock(account.getId(), "USD");

        assertTrue(found.isPresent());
        assertEquals("USD", found.get().getCurrency());
        assertEquals(new BigDecimal("100.00"), found.get().getAmount());
    }
}
