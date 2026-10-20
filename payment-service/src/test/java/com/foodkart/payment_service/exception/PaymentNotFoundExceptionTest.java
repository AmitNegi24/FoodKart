package com.foodkart.payment_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Payment not found for order: 123";
        PaymentNotFoundException exception = new PaymentNotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testInheritance() {
        PaymentNotFoundException exception = new PaymentNotFoundException("Test message");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowingException() {
        String message = "Payment not found";
        
        assertThrows(PaymentNotFoundException.class, () -> {
            throw new PaymentNotFoundException(message);
        });
    }

    @Test
    void testMessageRetrieval() {
        String message = "Payment with ID 999 not found";
        PaymentNotFoundException exception = new PaymentNotFoundException(message);

        try {
            throw exception;
        } catch (PaymentNotFoundException e) {
            assertEquals(message, e.getMessage());
        }
    }
}
