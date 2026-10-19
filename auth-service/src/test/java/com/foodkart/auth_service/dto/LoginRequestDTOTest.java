package com.foodkart.auth_service.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOTest {

    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO();
    }

    @Test
    void setEmail_setsEmail() {
        loginRequestDTO.setEmail("test@example.com");
        assertEquals("test@example.com", loginRequestDTO.getEmail());
    }

    @Test
    void setPassword_setsPassword() {
        loginRequestDTO.setPassword("password123");
        assertEquals("password123", loginRequestDTO.getPassword());
    }

    @Test
    void getEmail_returnsEmail() {
        loginRequestDTO.setEmail("user@example.com");
        assertEquals("user@example.com", loginRequestDTO.getEmail());
    }

    @Test
    void getPassword_returnsPassword() {
        loginRequestDTO.setPassword("secret");
        assertEquals("secret", loginRequestDTO.getPassword());
    }

    @Test
    void setEmail_null() {
        loginRequestDTO.setEmail(null);
        assertNull(loginRequestDTO.getEmail());
    }

    @Test
    void setPassword_null() {
        loginRequestDTO.setPassword(null);
        assertNull(loginRequestDTO.getPassword());
    }

    @Test
    void setEmail_emptyString() {
        loginRequestDTO.setEmail("");
        assertEquals("", loginRequestDTO.getEmail());
    }

    @Test
    void setPassword_emptyString() {
        loginRequestDTO.setPassword("");
        assertEquals("", loginRequestDTO.getPassword());
    }
}
