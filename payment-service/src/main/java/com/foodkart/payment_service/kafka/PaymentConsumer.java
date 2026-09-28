package com.foodkart.payment_service.kafka;

import com.foodkart.payment_service.dto.PaymentRequestDTO;
import com.foodkart.payment_service.dto.PaymentResponseDTO;
import com.foodkart.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class PaymentConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    public PaymentConsumer(ObjectMapper objectMapper,
                           PaymentService paymentService) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;

        System.out.println("🔥 PaymentConsumer BEAN CREATED");
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "payment-service-group"
    )
    public void consumeOrderCreated(String message) {

        System.out.println("🔥🔥 KAFKA LISTENER CALLED");

        try {
            System.out.println("Received message: " + message);

            PaymentRequestDTO request =
                    objectMapper.readValue(
                            message,
                            PaymentRequestDTO.class
                    );

            PaymentResponseDTO response =
                    paymentService.processPayment(request);

            System.out.println(
                    "Payment processed. Payment ID: "
                            + response.getId()
            );

            System.out.println(
                    "Payment Status: "
                            + response.getStatus()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}