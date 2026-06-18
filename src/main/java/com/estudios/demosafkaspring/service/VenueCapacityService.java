package com.estudios.demosafkaspring.service;

import com.estudios.demosafkaspring.dto.VenueCapacityDocument;
import com.estudios.demosafkaspring.repository.VenueCapacityRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Service
@RequiredArgsConstructor
public class VenueCapacityService {

    static final String CAPACITY_ID  = "venue-capacity";
    static final String CAPACITY_TYPE = "CAPACITY";
    static final int    TOTAL_SEATS  = 20;

    private final MongoTemplate mongoTemplate;
    private final VenueCapacityRepository capacityRepository;

    public void initCapacity() {
        Query query  = query(where("_id").is(CAPACITY_ID));
        Update update = new Update()
                .setOnInsert("id",         CAPACITY_ID)
                .setOnInsert("type",       CAPACITY_TYPE)
                .setOnInsert("totalSeats", TOTAL_SEATS)
                .setOnInsert("reserved",   0)
                .setOnInsert("available",  TOTAL_SEATS)
                .setOnInsert("updatedAt",  Instant.now());

        mongoTemplate.upsert(query, update, VenueCapacityDocument.class);
        log.info("Capacity document initialised — totalSeats: {}", TOTAL_SEATS);
    }

    // ── Reserve ───────────────────────────────────────────────────────────────

    /**
     * Atomically increments reserved by partySize and decrements available.
     * Returns the updated document, or null if not enough seats are left.
     *
     * Uses findAndModify so the check + update happen in a single atomic operation.
     */
    public boolean reserveSeats(int partySize) {
        UpdateResult r = mongoTemplate.updateFirst(
                query(where("available").gte(partySize)),
                new Update().inc("available", -partySize).inc("reserved", partySize),
                VenueCapacityDocument.class);
        return r.getModifiedCount() > 0;
    }

    // ── Cancel / Release ──────────────────────────────────────────────────────

    /**
     * Releases seats back to available when a booking is cancelled.
     */
    public void releaseSeats(int partySize) {
        mongoTemplate.updateFirst(
                query(where("id").is(CAPACITY_ID)),
                new Update().inc("available", partySize).inc("reserved", -partySize),
                VenueCapacityDocument.class);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Returns the current capacity state without modifying anything.
     */
    public VenueCapacityDocument getCapacity() {
        return capacityRepository.findById(CAPACITY_ID)
                .orElseThrow(() -> new IllegalStateException(
                        "Capacity document not found — call initCapacity() first"));
    }

    /**
     * Returns true if the requested party size can be accommodated.
     */
    public boolean hasAvailability(int partySize) {
        VenueCapacityDocument cap = getCapacity();
        return cap.getAvailable() >= partySize;
    }
}
