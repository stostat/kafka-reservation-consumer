package com.estudios.demosafkaspring.handler;

import com.estudios.demosafkaspring.dto.ScheduleEvent;
import com.estudios.demosafkaspring.exceptions.InsufficientCapacityException;
import com.estudios.demosafkaspring.service.ScheduleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@AllArgsConstructor
public class ScheduleTopicHandler implements KafkaMessageHandler {

    private ObjectMapper objectMapper;
    private ScheduleService scheduleService;

    @Override
    public void handle(ConsumerRecord<String, String> order) throws JacksonException {
        ScheduleEvent event = objectMapper.readValue(order.value(), ScheduleEvent.class);
        log.info("mapped value: " + event);
        switch (event.getBookingEvent()) {
            case BOOKING, CONFIRMATION -> {
                try {
                scheduleService.save(event, order);
            } catch (InsufficientCapacityException e) {
                log.warn("Booking not accepted — key {}: {}", order.key(), e.getMessage());
            } }
            case ANNULATION -> scheduleService.cancel(event.getGuestPhone(), event.getGuestEmail());
            default -> log.warn("No method found for event {}", event.getBookingEvent());
        }
    }
}
