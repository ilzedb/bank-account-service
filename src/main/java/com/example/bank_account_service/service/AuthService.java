package com.example.bank_account_service.service;

import com.example.bank_account_service.model.auth.AuthResponse;
import com.example.bank_account_service.model.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse authenticate(User user){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
        );
        String token = jwtService.generateToken(user.getUserName());
        return AuthResponse.builder().token(token).user(user.getUserName()).build();
    }
}
