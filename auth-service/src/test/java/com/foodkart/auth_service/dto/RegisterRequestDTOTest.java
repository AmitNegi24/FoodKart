package com.foodkart.auth_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestDTOTest {

    @Test
    void builder_createsDTO() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        assertEquals("Test User", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void noArgsConstructor_createsEmptyDTO() {
        RegisterRequestDTO dto = new RegisterRequestDTO();

        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getPassword());
    }

    @Test
    void allArgsConstructor_createsDTO() {
        RegisterRequestDTO dto = new RegisterRequestDTO("Test User", "test@example.com", "password123");

        assertEquals("Test User", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void setName_setsName() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setName("New Name");
        assertEquals("New Name", dto.getName());
    }

    @Test
    void setEmail_setsEmail() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("new@example.com");
        assertEquals("new@example.com", dto.getEmail());
    }

    @Test
    void setPassword_setsPassword() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setPassword("newpassword");
        assertEquals("newpassword", dto.getPassword());
    }

    @Test
    void getName_returnsName() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .name("User Name")
                .build();
        assertEquals("User Name", dto.getName());
    }

    @Test
    void getEmail_returnsEmail() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .email("email@example.com")
                .build();
        assertEquals("email@example.com", dto.getEmail());
    }

    @Test
    void getPassword_returnsPassword() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .password("mypass")
                .build();
        assertEquals("mypass", dto.getPassword());
    }
}
