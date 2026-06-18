package com.estudios.demosafkaspring.config;

import com.estudios.demosafkaspring.dto.ScheduleDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.PartialIndexFilter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleIndexes {

    private final MongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void ensureIndexes() {
        IndexOperations ops = mongoTemplate.indexOps(ScheduleDocument.class);

        ops.createIndex(new Index().on("guestPhone", Sort.Direction.ASC).unique()
                .partial(PartialIndexFilter.of(
                        Criteria.where("bookingStatus").is("ACTIVE")
                                .and("guestPhone").exists(true))));

        ops.createIndex(new Index().on("guestEmail", Sort.Direction.ASC).unique()
                .partial(PartialIndexFilter.of(
                        Criteria.where("bookingStatus").is("ACTIVE")
                                .and("guestEmail").exists(true))));
    }
}
