package com.example.bank_account_service.controller;

import com.example.bank_account_service.model.auth.AuthResponse;
import com.example.bank_account_service.model.auth.User;
import com.example.bank_account_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    private static final String TOKEN = "token";
    private static final String USER = "user";
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testAuthenticate(){
        User user = Mockito.mock(User.class);
        AuthResponse authResponse = AuthResponse.builder().token(TOKEN).user(USER).build();
        when(authService.authenticate(user)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.authenticate(user);

        assertNotNull(response);
        assertEquals(USER, response.getBody().getUser());
        assertEquals(TOKEN, response.getBody().getToken());
    }
}
