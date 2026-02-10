package com.example.bank_account_service.repository;

import com.example.bank_account_service.model.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepository extends JpaRepository <TransactionHistory, Long> {

}
