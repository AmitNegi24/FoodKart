
package com.foodkart.api_gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CorrelationIdFilterTest {

    private ServerRequest createRequest(String correlationId) {
        MockHttpServletRequest servletRequest =
                new MockHttpServletRequest("GET", "/test");

        if (correlationId != null) {
            servletRequest.addHeader(
                    "X-Correlation-ID",
                    correlationId
            );
        }

        return ServerRequest.create(servletRequest, List.of());
    }

    @Test
    void generatesNewIdWhenMissing() throws Exception {
        ServerRequest request = createRequest(null);

        ServerResponse response = mock(ServerResponse.class);
        HttpHeaders responseHeaders = new HttpHeaders();

        when(response.headers()).thenReturn(responseHeaders);

        AtomicReference<ServerRequest> forwardedRequest =
                new AtomicReference<>();

        HandlerFunction<ServerResponse> next = req -> {
            forwardedRequest.set(req);
            return response;
        };

        HandlerFilterFunction<ServerResponse, ServerResponse> filter =
                CorrelationIdFilter.addCorrelationId();

        ServerResponse result = filter.filter(request, next);

        assertSame(response, result);
        assertNotNull(forwardedRequest.get());

        String generatedId = forwardedRequest.get()
                .headers()
                .firstHeader("X-Correlation-ID");

        assertNotNull(generatedId);
        assertDoesNotThrow(() -> UUID.fromString(generatedId));

        assertEquals(
                generatedId,
                responseHeaders.getFirst("X-Correlation-ID")
        );
    }

    @Test
    void preservesExistingId() throws Exception {
        ServerRequest request = createRequest("existing-id-123");

        ServerResponse response = mock(ServerResponse.class);
        HttpHeaders responseHeaders = new HttpHeaders();

        when(response.headers()).thenReturn(responseHeaders);

        AtomicReference<ServerRequest> forwardedRequest =
                new AtomicReference<>();

        HandlerFunction<ServerResponse> next = req -> {
            forwardedRequest.set(req);
            return response;
        };

        ServerResponse result =
                CorrelationIdFilter.addCorrelationId()
                        .filter(request, next);

        assertSame(response, result);
        assertSame(request, forwardedRequest.get());

        assertEquals(
                "existing-id-123",
                responseHeaders.getFirst("X-Correlation-ID")
        );
    }

    @Test
    void generatesNewIdWhenBlank() throws Exception {
        ServerRequest request = createRequest("   ");

        ServerResponse response = mock(ServerResponse.class);
        HttpHeaders responseHeaders = new HttpHeaders();

        when(response.headers()).thenReturn(responseHeaders);

        AtomicReference<ServerRequest> forwardedRequest =
                new AtomicReference<>();

        HandlerFunction<ServerResponse> next = req -> {
            forwardedRequest.set(req);
            return response;
        };

        CorrelationIdFilter.addCorrelationId()
                .filter(request, next);

        // Current production behavior:
        // Blank request header is not replaced,
        // but a new ID is set on the response.
        assertSame(request, forwardedRequest.get());

        String generatedId =
                responseHeaders.getFirst("X-Correlation-ID");

        assertNotNull(generatedId);
        assertDoesNotThrow(() -> UUID.fromString(generatedId));
        assertNotEquals("   ", generatedId);
    }
}
