package com.foodkart.order_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Order not found with id: 123";
        OrderNotFoundException exception = new OrderNotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        OrderNotFoundException exception = new OrderNotFoundException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(OrderNotFoundException.class, () -> {
            throw new OrderNotFoundException("Order not found");
        });
    }

    @Test
    void testExceptionMessageCanBeRetrieved() {
        String message = "Order not found with id: 456";
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            throw new OrderNotFoundException(message);
        });

        assertEquals(message, exception.getMessage());
    }
}
