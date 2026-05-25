package com.example.module.service;



import com.example.module.dto.KafkaEventDto;
import com.example.module.entity.Product;
import com.example.module.kafka.ProductEventProducer;
import com.example.module.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductEventProducer producer;

    public Product create(Product product) {

        Product saved = repository.save(product);

        KafkaEventDto event = KafkaEventDto.builder()
                .eventType("CREATE")
                .moduleName("PRODUCT")
                .entityId(saved.getId())
                .data(saved)
                .timestamp(LocalDateTime.now())
                .build();

        producer.publishEvent(event);

        return saved;
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public Product getById(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product Not Found"));
    }

    public Product update(String id, Product product) {

        Product existing = getById(id);

        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());

        Product updated = repository.save(existing);

        KafkaEventDto event = KafkaEventDto.builder()
                .eventType("UPDATE")
                .moduleName("PRODUCT")
                .entityId(updated.getId())
                .data(updated)
                .timestamp(LocalDateTime.now())
                .build();

        producer.publishEvent(event);

        return updated;
    }

    public void delete(String id) {

        Product existing = getById(id);

        repository.deleteById(id);

        KafkaEventDto event = KafkaEventDto.builder()
                .eventType("DELETE")
                .moduleName("PRODUCT")
                .entityId(existing.getId())
                .data(existing)
                .timestamp(LocalDateTime.now())
                .build();

        producer.publishEvent(event);
    }
}