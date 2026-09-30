package com.foodkart.api_gateway.config;

import com.foodkart.api_gateway.filter.CorrelationIdFilter;
import org.springframework.cloud.gateway.server.mvc.filter.SimpleFilterSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayFilterConfig {

    public static class CorrelationIdFilterSupplier
            extends SimpleFilterSupplier {

        public CorrelationIdFilterSupplier() {
            super(CorrelationIdFilter.class);
        }
    }

    @Bean
    public CorrelationIdFilterSupplier correlationIdFilterSupplier() {
        return new CorrelationIdFilterSupplier();
    }
}