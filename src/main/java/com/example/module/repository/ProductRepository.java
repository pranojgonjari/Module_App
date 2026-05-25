package com.example.module.repository;



import com.example.module.entity.Product;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository
        extends MongoRepository<Product, String> {
}