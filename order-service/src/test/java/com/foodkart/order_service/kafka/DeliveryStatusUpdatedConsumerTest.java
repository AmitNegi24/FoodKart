
package com.foodkart.order_service.kafka;

import tools.jackson.databind.ObjectMapper;
import com.foodkart.order_service.dto.DeliveryStatusUpdatedEvent;
import com.foodkart.order_service.entity.Order;
import com.foodkart.order_service.entity.OrderStatus;
import com.foodkart.order_service.repository.OrderRepository;
import com.foodkart.order_service.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryStatusUpdatedConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private DeliveryStatusUpdatedConsumer deliveryStatusUpdatedConsumer;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1L)
                .userId("test@example.com")
                .userEmail("test@example.com")
                .restaurantId(100L)
                .status(OrderStatus.READY_FOR_PICKUP)
                .build();
    }

    private void mockEvent(String status) throws Exception {
        DeliveryStatusUpdatedEvent event =
                new DeliveryStatusUpdatedEvent(1L, status);

        when(objectMapper.readValue(
                anyString(),
                eq(DeliveryStatusUpdatedEvent.class)
        )).thenReturn(event);
    }

    @Test
    void consumeDeliveryStatusUpdated_pickedUp_success() throws Exception {
        mockEvent("PICKED_UP");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        deliveryStatusUpdatedConsumer.consumeDeliveryStatusUpdated(
                "{\"orderId\":1,\"status\":\"PICKED_UP\"}"
        );

        verify(orderRepository).findById(1L);
        verify(orderService).updateOrderStatus(
                1L, OrderStatus.OUT_FOR_DELIVERY
        );
    }

    @Test
    void consumeDeliveryStatusUpdated_delivered_success() throws Exception {
        mockEvent("DELIVERED");

        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        deliveryStatusUpdatedConsumer.consumeDeliveryStatusUpdated(
                "{\"orderId\":1,\"status\":\"DELIVERED\"}"
        );

        verify(orderRepository).findById(1L);
        verify(orderService).updateOrderStatus(
                1L, OrderStatus.DELIVERED
        );
    }

    @Test
    void consumeDeliveryStatusUpdated_noStatusUpdate() throws Exception {
        mockEvent("IN_TRANSIT");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        deliveryStatusUpdatedConsumer.consumeDeliveryStatusUpdated(
                "{\"orderId\":1,\"status\":\"IN_TRANSIT\"}"
        );

        verify(orderRepository).findById(1L);
        verify(orderService, never())
                .updateOrderStatus(any(), any());
    }

    @Test
    void consumeDeliveryStatusUpdated_duplicateEvent() throws Exception {
        mockEvent("PICKED_UP");

        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        deliveryStatusUpdatedConsumer.consumeDeliveryStatusUpdated(
                "{\"orderId\":1,\"status\":\"PICKED_UP\"}"
        );

        verify(orderRepository).findById(1L);
        verify(orderService, never())
                .updateOrderStatus(any(), any());
    }

    @Test
    void consumeDeliveryStatusUpdated_orderNotFound() throws Exception {
        mockEvent("PICKED_UP");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                deliveryStatusUpdatedConsumer.consumeDeliveryStatusUpdated(
                        "{\"orderId\":1,\"status\":\"PICKED_UP\"}"
                )
        );

        verify(orderRepository).findById(1L);
        verify(orderService, never())
                .updateOrderStatus(any(), any());
    }
}
