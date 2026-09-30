package com.foodkart.payment_service.kafka;

import com.foodkart.payment_service.entity.OutboxStatus;
import com.foodkart.payment_service.entity.PaymentOutboxEvent;
import com.foodkart.payment_service.repository.PaymentOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentOutboxPublisher {

    private final PaymentOutboxEventRepository paymentOutboxEventRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<PaymentOutboxEvent> events =
                paymentOutboxEventRepository
                        .findByStatusOrderByCreatedAtAsc(
                                OutboxStatus.PENDING
                        );

        for (PaymentOutboxEvent event : events) {

            try {

                String orderId =
                        event.getAggregateId().toString();

                if ("PAYMENT_SUCCESS".equals(event.getEventType())) {

                    paymentEventProducer.publishPaymentSuccess(
                            event.getPayload(),
                            orderId
                    );

                } else if ("PAYMENT_FAILED".equals(event.getEventType())) {

                    paymentEventProducer.publishPaymentFailed(
                            event.getPayload(),
                            orderId
                    );

                } else {

                    throw new RuntimeException(
                            "Unknown payment event type: "
                                    + event.getEventType()
                    );
                }

                event.setStatus(OutboxStatus.PROCESSED);
                paymentOutboxEventRepository.save(event);

                System.out.println(
                        "Payment outbox event processed: "
                                + event.getId()
                );

            } catch (Exception e) {

                System.err.println(
                        "Failed to publish payment outbox event: "
                                + event.getId()
                );

                e.printStackTrace();

                // Event remains PENDING and will be retried.
            }
        }
    }
}

