package com.example.bank_account_service.service;

import com.example.bank_account_service.model.entity.User;
import com.example.bank_account_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final UserRepository repository;
    /**
     * Gets the currently logged-in user from the JWT Security Context.
     */
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found in database"));
    }
}
