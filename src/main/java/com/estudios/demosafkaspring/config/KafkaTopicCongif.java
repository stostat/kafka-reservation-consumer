package com.estudios.demosafkaspring.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicCongif {
    @Bean
    public NewTopic userTopic() {
        return TopicBuilder.name("user-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean NewTopic scheduleTopic() {
        return TopicBuilder.name("schedule_topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean NewTopic inventoryTopic() {
        return TopicBuilder.name("inventory_topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean NewTopic burgerTopic() {
        return TopicBuilder.name("burger_topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
