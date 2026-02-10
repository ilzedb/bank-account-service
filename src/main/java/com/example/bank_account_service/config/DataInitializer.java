package com.example.bank_account_service.config;

import com.example.bank_account_service.model.entity.Account;
import com.example.bank_account_service.model.entity.AccountBalance;
import com.example.bank_account_service.model.entity.User;
import com.example.bank_account_service.repository.AccountBalanceRepository;
import com.example.bank_account_service.repository.AccountRepository;
import com.example.bank_account_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    @Value("${application.defaultPassword}")
    private String defaultPassword;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadData(UserRepository userRepository, AccountRepository accountRepository,
                               AccountBalanceRepository accountBalanceRepository) {
        return args -> {
            // Avoid duplicates
            if (userRepository.count() > 0) {
                return;
            }

            // Test User
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode(defaultPassword))
                    .build();

            User user1 = User.builder()
                    .username("user1")
                    .password(passwordEncoder.encode(defaultPassword))
                    .build();

            User user2 = User.builder()
                    .username("user2")
                    .password(passwordEncoder.encode(defaultPassword))
                    .build();

            userRepository.saveAll(List.of(admin, user1, user2));

            Account account1 = Account.builder()
                    .accountNumber("ID1")
                    .owner(admin)
                    .build();

            Account account2 = Account.builder()
                    .accountNumber("ID3")
                    .owner(user1)
                    .build();

            accountRepository.saveAll(List.of(
                    account1,
                    Account.builder()
                            .accountNumber("ID2")
                            .owner(admin)
                            .build(),
                    account2,
                    Account.builder()
                            .accountNumber("ID4")
                            .owner(user2)
                            .build()
                    )
            );

            accountBalanceRepository.saveAll(List.of(
                    AccountBalance.builder()
                            .account(account1)
                            .currency("USD")
                            .amount(new BigDecimal("1500.00"))
                            .build(),
                    AccountBalance.builder()
                            .account(account1)
                            .currency("EUR")
                            .amount(new BigDecimal("500.00"))
                            .build(),
                    AccountBalance.builder()
                            .account(account2)
                            .currency("SEK")
                            .amount(new BigDecimal("100.00"))
                            .build()
            ));
            log.info("--- Initial Data Loaded Successfully ---");
        };
    }
}