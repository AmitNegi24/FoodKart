package com.foodkart.menu_service.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        lenient().when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleRestaurantNotFound_success() {
        RestaurantNotFoundException exception = new RestaurantNotFoundException();

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleRestaurantNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleInvalidEnum_withInvalidFormatException() {
        InvalidFormatException invalidFormatException = mock(InvalidFormatException.class);
        when(invalidFormatException.getValue()).thenReturn("INVALID_VALUE");
        when(invalidFormatException.getTargetType()).thenReturn((Class) com.foodkart.menu_service.model.MenuCategory.class);

        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid enum", invalidFormatException, null);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleInvalidEnum(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").contains("Invalid value"));
    }

    @Test
    void handleInvalidEnum_withoutInvalidFormatException() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid enum", null, null);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleInvalidEnum(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid Enum Value", response.getBody().get("message"));
    }

    @Test
    void handleMenuItemNotFoundException_success() {
        MenuItemNotFoundException exception = new MenuItemNotFoundException("Menu item not found with id: 1");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMenuItemNotFoundException(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("Menu item not found with id: 1", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }
}
