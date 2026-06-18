package com.estudios.demosafkaspring.consumer;

import com.estudios.demosafkaspring.handler.OtherTopicHandler;
import com.estudios.demosafkaspring.handler.PizzaOrderHandler;
import com.estudios.demosafkaspring.handler.ScheduleTopicHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final PizzaOrderHandler pizzaOrderHandler;
    private final OtherTopicHandler otherTopicHandler;
    private final ScheduleTopicHandler scheduleTopicHandler;

    @KafkaListener(topics = {"user-topic", "inventory-topic", "schedule-topic", "burger-topic"}, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> order) {
        log.info("Received message — topic: {}, partition: {}, offset: {}, key: {}, value: {}",
                order.topic(), order.partition(), order.offset(), order.key(), order.value());

        try {
         switch (order.topic()) {
             case "inventory-topic" -> pizzaOrderHandler.handle(order);
             case "schedule-topic" -> scheduleTopicHandler.handle(order);
             case "user-topic" -> otherTopicHandler.handle(order);
             default -> log.warn("No handler for topic: {}", order.topic());
         }
        } catch (Exception e) {
            log.error("Failed to process message — topic: {}, key: {}, error: {}",
                order.topic(), order.key(), e.getMessage(), e);
        }
    }
}