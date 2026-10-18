package com.foodKart.restaurant_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    @InjectMocks
    private JWTService jwtService;

    private String secretKey;
    private String validToken;

    @BeforeEach
    void setUp() {
        secretKey = Base64.getEncoder().encodeToString(
                "ThisIsASecretKeyForTestingJWTTokenGeneration1234567890".getBytes()
        );
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);

        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        validToken = Jwts.builder()
                .subject("test@example.com")
                .signWith(key)
                .compact();
    }

    @Test
    void extractEmailId_success() {
        String email = jwtService.extractEmailId(validToken);

        assertNotNull(email);
        assertEquals("test@example.com", email);
    }

    @Test
    void extractClaim_success() {
        String email = jwtService.extractClaim(validToken, claims -> claims.getSubject());

        assertNotNull(email);
        assertEquals("test@example.com", email);
    }

    @Test
    void isTokenValid_validToken() {
        boolean isValid = jwtService.isTokenValid(validToken);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_invalidToken() {
        boolean isValid = jwtService.isTokenValid("invalid.token.here");

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_emptyToken() {
        boolean isValid = jwtService.isTokenValid("");

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_nullToken() {
        boolean isValid = jwtService.isTokenValid(null);

        assertFalse(isValid);
    }
}
