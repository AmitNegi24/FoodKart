package com.foodkart.payment_service.kafka;

import com.foodkart.payment_service.dto.PaymentFailedEvent;
import com.foodkart.payment_service.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishPaymentSuccess(PaymentSuccessEvent event) {

        try {

            String message =
                    objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    "payment-success",
                    event.getOrderId().toString(),
                    message
            );

            System.out.println(
                    "PAYMENT_SUCCESS event published for order: "
                            + event.getOrderId()
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to publish PAYMENT_SUCCESS event"
            );

            e.printStackTrace();
        }
    }
    public void publishPaymentFailed(PaymentFailedEvent event) {

        try {

            String message =
                    objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    "payment-failed",
                    event.getOrderId().toString(),
                    message
            );

            System.out.println(
                    "PAYMENT_FAILED event published for order: "
                            + event.getOrderId()
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to publish PAYMENT_FAILED event"
            );

            e.printStackTrace();
        }
    }
}