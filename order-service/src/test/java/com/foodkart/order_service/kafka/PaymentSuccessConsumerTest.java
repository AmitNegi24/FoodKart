package com.foodkart.order_service.kafka;

import tools.jackson.databind.ObjectMapper;
import com.foodkart.order_service.dto.PaymentSuccessEvent;
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
class PaymentSuccessConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentSuccessConsumer paymentSuccessConsumer;

    private PaymentSuccessEvent paymentSuccessEvent;
    private Order order;

    @BeforeEach
    void setUp() throws Exception {
        paymentSuccessEvent = PaymentSuccessEvent.builder()
                .orderId(1L)
                .amount(new BigDecimal("20.00"))
                .build();

        order = Order.builder()
                .id(1L)
                .userId("test@example.com")
                .userEmail("test@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .status(OrderStatus.PENDING)
                .build();

        when(objectMapper.readValue(any(String.class), eq(PaymentSuccessEvent.class)))
                .thenReturn(paymentSuccessEvent);
    }

    @Test
    void consumePaymentSuccess_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        paymentSuccessConsumer.consumePaymentSuccess("{\"orderId\":1}");

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void consumePaymentSuccess_orderAlreadyConfirmed() {
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        paymentSuccessConsumer.consumePaymentSuccess("{\"orderId\":1}");

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void consumePaymentSuccess_orderNotPending() {
        order.setStatus(OrderStatus.PREPARING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        paymentSuccessConsumer.consumePaymentSuccess("{\"orderId\":1}");

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void consumePaymentSuccess_orderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            paymentSuccessConsumer.consumePaymentSuccess("{\"orderId\":1}")
        );

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
