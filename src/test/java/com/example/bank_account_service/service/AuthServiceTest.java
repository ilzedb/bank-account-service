package com.example.bank_account_service.service;

import com.example.bank_account_service.model.auth.AuthResponse;
import com.example.bank_account_service.model.auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserName("test_user");
        testUser.setPassword("secure_password");
    }

    @Test
    void testAuthenticateSuccess() {
        String token = "Token";
        when(jwtService.generateToken(testUser.getUserName())).thenReturn(token);

        AuthResponse response = authService.authenticate(testUser);

        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals(testUser.getUserName(), response.getUser());

        verify(authenticationManager).authenticate(
                argThat(authentication ->
                        authentication.getPrincipal().equals(testUser.getUserName()) &&
                                authentication.getCredentials().equals(testUser.getPassword())
                )
        );
    }

    @Test
    void testAuthenticateInvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(testUser));
        verify(jwtService, never()).generateToken(anyString());
    }
}
