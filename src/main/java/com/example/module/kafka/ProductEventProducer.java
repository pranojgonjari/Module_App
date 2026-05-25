package com.example.module.kafka;


import com.example.module.dto.KafkaEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {

    private final KafkaTemplate<String, KafkaEventDto> kafkaTemplate;

    private static final String TOPIC = "product-events-topic";

    public void publishEvent(KafkaEventDto event) {

        log.info("Publishing Event => {}", event);

        kafkaTemplate.send(TOPIC, event);
    }
}