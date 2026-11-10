
package com.foodkart.delivery_service.kafka;

import com.foodkart.delivery_service.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentSuccessConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DeliveryService deliveryService;

    @Mock
    private JsonNode event;

    @Mock
    private JsonNode orderIdNode;

    @Mock
    private JsonNode statusNode;

    private PaymentSuccessConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new PaymentSuccessConsumer(
                objectMapper,
                deliveryService
        );
    }

    private void mockReadyForPickup(Long orderId)
            throws Exception {

        when(objectMapper.readTree(anyString()))
                .thenReturn(event);

        when(event.get("orderId"))
                .thenReturn(orderIdNode);

        when(event.get("status"))
                .thenReturn(statusNode);

        when(orderIdNode.asLong())
                .thenReturn(orderId);

        when(statusNode.asText())
                .thenReturn("READY_FOR_PICKUP");
    }

    @Test
    void testReadyForPickupCreatesDelivery()
            throws Exception {

        mockReadyForPickup(101L);

        assertDoesNotThrow(() ->
                consumer.consumeOrderReadyForPickup(
                        "{\"orderId\":101,\"status\":\"READY_FOR_PICKUP\"}"
                )
        );

        verify(deliveryService, times(1))
                .createDelivery(101L);
    }

    @Test
    void testDifferentOrderId()
            throws Exception {

        mockReadyForPickup(202L);

        consumer.consumeOrderReadyForPickup(
                "{\"orderId\":202,\"status\":\"READY_FOR_PICKUP\"}"
        );

        verify(deliveryService, times(1))
                .createDelivery(202L);
    }

    @Test
    void testUnexpectedStatusIgnored()
            throws Exception {

        when(objectMapper.readTree(anyString()))
                .thenReturn(event);

        when(event.get("orderId"))
                .thenReturn(orderIdNode);

        when(event.get("status"))
                .thenReturn(statusNode);

        when(orderIdNode.asLong())
                .thenReturn(101L);

        when(statusNode.asText())
                .thenReturn("CONFIRMED");

        assertDoesNotThrow(() ->
                consumer.consumeOrderReadyForPickup(
                        "{\"orderId\":101,\"status\":\"CONFIRMED\"}"
                )
        );

        verifyNoInteractions(deliveryService);
    }

    @Test
    void testDeserializationError()
            throws Exception {

        when(objectMapper.readTree(anyString()))
                .thenThrow(new IllegalArgumentException(
                        "Invalid JSON"
                ));

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

        mockReadyForPickup(101L);

        when(deliveryService.createDelivery(101L))
                .thenThrow(new IllegalStateException(
                        "Database unavailable"
                ));

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
