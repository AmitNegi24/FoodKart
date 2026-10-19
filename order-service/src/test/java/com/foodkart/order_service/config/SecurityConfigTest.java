package com.foodkart.order_service.config;

import com.foodkart.order_service.security.JWTFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JWTFilter jwtFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void testSecurityConfigCreation() {
        assertNotNull(securityConfig);
        assertNotNull(jwtFilter);
    }
}
