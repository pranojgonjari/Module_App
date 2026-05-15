package com.example.module.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PublicController - Endpoints that are accessible without authentication.
 *
 * These endpoints are configured in SecurityConfig to permit all users.
 * No authentication is required to access these endpoints.
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    /**
     * Health check endpoint - Publicly accessible.
     * Can be used to verify the application is running.
     *
     * @return Simple health status message
     */
    @GetMapping("/hello")
    public String publicHello() {
        return "Hello! This is a public endpoint - no authentication required.";
    }

    /**
     * Public information endpoint.
     * Returns basic application information.
     *
     * @return Application information
     */
    @GetMapping("/info")
    public String publicInfo() {
        return "Module API - Spring Boot 3 with Spring Security and MongoDB";
    }

    /**
     * Health check - Publicly accessible.
     * Can be used by load balancers to check if service is healthy.
     *
     * @return OK status
     */
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}

