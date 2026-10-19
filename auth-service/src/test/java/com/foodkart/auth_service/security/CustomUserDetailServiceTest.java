package com.foodkart.auth_service.security;

import com.foodkart.auth_service.entity.User;
import com.foodkart.auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role("CUSTOMER")
                .active(true)
                .build();
    }

    @Test
    void loadUserByUsername_success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER")));
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
            customUserDetailService.loadUserByUsername("nonexistent@example.com")
        );

        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void loadUserByUsername_withAdminRole() {
        user.setRole("ADMIN");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailService.loadUserByUsername("admin@example.com");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        verify(userRepository, times(1)).findByEmail("admin@example.com");
    }

    @Test
    void loadUserByUsername_withRestaurantOwnerRole() {
        user.setRole("RESTAURANT_OWNER");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailService.loadUserByUsername("owner@example.com");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_RESTAURANT_OWNER")));
    }

    @Test
    void loadUserByUsername_inactiveUser() {
        user.setActive(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailService.loadUserByUsername("inactive@example.com");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
        verify(userRepository, times(1)).findByEmail("inactive@example.com");
    }
}
