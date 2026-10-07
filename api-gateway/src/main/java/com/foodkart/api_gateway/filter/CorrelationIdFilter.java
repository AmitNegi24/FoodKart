package com.foodkart.api_gateway.filter;

import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

public class CorrelationIdFilter {

    public static HandlerFilterFunction<ServerResponse, ServerResponse>
    addCorrelationId() {

        return (request, next) -> {

            String correlationId =
                    request.headers().firstHeader("X-Correlation-ID");

            // If client did not send one, generate it
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }

            final String finalCorrelationId = correlationId;

            // Only add the header if it was missing
            ServerRequest modifiedRequest = request;

            if (request.headers().firstHeader("X-Correlation-ID") == null) {

                modifiedRequest =
                        ServerRequest.from(request)
                                .header(
                                        "X-Correlation-ID",
                                        finalCorrelationId
                                )
                                .build();
            }

            ServerResponse response =
                    next.handle(modifiedRequest);

            response.headers().set(
                    "X-Correlation-ID",
                    finalCorrelationId
            );

            return response;
        };
    }
}