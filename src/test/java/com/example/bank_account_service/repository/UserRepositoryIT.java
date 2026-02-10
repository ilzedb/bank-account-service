package com.example.bank_account_service.repository;

import com.example.bank_account_service.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryIT {
    private static final String USER = "user-name";

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByUsername() {
        User user = new User();
        user.setUsername(USER);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername(USER);

        assertTrue(found.isPresent());
        assertEquals(USER, found.get().getUsername());
    }
}
