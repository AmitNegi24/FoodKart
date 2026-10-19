package com.foodkart.order_service.kafka;

import tools.jackson.databind.ObjectMapper;
import com.foodkart.order_service.dto.PaymentFailedEvent;
import com.foodkart.order_service.entity.Order;
import com.foodkart.order_service.entity.OrderStatus;
import com.foodkart.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentFailedConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentFailedConsumer paymentFailedConsumer;

    private PaymentFailedEvent paymentFailedEvent;
    private Order order;

    @BeforeEach
    void setUp() throws Exception {
        paymentFailedEvent = PaymentFailedEvent.builder()
                .orderId(1L)
                .reason("Insufficient funds")
                .build();

        order = Order.builder()
                .id(1L)
                .userId("test@example.com")
                .userEmail("test@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .status(OrderStatus.PENDING)
                .build();

        when(objectMapper.readValue(any(String.class), eq(PaymentFailedEvent.class)))
                .thenReturn(paymentFailedEvent);
    }

    @Test
    void consumePaymentFailed_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        paymentFailedConsumer.consumePaymentFailed("{\"orderId\":1}");

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void consumePaymentFailed_orderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            paymentFailedConsumer.consumePaymentFailed("{\"orderId\":1}")
        );

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
