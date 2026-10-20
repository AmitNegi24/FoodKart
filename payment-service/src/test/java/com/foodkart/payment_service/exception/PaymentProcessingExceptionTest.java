package com.foodkart.payment_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProcessingExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Payment processing failed";
        PaymentProcessingException exception = new PaymentProcessingException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Payment processing failed";
        Throwable cause = new RuntimeException("Database error");
        PaymentProcessingException exception = new PaymentProcessingException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testInheritance() {
        PaymentProcessingException exception = new PaymentProcessingException("Test");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowingExceptionWithMessage() {
        String message = "Payment timeout";
        
        assertThrows(PaymentProcessingException.class, () -> {
            throw new PaymentProcessingException(message);
        });
    }

    @Test
    void testThrowingExceptionWithCause() {
        Throwable cause = new RuntimeException("Network error");
        
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () -> {
            throw new PaymentProcessingException("Payment failed", cause);
        });

        assertEquals(cause, exception.getCause());
    }

    @Test
    void testMessageRetrieval() {
        String message = "Invalid payment amount";
        PaymentProcessingException exception = new PaymentProcessingException(message);

        try {
            throw exception;
        } catch (PaymentProcessingException e) {
            assertEquals(message, e.getMessage());
        }
    }
}
