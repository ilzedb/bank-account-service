package com.example.bank_account_service.model.auth;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
public class AuthResponse {
    @ToString.Exclude
    private String token;
    private String user;
}
