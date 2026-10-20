package com.foodkart.delivery_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid order ID");
        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Invalid Request", body.get("error"));
        assertEquals("Invalid order ID", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleIllegalState() {
        IllegalStateException ex = new IllegalStateException("Delivery already assigned");
        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleIllegalState(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.get("status"));
        assertEquals("Conflict", body.get("error"));
        assertEquals("Delivery already assigned", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }
}
