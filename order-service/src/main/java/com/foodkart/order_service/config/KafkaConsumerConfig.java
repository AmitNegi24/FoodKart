package com.foodkart.order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, Object> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, exception) ->
                                new TopicPartition(
                                        record.topic() + ".DLT",
                                        record.partition()
                                )
                );

        FixedBackOff fixedBackOff =
                new FixedBackOff(
                        3000L,
                        2L
                );

        return new DefaultErrorHandler(
                recoverer,
                fixedBackOff
        );
    }

    @Bean
    public NewTopic paymentSuccessDltTopic() {

        return TopicBuilder
                .name("payment-success.DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentFailedDltTopic() {

        return TopicBuilder
                .name("payment-failed.DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }
}

