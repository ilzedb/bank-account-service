package com.example.bank_account_service.repository;

import com.example.bank_account_service.model.entity.AccountBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {
    List<AccountBalance> findByAccount_AccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM AccountBalance b WHERE b.account.id = :accountId AND b.currency = :currency")
    Optional<AccountBalance> findByAccountIdAndCurrencyWithLock(Long accountId, String currency);
}
