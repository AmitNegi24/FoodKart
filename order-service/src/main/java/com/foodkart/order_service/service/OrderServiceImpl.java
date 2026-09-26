package com.foodkart.order_service.service;

import com.foodkart.order_service.client.MenuClient;
import com.foodkart.order_service.dto.*;
import com.foodkart.order_service.entity.Order;
import com.foodkart.order_service.entity.OrderItem;
import com.foodkart.order_service.entity.OrderStatus;
import com.foodkart.order_service.kafka.OrderEventProducer;

import com.foodkart.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final MenuClient menuClient;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

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
        String userId = authentication.getName();

        // 2. Create Order
        Order order = Order.builder()
                .userId(userId)
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

        // 11. Publish OrderCreated event to Kafka
        OrderCreatedEvent event =
                OrderCreatedEvent.builder()
                        .orderId(savedOrder.getId())
                        .userEmailId(savedOrder.getUserEmail())
                        .amount(savedOrder.getTotalAmount())
                        .build();

        orderEventProducer.publishOrderCreated(event);

        // 12. Convert OrderItems to response DTO
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

        // 13. Return response
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
}
