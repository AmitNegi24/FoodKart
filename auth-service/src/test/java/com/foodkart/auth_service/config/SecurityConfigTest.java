package com.foodkart.auth_service.config;

import com.foodkart.auth_service.security.JWTFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JWTFilter jwtFilter;

    @Mock
    private PasswordEncoderConfig passwordEncoder;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
    }

    @Test
    void compromisedPasswordChecker_returnsHaveIBeenPwnedChecker() {
        CompromisedPasswordChecker checker = securityConfig.compromisedPasswordChecker();

        assertNotNull(checker);
        assertTrue(checker instanceof HaveIBeenPwnedRestApiPasswordChecker);
    }

    @Test
    void authenticationProvider_returnsDaoAuthenticationProvider() {
        when(passwordEncoder.passwordEncoder()).thenReturn(passwordEncoderMock);

        AuthenticationProvider provider = securityConfig.authenticationProvider();

        assertNotNull(provider);
        assertTrue(provider instanceof org.springframework.security.authentication.dao.DaoAuthenticationProvider);
    }

    @Test
    void authenticationManager_returnsAuthenticationManager() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(result);
        assertEquals(authenticationManager, result);
        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }

    @Test
    void defaultSecurityFilterChain_returnsSecurityFilterChain() throws Exception {
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mock(org.springframework.security.web.DefaultSecurityFilterChain.class));

        SecurityFilterChain result = securityConfig.defaultSecurityFilterChain(httpSecurity);

        assertNotNull(result);
        verify(httpSecurity, times(1)).build();
    }
}
