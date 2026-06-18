package com.estudios.demosafkaspring.service;

import com.estudios.demosafkaspring.dto.BookingStatus;
import com.estudios.demosafkaspring.dto.ScheduleDocument;
import com.estudios.demosafkaspring.dto.ScheduleEvent;
import com.estudios.demosafkaspring.exceptions.InsufficientCapacityException;
import com.estudios.demosafkaspring.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleTransactionService {
    private final ScheduleRepository scheduleRepository;
    private final VenueCapacityService capacityService;

    @Transactional
    public ScheduleDocument reserveAndInsert(ScheduleEvent event, ConsumerRecord<String, String> eventConsumerRecord) {
        if (!capacityService.reserveSeats(event.getPartySize())) {
            throw new InsufficientCapacityException("booking rejected");
        }

        ScheduleDocument document = ScheduleDocument.builder()
                .bookingId(event.getBookingId())
                .guestName(event.getGuestName())
                .guestPhone(event.getGuestPhone())
                .guestEmail(event.getGuestEmail())
                .partySize(event.getPartySize())
                .dateOfReservation(event.getDateOfReservation())
                .createdAt(Instant.parse(event.getCreatedAt()))
                .bookingStatus(BookingStatus.ACTIVE)
                // Kafka metadata
                .kafkaTopic(eventConsumerRecord.topic())
                .kafkaPartition(eventConsumerRecord.partition())
                .kafkaOffset(eventConsumerRecord.offset())
                .savedAt(Instant.now())
                .build();

        ScheduleDocument saved = scheduleRepository.insert(document);
        log.info("Saved schedule — bookingId: {}, offset: {}", saved.getBookingId(), document.getKafkaOffset());
        return saved;
    }
}
