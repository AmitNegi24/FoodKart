
package com.foodkart.api_gateway.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GatewayFilterConfigTest {

    @Test
    void correlationIdFilterSupplierConstructor() {
        GatewayFilterConfig.CorrelationIdFilterSupplier supplier =
                new GatewayFilterConfig.CorrelationIdFilterSupplier();

        assertNotNull(supplier);
    }

    @Test
    void correlationIdFilterSupplierBean() {
        GatewayFilterConfig config = new GatewayFilterConfig();

        GatewayFilterConfig.CorrelationIdFilterSupplier supplier =
                config.correlationIdFilterSupplier();

        assertNotNull(supplier);
        assertInstanceOf(
                GatewayFilterConfig.CorrelationIdFilterSupplier.class,
                supplier
        );
    }

    @Test
    void correlationIdFilterSupplierBeanReturnsNewInstance() {
        GatewayFilterConfig config = new GatewayFilterConfig();

        var first = config.correlationIdFilterSupplier();
        var second = config.correlationIdFilterSupplier();

        assertNotSame(first, second);
    }
}
