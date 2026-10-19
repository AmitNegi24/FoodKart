package com.foodkart.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodkart.auth_service.dto.AuthResponseDTO;
import com.foodkart.auth_service.dto.LoginRequestDTO;
import com.foodkart.auth_service.dto.RegisterRequestDTO;
import com.foodkart.auth_service.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RegisterRequestDTO registerRequestDTO;
    private LoginRequestDTO loginRequestDTO;
    private AuthResponseDTO authResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        registerRequestDTO = RegisterRequestDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("test@example.com");
        loginRequestDTO.setPassword("password123");

        authResponseDTO = new AuthResponseDTO("jwt-token");
    }

    @Test
    void register_success() {
        when(authService.register(any(String.class), any(String.class), any(String.class)))
                .thenReturn("User registered successfully");

        ResponseEntity<String> result = authController.register(registerRequestDTO);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("User registered successfully", result.getBody());
        verify(authService, times(1)).register("Test User", "test@example.com", "password123");
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        when(authService.register(any(String.class), any(String.class), any(String.class)))
                .thenThrow(new RuntimeException("Email already registered"));

        assertThrows(RuntimeException.class, () -> authController.register(registerRequestDTO));
        verify(authService, times(1)).register("Test User", "test@example.com", "password123");
    }

    @Test
    void login_success() {
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(authResponseDTO);

        ResponseEntity<AuthResponseDTO> result = authController.login(loginRequestDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("jwt-token", result.getBody().getToken());
        verify(authService, times(1)).login(loginRequestDTO);
    }

    @Test
    void login_invalidCredentials_throwsException() {
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> authController.login(loginRequestDTO));
        verify(authService, times(1)).login(loginRequestDTO);
    }

    @Test
    void test_success() {
        String result = authController.test();
        assertEquals("Authenticated user", result);
    }

    @Test
    void register_withMockMvc_success() throws Exception {
        when(authService.register(any(String.class), any(String.class), any(String.class)))
                .thenReturn("User registered successfully");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void login_withMockMvc_success() throws Exception {
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(authResponseDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk());
    }
}
