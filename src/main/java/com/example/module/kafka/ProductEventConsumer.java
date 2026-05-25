package com.example.module.kafka;



import com.example.module.dto.KafkaEventDto;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductEventConsumer {

    @KafkaListener(
            topics = "product-events-topic",
            groupId = "product-group"
    )
    public void consume(KafkaEventDto event) {

        log.info("Received Kafka Event => {}", event);
    }
}