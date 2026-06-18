package com.estudios.demosafkaspring.repository;

import com.estudios.demosafkaspring.dto.BookingStatus;
import com.estudios.demosafkaspring.dto.ScheduleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends MongoRepository<ScheduleDocument, String> {

    Optional<ScheduleDocument> findByBookingId(String bookingId);

    Optional<ScheduleDocument> findByBookingIdAndBookingStatus(String bookingId, BookingStatus bookingStatus);
    boolean existsByBookingId(String bookingId);
    List<ScheduleDocument> findByGuestPhoneOrGuestEmail(String guestPhone, String guestEmail);

}
