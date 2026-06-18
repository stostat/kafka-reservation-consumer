package com.estudios.demosafkaspring.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "schedule")
public class VenueCapacityDocument {
    @Id
    private String id;          // always "venue-capacity"

    private String type;        // always "CAPACITY" — lets you filter it out of booking queries

    private int totalSeats;     // 20 — never changes
    private int reserved;       // sum of all partySize values in confirmed bookings
    private int available;      // totalSeats - reserved

    private Instant updatedAt;
}
