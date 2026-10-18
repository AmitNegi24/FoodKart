package com.foodkart.menu_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTFilterTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JWTFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_noAuthHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).isTokenValid(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_invalidAuthHeaderFormat() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).isTokenValid(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_validToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
        when(jwtService.isTokenValid("valid.token.here")).thenReturn(true);
        when(jwtService.extractEmailId("valid.token.here")).thenReturn("test@example.com");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).isTokenValid("valid.token.here");
        verify(jwtService, times(1)).extractEmailId("valid.token.here");
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@example.com", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_invalidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");
        when(jwtService.isTokenValid("invalid.token")).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).isTokenValid("invalid.token");
        verify(jwtService, never()).extractEmailId(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
