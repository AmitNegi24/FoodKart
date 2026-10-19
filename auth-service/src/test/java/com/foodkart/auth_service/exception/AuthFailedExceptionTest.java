package com.foodkart.auth_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthFailedExceptionTest {

    @Test
    void constructor_withMessage_setsMessage() {
        String message = "AuthService.INVALID_CREDENTIALS";
        AuthFailedException exception = new AuthFailedException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_withNullMessage_setsNullMessage() {
        AuthFailedException exception = new AuthFailedException(null);

        assertNull(exception.getMessage());
    }

    @Test
    void constructor_withEmptyMessage_setsEmptyMessage() {
        AuthFailedException exception = new AuthFailedException("");

        assertEquals("", exception.getMessage());
    }

    @Test
    void exception_isRuntimeException() {
        AuthFailedException exception = new AuthFailedException("Test message");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void exception_canBeThrown() {
        assertThrows(AuthFailedException.class, () -> {
            throw new AuthFailedException("Test exception");
        });
    }

    @Test
    void exception_withCustomMessage() {
        String customMessage = "Custom authentication failed message";
        AuthFailedException exception = new AuthFailedException(customMessage);

        assertEquals(customMessage, exception.getMessage());
    }
}
