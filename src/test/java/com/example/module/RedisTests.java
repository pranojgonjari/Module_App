package com.example.module;


import com.example.module.service.RedisService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisTests {

    @Autowired
    private RedisService redisService;

    @Test
    void testRedisSetAndGet() {

        // Save value
        redisService.setValue("name", "Pranoj");

        // Get value
        String result = redisService.getValue("name");

        // Validate
        Assertions.assertEquals("Pranoj", result);
    }
}