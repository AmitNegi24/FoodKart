package com.foodkart.order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class KafkaTopicConfigTest {

    @Test
    void testOrderTopicBean() {
        KafkaTopicConfig config = new KafkaTopicConfig();
        ReflectionTestUtils.setField(config, "topicName", "order-topic");

        NewTopic topic = config.orderTopic();

        assertNotNull(topic);
        assertEquals("order-topic", topic.name());
    }

    @Test
    void testOrderTopicBeanWithDifferentName() {
        KafkaTopicConfig config = new KafkaTopicConfig();
        ReflectionTestUtils.setField(config, "topicName", "custom-topic");

        NewTopic topic = config.orderTopic();

        assertNotNull(topic);
        assertEquals("custom-topic", topic.name());
    }
}
