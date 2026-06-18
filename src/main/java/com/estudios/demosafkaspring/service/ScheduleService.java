package com.estudios.demosafkaspring.service;

import com.estudios.demosafkaspring.dto.BookingStatus;
import com.estudios.demosafkaspring.dto.ScheduleDocument;
import com.estudios.demosafkaspring.dto.ScheduleEvent;
import com.estudios.demosafkaspring.repository.ScheduleRepository;
import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final VenueCapacityService capacityService;
    private final ScheduleTransactionService scheduleTransactionService;
    private final MongoTemplate mongoTemplate;

    public ScheduleDocument save(ScheduleEvent event, ConsumerRecord<String, String> eventConsumerRecord) {

        String bookingId = event.getBookingId();

        Optional<ScheduleDocument> existing = scheduleRepository.findByBookingId(bookingId);
        if (existing.isPresent()) {
            log.warn("Duplicate event ignored — bookingId: {}", bookingId);
            return existing.get();
        }

        try {
            return scheduleTransactionService.reserveAndInsert(event, eventConsumerRecord);
        } catch (DuplicateKeyException error) {
            log.warn("Concurrent duplicate detected — bookingId: {}", bookingId);
            return scheduleRepository.findByBookingId(bookingId)
                    .orElseThrow(() -> new IllegalStateException("Lost duplicate for " + bookingId));
        }
    }

    public void cancel(String guestPhone, String guestEmail) {

        Query query = query(
                new Criteria().andOperator(
                        where("bookingStatus").is(BookingStatus.ACTIVE),
                        new Criteria().orOperator(
                                where("guestPhone").is(guestPhone),
                                where("guestEmail").is(guestEmail))));

        ScheduleDocument cancelled = mongoTemplate.findAndModify(
                query,
                new Update().set("bookingStatus", BookingStatus.CANCELLED)
                        .set("savedAt", Instant.now()),
                FindAndModifyOptions.options().returnNew(false),
                ScheduleDocument.class);
        if (cancelled == null) {
            log.warn("No active reservation to cancel for phone/email");
            return;
        }

        capacityService.releaseSeats(cancelled.getPartySize());
        log.info("Booking cancelled — bookingId: {}, {} seats released",
                cancelled.getBookingId(), cancelled.getPartySize());
    }

}

//TODO: Example for multithreading on Java 19
/*
public class ScheduleService {

    private final BookingRepository bookingRepository;
    private final NotificationClient notificationClient;
    private final AuditClient auditClient;
    private final CalendarClient calendarClient;

    public void save(ScheduleEvent event, ConsumerRecord<String, String> record) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var persist = executor.submit(() -> bookingRepository.save(toEntity(event)));
            var notify  = executor.submit(() -> notificationClient.sendConfirmation(event));
            var audit   = executor.submit(() -> auditClient.recordBooking(event, record.offset()));
            var calendar = executor.submit(() -> calendarClient.block(event.getSlot()));

            // Await all and surface failures
            persist.get();
            notify.get();
            audit.get();
            calendar.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EventProcessingException("Interrupted while saving event", e);
        } catch (ExecutionException e) {
            throw new EventProcessingException("Failed to save event", e.getCause());
        }
    }

    public void cancel(String guestPhone, String guestEmail) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var deleteBooking = executor.submit(() -> bookingRepository.deleteByContact(guestPhone, guestEmail));
            var notifyGuest   = executor.submit(() -> notificationClient.sendCancellation(guestPhone, guestEmail));
            var releaseSlot   = executor.submit(() -> calendarClient.release(guestPhone, guestEmail));

            deleteBooking.get();
            notifyGuest.get();
            releaseSlot.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EventProcessingException("Interrupted while cancelling", e);
        } catch (ExecutionException e) {
            throw new EventProcessingException("Failed to cancel event", e.getCause());
        }
    }
}
 */

//TODO: Example of multithreading in java 21-25
/*
public void save(ScheduleEvent event, ConsumerRecord<String, String> record) throws InterruptedException {
    try (var scope = StructuredTaskScope.open()) {
        var persist  = scope.fork(() -> bookingRepository.save(toEntity(event)));
        var notify   = scope.fork(() -> notificationClient.sendConfirmation(event));
        var audit    = scope.fork(() -> auditClient.recordBooking(event, record.offset()));
        var calendar = scope.fork(() -> calendarClient.block(event.getSlot()));

        scope.join();  // wait for all, propagate any failure
    }
}
 */