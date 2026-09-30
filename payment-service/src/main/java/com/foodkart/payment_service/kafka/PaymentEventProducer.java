package com.foodkart.payment_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentSuccess(String payload, String orderId) {

        try {

            kafkaTemplate.send(
                    "payment-success",
                    orderId,
                    payload
            ).get();

            System.out.println(
                    "PAYMENT_SUCCESS event published for order: "
                            + orderId
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to publish PAYMENT_SUCCESS event"
            );

            throw new RuntimeException(
                    "Failed to publish PAYMENT_SUCCESS event",
                    e
            );
        }
    }

    public void publishPaymentFailed(String payload, String orderId) {

        try {

            kafkaTemplate.send(
                    "payment-failed",
                    orderId,
                    payload
            ).get();

            System.out.println(
                    "PAYMENT_FAILED event published for order: "
                            + orderId
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to publish PAYMENT_FAILED event"
            );

            throw new RuntimeException(
                    "Failed to publish PAYMENT_FAILED event",
                    e
            );
        }
    }
}

