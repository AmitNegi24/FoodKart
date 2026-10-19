package com.foodkart.auth_service.service;

import com.foodkart.auth_service.dto.AuthResponseDTO;
import com.foodkart.auth_service.dto.LoginRequestDTO;
import com.foodkart.auth_service.entity.User;
import com.foodkart.auth_service.exception.AuthFailedException;
import com.foodkart.auth_service.repository.UserRepository;
import com.foodkart.auth_service.security.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("test@example.com");
        loginRequestDTO.setPassword("password123");
    }

    @Test
    void register_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(User.builder().build());

        String result = authService.register("Test User", "test@example.com", "password123");

        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            authService.register("Test User", "test@example.com", "password123")
        );
        
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(anyString())).thenReturn("jwt-token");

        AuthResponseDTO result = authService.login(loginRequestDTO);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken("test@example.com");
    }

    @Test
    void login_invalidCredentials_throwsAuthFailedException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(AuthFailedException.class, () -> authService.login(loginRequestDTO));
        
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_authenticationException_throwsAuthFailedException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(AuthFailedException.class, () -> authService.login(loginRequestDTO));
        
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void register_withAllFields() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        User savedUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role("CUSTOMER")
                .active(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        String result = authService.register("Test User", "test@example.com", "password123");

        assertEquals("User registered successfully", result);
        verify(userRepository).save(argThat(user -> 
            user.getName().equals("Test User") &&
            user.getEmail().equals("test@example.com") &&
            user.getRole().equals("CUSTOMER") &&
            user.isActive() == true
        ));
    }
}
