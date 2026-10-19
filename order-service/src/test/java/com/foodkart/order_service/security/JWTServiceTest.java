package com.foodkart.order_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class JWTServiceTest {

    @InjectMocks
    private JWTService jwtService;

    private String testSecretKey;
    private String validToken;

    @BeforeEach
    void setUp() {
        testSecretKey = Base64.getEncoder().encodeToString(
                "ThisIsASecretKeyForTestingJWTTokenValidationThatIsLongEnough".getBytes()
        );
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);

        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(testSecretKey));
        validToken = Jwts.builder()
                .subject("test@example.com")
                .signWith(key)
                .compact();
    }

    @Test
    void extractEmailId_success() {
        String email = jwtService.extractEmailId(validToken);
        assertEquals("test@example.com", email);
    }

    @Test
    void extractClaim_success() {
        String subject = jwtService.extractClaim(validToken, claims -> claims.getSubject());
        assertEquals("test@example.com", subject);
    }

    @Test
    void isTokenValid_validToken_returnsTrue() {
        boolean isValid = jwtService.isTokenValid(validToken);
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_invalidToken_returnsFalse() {
        boolean isValid = jwtService.isTokenValid("invalid.token.here");
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_emptyToken_returnsFalse() {
        boolean isValid = jwtService.isTokenValid("");
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_nullToken_returnsFalse() {
        boolean isValid = jwtService.isTokenValid(null);
        assertFalse(isValid);
    }

    @Test
    void extractClaim_withDifferentClaims() {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(testSecretKey));
        String token = Jwts.builder()
                .subject("test@example.com")
                .claim("customClaim", "customValue")
                .signWith(key)
                .compact();

        String customValue = jwtService.extractClaim(token, claims -> claims.get("customClaim", String.class));
        assertEquals("customValue", customValue);
    }
}
