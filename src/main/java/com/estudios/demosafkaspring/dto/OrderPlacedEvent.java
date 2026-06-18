package com.estudios.demosafkaspring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderPlacedEvent {
    private String orderId;
    private String customerId;
    private BigDecimal totalAmount;
    private String currency;
    private Integer itemCount;
    private Instant placedAt;
}
