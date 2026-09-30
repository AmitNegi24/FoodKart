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

            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }

            ServerRequest modifiedRequest =
                    ServerRequest.from(request)
                            .header("X-Correlation-ID", correlationId)
                            .build();

            ServerResponse response =
                    next.handle(modifiedRequest);

            response.headers().add(
                    "X-Correlation-ID",
                    correlationId
            );

            return response;
        };
    }
}