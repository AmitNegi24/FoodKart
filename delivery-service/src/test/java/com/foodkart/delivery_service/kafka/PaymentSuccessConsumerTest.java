
package com.foodkart.delivery_service.kafka;

import com.foodkart.delivery_service.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentSuccessConsumerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DeliveryService deliveryService;

    private PaymentSuccessConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new PaymentSuccessConsumer(
                objectMapper,
                deliveryService
        );
    }

    @Test
    void testReadyForPickupCreatesDelivery()
            throws Exception {

        consumer.consumeOrderReadyForPickup(
                "{\"orderId\":101,\"status\":\"READY_FOR_PICKUP\"}"
        );

        verify(deliveryService, times(1))
                .createDelivery(101L);
    }

    @Test
    void testDifferentOrderId()
            throws Exception {

        consumer.consumeOrderReadyForPickup(
                "{\"orderId\":202,\"status\":\"READY_FOR_PICKUP\"}"
        );

        verify(deliveryService, times(1))
                .createDelivery(202L);
    }

    @Test
    void testUnexpectedStatusIgnored()
            throws Exception {

        consumer.consumeOrderReadyForPickup(
                "{\"orderId\":101,\"status\":\"CONFIRMED\"}"
        );

        verifyNoInteractions(deliveryService);
    }

    @Test
    void testDeserializationError()
            throws Exception {

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> consumer.consumeOrderReadyForPickup(
                        "invalid json"
                )
        );

        assertEquals(
                "Failed to process order-ready-for-pickup event",
                exception.getMessage()
        );

        verifyNoInteractions(deliveryService);
    }

    @Test
    void testDeliveryServiceError()
            throws Exception {

        doThrow(new IllegalStateException("Database unavailable"))
                .when(deliveryService).createDelivery(101L);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> consumer.consumeOrderReadyForPickup(
                        "{\"orderId\":101,\"status\":\"READY_FOR_PICKUP\"}"
                )
        );

        assertEquals(
                "Failed to process order-ready-for-pickup event",
                exception.getMessage()
        );

        verify(deliveryService, times(1))
                .createDelivery(101L);
    }
}
