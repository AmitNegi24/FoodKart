package com.foodkart.auth_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseDTOTest {

    @Test
    void constructor_withToken_setsToken() {
        AuthResponseDTO dto = new AuthResponseDTO("jwt-token");
        assertEquals("jwt-token", dto.getToken());
    }

    @Test
    void constructor_withNullToken_setsNull() {
        AuthResponseDTO dto = new AuthResponseDTO(null);
        assertNull(dto.getToken());
    }

    @Test
    void constructor_withEmptyToken_setsEmpty() {
        AuthResponseDTO dto = new AuthResponseDTO("");
        assertEquals("", dto.getToken());
    }

    @Test
    void getToken_returnsToken() {
        AuthResponseDTO dto = new AuthResponseDTO("test-token");
        assertEquals("test-token", dto.getToken());
    }

    @Test
    void setToken_setsToken() {
        AuthResponseDTO dto = new AuthResponseDTO("old-token");
        dto.setToken("new-token");
        assertEquals("new-token", dto.getToken());
    }
}
