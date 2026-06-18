package com.estudios.demosafkaspring.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@Document(collection = "schedule")
public class ScheduleDocument {

    @MongoId
    private String bookingId;

    private String  guestName;
    private String  guestPhone;
    private String guestEmail;
    private Integer partySize;
    private LocalDate dateOfReservation;
    private Instant createdAt;

    private String  kafkaTopic;
    private Integer kafkaPartition;
    private Long    kafkaOffset;

    private Instant savedAt;

    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.ACTIVE;
}
