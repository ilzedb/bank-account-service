package com.example.bank_account_service.controller;

import com.example.bank_account_service.model.auth.AuthResponse;
import com.example.bank_account_service.model.auth.User;
import com.example.bank_account_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody User user) {
        return ResponseEntity.ok(authService.authenticate(user));
    }

}
