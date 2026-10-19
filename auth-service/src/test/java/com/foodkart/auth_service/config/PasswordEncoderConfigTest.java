package com.foodkart.auth_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderConfigTest {

    @Test
    void passwordEncoder_returnsBCryptPasswordEncoder() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder);
        
        // Test encoding and matching
        String rawPassword = "testPassword123";
        String encodedPassword = encoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
        assertFalse(encoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void passwordEncoder_differentPasswords_differentHashes() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String password1 = encoder.encode("password1");
        String password2 = encoder.encode("password2");

        assertNotEquals(password1, password2);
    }

    @Test
    void passwordEncoder_samePassword_differentHashes() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String password1 = encoder.encode("samePassword");
        String password2 = encoder.encode("samePassword");

        // BCrypt generates different hashes for the same password due to salt
        assertNotEquals(password1, password2);
        // But both should match the original password
        assertTrue(encoder.matches("samePassword", password1));
        assertTrue(encoder.matches("samePassword", password2));
    }
}
