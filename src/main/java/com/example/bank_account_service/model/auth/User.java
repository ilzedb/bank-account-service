package com.example.bank_account_service.model.auth;

import lombok.Data;
import lombok.ToString;

@Data
public class User {
    private String userName;
    @ToString.Exclude
    private String password;
}
