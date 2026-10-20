package com.foodkart.payment_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handlePaymentNotFound_returnsNotFoundResponse() {
        String message = "Payment not found for order: 123";
        PaymentNotFoundException exception = new PaymentNotFoundException(message);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handlePaymentNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().get("status"));
        assertEquals("Payment Not Found", response.getBody().get("error"));
        assertEquals(message, response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handlePaymentProcessing_returnsInternalServerErrorResponse() {
        String message = "Payment processing failed";
        PaymentProcessingException exception = new PaymentProcessingException(message);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handlePaymentProcessing(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().get("status"));
        assertEquals("Payment Processing Failed", response.getBody().get("error"));
        assertEquals(message, response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handlePaymentProcessing_withCause_returnsInternalServerErrorResponse() {
        String message = "Payment timeout";
        Throwable cause = new RuntimeException("Network error");
        PaymentProcessingException exception = new PaymentProcessingException(message, cause);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handlePaymentProcessing(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().get("status"));
        assertEquals("Payment Processing Failed", response.getBody().get("error"));
        assertEquals(message, response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleGenericException_returnsInternalServerErrorResponse() {
        String message = "Unexpected error occurred";
        Exception exception = new Exception(message);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("Something went wrong", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handlePaymentNotFound_responseStructure() {
        PaymentNotFoundException exception = new PaymentNotFoundException("Test message");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handlePaymentNotFound(exception);
        Map<String, Object> body = response.getBody();

        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("message"));
        assertEquals(4, body.size());
    }

    @Test
    void handlePaymentProcessing_responseStructure() {
        PaymentProcessingException exception = new PaymentProcessingException("Test message");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handlePaymentProcessing(exception);
        Map<String, Object> body = response.getBody();

        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("message"));
        assertEquals(4, body.size());
    }

    @Test
    void handleGenericException_responseStructure() {
        Exception exception = new Exception("Test message");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(exception);
        Map<String, Object> body = response.getBody();

        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("message"));
        assertEquals(4, body.size());
    }
}
