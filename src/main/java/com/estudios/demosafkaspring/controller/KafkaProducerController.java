package com.estudios.demosafkaspring.controller;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaProducerController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/publish/{topic}")
    public ResponseEntity<String> publish(@PathVariable String topic,
                                          @RequestBody Map<String, Object> payload){
        String key = UUID.randomUUID().toString();
        ProducerRecord<String, Object> order = new ProducerRecord<>(topic, payload);
        kafkaTemplate.send(order);
        return ResponseEntity.ok("Message sent to topic " + topic + "key: " + key);
    }

}
