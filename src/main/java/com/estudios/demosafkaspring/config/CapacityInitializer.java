package com.estudios.demosafkaspring.config;

import com.estudios.demosafkaspring.service.VenueCapacityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CapacityInitializer implements CommandLineRunner {

    private final VenueCapacityService venueCapacityService;

    @Override
    public void run(String... args) {
        venueCapacityService.initCapacity();
        var cap = venueCapacityService.getCapacity();
        log.info("Venue capacity — total: {}, reserved: {}, available: {}",
                cap.getTotalSeats(), cap.getReserved(), cap.getAvailable());
    }
}
