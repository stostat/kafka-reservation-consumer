package com.estudios.demosafkaspring.repository;

import com.estudios.demosafkaspring.dto.VenueCapacityDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VenueCapacityRepository extends MongoRepository<VenueCapacityDocument, String> {
}
