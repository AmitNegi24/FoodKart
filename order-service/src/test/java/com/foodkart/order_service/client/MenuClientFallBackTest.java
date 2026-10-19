package com.foodkart.order_service.client;

import com.foodkart.order_service.dto.MenuItemDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuClientFallBackTest {

    @Test
    void getMenuItemById_throwsRuntimeException() {
        MenuClientFallBack fallback = new MenuClientFallBack();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fallback.getMenuItemById(1L);
        });

        assertEquals("Menu Service is currently unavailable. Please try again later.", exception.getMessage());
    }

    @Test
    void getMenuItemById_withDifferentId() {
        MenuClientFallBack fallback = new MenuClientFallBack();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fallback.getMenuItemById(999L);
        });

        assertEquals("Menu Service is currently unavailable. Please try again later.", exception.getMessage());
    }
}
