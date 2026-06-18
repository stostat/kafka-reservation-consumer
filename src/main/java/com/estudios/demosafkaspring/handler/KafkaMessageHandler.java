package com.estudios.demosafkaspring.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaMessageHandler {
    void handle(ConsumerRecord<String, String> order) throws Exception;
}
