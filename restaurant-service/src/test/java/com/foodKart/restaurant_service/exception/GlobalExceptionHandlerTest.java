package com.foodKart.restaurant_service.exception;

import com.foodKart.restaurant_service.exception.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FieldError fieldError;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleRestaurantNotFound_success() {
        RestaurantNotFoundException exception = new RestaurantNotFoundException("Restaurant not found with id: 1");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleRestaurantNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("Restaurant not found with id: 1", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertEquals("Not Found", response.getBody().getError());
    }

    @Test
    void handleValidationError_success() {
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(fieldError.getDefaultMessage()).thenReturn("Name is required");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationError(methodArgumentNotValidException, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Name is required", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertEquals("Bad Request", response.getBody().getError());
    }
}
