package com.foodkart.order_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleOrderNotFoundException_success() {
        OrderNotFoundException exception = new OrderNotFoundException("Order not found with id: 123");
        when(request.getRequestURI()).thenReturn("/orders/getOrder/123");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleRestaurantNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("Order not found with id: 123", response.getBody().getMessage());
        assertEquals("/orders/getOrder/123", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleValidationError_success() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("orderRequestDTO", "restaurantId", "must not be null");
        when(exception.getBindingResult()).thenReturn(mock(org.springframework.validation.BindingResult.class));
        when(exception.getBindingResult().getFieldErrors()).thenReturn(List.of(fieldError));
        when(request.getRequestURI()).thenReturn("/orders/createOrder");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationError(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("must not be null", response.getBody().getMessage());
        assertEquals("/orders/createOrder", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleOrderNotFoundException_withDifferentMessage() {
        OrderNotFoundException exception = new OrderNotFoundException("Order not found");
        when(request.getRequestURI()).thenReturn("/orders/getOrder/999");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleRestaurantNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order not found", response.getBody().getMessage());
    }
}
