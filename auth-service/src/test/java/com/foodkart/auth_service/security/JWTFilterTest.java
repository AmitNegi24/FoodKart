package com.foodkart.auth_service.security;

import com.foodkart.auth_service.entity.User;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTFilterTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JWTFilter jwtFilter;

    private UserDetails userDetails;
    private String validToken;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        
        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .role("CUSTOMER")
                .active(true)
                .build();
        
        userDetails = new UserPrincipal(user);
        validToken = "valid.jwt.token";
    }

    @Test
    void doFilterInternal_noAuthHeader_continuesChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractEmailId(anyString());
    }

    @Test
    void doFilterInternal_invalidAuthHeaderFormat_continuesChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat token");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractEmailId(anyString());
    }

    @Test
    void doFilterInternal_validToken_noExistingAuthentication_setsAuthentication() 
            throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractEmailId(validToken)).thenReturn("test@example.com");
        when(customUserDetailService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(validToken, userDetails)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractEmailId(validToken);
        verify(customUserDetailService, times(1)).loadUserByUsername("test@example.com");
        verify(jwtService, times(1)).isTokenValid(validToken, userDetails);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
    }

    @Test
    void doFilterInternal_validToken_existingAuthentication_skipsSetting() 
            throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractEmailId(validToken)).thenReturn("test@example.com");
        
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(customUserDetailService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_validToken_invalidToken_continuesChain() 
            throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractEmailId(validToken)).thenReturn("test@example.com");
        when(customUserDetailService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(validToken, userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).isTokenValid(validToken, userDetails);
    }

    @Test
    void doFilterInternal_nullEmailId_continuesChain() 
            throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractEmailId(validToken)).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(customUserDetailService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_emptyAuthHeader_continuesChain() 
            throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractEmailId(anyString());
    }

    @Test
    void doFilterInternal_bearerWithoutToken_continuesChain() 
            throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}
