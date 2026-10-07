package com.foodkart.order_service.service;


import com.foodkart.order_service.client.MenuClient;
import com.foodkart.order_service.dto.*;
import com.foodkart.order_service.entity.*;
import com.foodkart.order_service.exception.OrderNotFoundException;
import com.foodkart.order_service.repository.OrderRepository;
import com.foodkart.order_service.repository.OutboxEventRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final MenuClient menuClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request, String correlationId) {

        // 1. Get logged-in user from JWT
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "User not authenticated"
            );
        }

        // JWTFilter stores email as the principal
        String userEmailId = authentication.getName();

        // 2. Create Order
        Order order = Order.builder()
                .userId(userEmailId)
                .userEmail(userEmailId)
                .restaurantId(request.getRestaurantId())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. Process each menu item
        for (var itemRequest : request.getItems()) {

            System.out.println(
                    "Menu Item ID from request = "
                            + itemRequest.getMenuItemId()
            );

            // Get menu item from Menu Service
            MenuItemDTO menuItem =
                    menuClient.getMenuItemById(
                            itemRequest.getMenuItemId()
                    );

            // 4. Check availability
            if (!menuItem.getFoodItem().getAvailable()) {

                throw new RuntimeException(
                        "Menu item is not available: "
                                + menuItem.getFoodItem().getName()
                );
            }

            // 5. Check restaurant
            if (!menuItem.getRestaurantId()
                    .equals(request.getRestaurantId())) {

                throw new RuntimeException(
                        "Menu item does not belong to this restaurant: "
                                + menuItem.getFoodItem().getName()
                );
            }

            // 6. Calculate subtotal
            BigDecimal subtotal =
                    menuItem.getFoodItem().getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest.getQuantity()
                                    )
                            );

            // 7. Create OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItemId(menuItem.getFoodItemId())
                    .itemName(menuItem.getFoodItem().getName())
                    .price(menuItem.getFoodItem().getPrice())
                    .quantity(itemRequest.getQuantity())
                    .subtotal(subtotal)
                    .build();

            order.getItems().add(orderItem);

            // 8. Add to order total
            totalAmount = totalAmount.add(subtotal);
        }

        // 9. Set total amount
        order.setTotalAmount(totalAmount);

        // 10. Save Order
        Order savedOrder =
                orderRepository.save(order);

        // 11. Create OrderCreated Event
        OrderCreatedEvent event =
                OrderCreatedEvent.builder()
                        .orderId(savedOrder.getId())
                        .userEmailId(savedOrder.getUserEmail())
                        .amount(savedOrder.getTotalAmount())
                        .correlationId(correlationId)
                        .build();

        // 12. Convert event to JSON
        String eventPayload;

        eventPayload =
                objectMapper.writeValueAsString(event);

        // 13. Save event in Outbox table
        OutboxEvent outboxEvent =
                OutboxEvent.builder()
                        .aggregateType("ORDER")
                        .aggregateId(
                                savedOrder.getId().toString()
                        )
                        .eventType("ORDER_CREATED")
                        .payload(eventPayload)
                        .status(OutboxStatus.PENDING)
                        .build();

        outboxEventRepository.save(outboxEvent);

        // 14. Convert OrderItems to response DTO
        List<OrderItemResponseDTO> itemResponses =
                savedOrder.getItems()
                        .stream()
                        .map(item ->
                                OrderItemResponseDTO.builder()
                                        .menuItemId(
                                                item.getMenuItemId()
                                        )
                                        .itemName(
                                                item.getItemName()
                                        )
                                        .price(
                                                item.getPrice()
                                        )
                                        .quantity(
                                                item.getQuantity()
                                        )
                                        .subtotal(
                                                item.getSubtotal()
                                        )
                                        .build()
                        )
                        .toList();

        // 15. Return response
        return OrderResponseDTO.builder()
                .id(savedOrder.getId())
                .userEmailId(savedOrder.getUserEmail())
                .restaurantId(savedOrder.getRestaurantId())
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus())
                .createdAt(savedOrder.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new OrderNotFoundException("Order not found");
        }

        List<OrderItemResponseDTO> orderItems = order.get().getItems().stream()
                .map(item -> OrderItemResponseDTO.builder()
                        .menuItemId(item.getMenuItemId())
                        .itemName(item.getItemName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();


        return OrderResponseDTO.builder()
                .id(order.get().getId())
                .userEmailId(order.get().getUserEmail())
                .restaurantId(order.get().getRestaurantId())
                .totalAmount(order.get().getTotalAmount())
                .status(order.get().getStatus())
                .createdAt(order.get().getCreatedAt())
                .items(orderItems)
                .build();
    }
}