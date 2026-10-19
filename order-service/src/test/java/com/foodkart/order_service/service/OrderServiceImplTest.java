package com.foodkart.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;
import com.foodkart.order_service.client.MenuClient;
import com.foodkart.order_service.dto.*;
import com.foodkart.order_service.entity.*;
import com.foodkart.order_service.exception.OrderNotFoundException;
import com.foodkart.order_service.repository.OrderRepository;
import com.foodkart.order_service.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private MenuClient menuClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequestDTO orderRequestDTO;
    private Order savedOrder;
    private MenuItemDTO menuItemDTO;

    @BeforeEach
    void setUp() {
        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setMenuItemId(1L);
        item.setQuantity(2);

        orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setRestaurantId(100L);
        orderRequestDTO.setItems(List.of(item));

        FoodItemDTO foodItem = new FoodItemDTO();
        foodItem.setName("Burger");
        foodItem.setPrice(new BigDecimal("10.00"));
        foodItem.setAvailable(true);

        menuItemDTO = new MenuItemDTO();
        menuItemDTO.setRestaurantId(100L);
        menuItemDTO.setFoodItemId(1L);
        menuItemDTO.setFoodItem(foodItem);

        savedOrder = Order.builder()
                .id(1L)
                .userId("test@example.com")
                .userEmail("test@example.com")
                .restaurantId(100L)
                .totalAmount(new BigDecimal("20.00"))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@example.com", null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createOrder_success() throws JsonProcessingException {
        when(menuClient.getMenuItemById(1L)).thenReturn(menuItemDTO);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(objectMapper.writeValueAsString(any(OrderCreatedEvent.class))).thenReturn("{\"test\":\"data\"}");
        when(outboxEventRepository.save(any(OutboxEvent.class))).thenReturn(OutboxEvent.builder().build());

        OrderResponseDTO result = orderService.createOrder(orderRequestDTO, "correlation-123");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getUserEmailId());
        assertEquals(100L, result.getRestaurantId());
        verify(menuClient, times(1)).getMenuItemById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(outboxEventRepository, times(1)).save(any(OutboxEvent.class));
    }

    @Test
    void createOrder_noAuthentication_throwsException() {
        SecurityContextHolder.clearContext();

        assertThrows(IllegalStateException.class, () -> 
            orderService.createOrder(orderRequestDTO, "correlation-123")
        );

        verify(menuClient, never()).getMenuItemById(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_itemNotAvailable_throwsException() {
        menuItemDTO.getFoodItem().setAvailable(false);
        when(menuClient.getMenuItemById(1L)).thenReturn(menuItemDTO);

        assertThrows(RuntimeException.class, () -> 
            orderService.createOrder(orderRequestDTO, "correlation-123")
        );

        verify(menuClient, times(1)).getMenuItemById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_wrongRestaurant_throwsException() {
        menuItemDTO.setRestaurantId(200L);
        when(menuClient.getMenuItemById(1L)).thenReturn(menuItemDTO);

        assertThrows(RuntimeException.class, () -> 
            orderService.createOrder(orderRequestDTO, "correlation-123")
        );

        verify(menuClient, times(1)).getMenuItemById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderById_success() {
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .menuItemId(1L)
                .itemName("Burger")
                .price(new BigDecimal("10.00"))
                .quantity(2)
                .subtotal(new BigDecimal("20.00"))
                .build();

        savedOrder.setItems(List.of(orderItem));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        OrderResponseDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getUserEmailId());
        assertEquals(1, result.getItems().size());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_notFound_throwsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> 
            orderService.getOrderById(999L)
        );

        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    void updateOrderStatus_pendingToConfirmed_success() {
        savedOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        OrderResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_confirmedToPreparing_success() {
        savedOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        OrderResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.PREPARING);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_preparingToReadyForPickup_success() {
        savedOrder.setStatus(OrderStatus.PREPARING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        OrderResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.READY_FOR_PICKUP);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_readyForPickupToOutForDelivery_success() {
        savedOrder.setStatus(OrderStatus.READY_FOR_PICKUP);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        OrderResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.OUT_FOR_DELIVERY);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_outForDeliveryToDelivered_success() {
        savedOrder.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        OrderResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.DELIVERED);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_pendingToCancelled_success() {
        savedOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        OrderResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_invalidTransition_throwsException() {
        savedOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        assertThrows(IllegalStateException.class, () -> 
            orderService.updateOrderStatus(1L, OrderStatus.PREPARING)
        );

        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_orderNotFound_throwsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> 
            orderService.updateOrderStatus(999L, OrderStatus.PREPARING)
        );

        verify(orderRepository, times(1)).findById(999L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_multipleItems_success() throws JsonProcessingException {
        OrderItemRequestDTO item1 = new OrderItemRequestDTO();
        item1.setMenuItemId(1L);
        item1.setQuantity(2);

        OrderItemRequestDTO item2 = new OrderItemRequestDTO();
        item2.setMenuItemId(2L);
        item2.setQuantity(1);

        orderRequestDTO.setItems(List.of(item1, item2));

        MenuItemDTO menuItemDTO2 = new MenuItemDTO();
        menuItemDTO2.setRestaurantId(100L);
        menuItemDTO2.setFoodItemId(2L);
        FoodItemDTO foodItem2 = new FoodItemDTO();
        foodItem2.setName("Pizza");
        foodItem2.setPrice(new BigDecimal("15.00"));
        foodItem2.setAvailable(true);
        menuItemDTO2.setFoodItem(foodItem2);

        when(menuClient.getMenuItemById(1L)).thenReturn(menuItemDTO);
        when(menuClient.getMenuItemById(2L)).thenReturn(menuItemDTO2);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(objectMapper.writeValueAsString(any(OrderCreatedEvent.class))).thenReturn("{\"test\":\"data\"}");
        when(outboxEventRepository.save(any(OutboxEvent.class))).thenReturn(OutboxEvent.builder().build());

        OrderResponseDTO result = orderService.createOrder(orderRequestDTO, "correlation-123");

        assertNotNull(result);
        verify(menuClient, times(1)).getMenuItemById(1L);
        verify(menuClient, times(1)).getMenuItemById(2L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_withNullCorrelationId_success() throws JsonProcessingException {
        when(menuClient.getMenuItemById(1L)).thenReturn(menuItemDTO);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(objectMapper.writeValueAsString(any(OrderCreatedEvent.class))).thenReturn("{\"test\":\"data\"}");
        when(outboxEventRepository.save(any(OutboxEvent.class))).thenReturn(OutboxEvent.builder().build());

        OrderResponseDTO result = orderService.createOrder(orderRequestDTO, null);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
