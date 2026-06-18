package com.estudios.demosafkaspring.handler;

import com.estudios.demosafkaspring.dto.OrderPlacedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@AllArgsConstructor
public class OtherTopicHandler implements KafkaMessageHandler {

    private ObjectMapper objectMapper;

    @Override
    public void handle(ConsumerRecord<String, String> order) {
        log.info("my-topic handler accesed");
        OrderPlacedEvent event = objectMapper.readValue(order.value(), OrderPlacedEvent.class);
        log.info("mapped value: " + event);
    }
}
