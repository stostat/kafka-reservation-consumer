package com.estudios.demosafkaspring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleEvent {
    private String bookingId;
    private String guestName;
    private BigDecimal totalAmountOfPeople;
    private LocalDate dateOfReservation;
    private String guestPhone;
    private String guestEmail;
    private Integer partySize;
    private String createdAt;
    private BookingEvent bookingEvent;
    private BookingStatus bookingStatus;
}