package com.example.module.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WeatherRequest DTO - Data Transfer Object for Weather API Requests
 *
 * This class represents the request payload when calling external weather APIs.
 * It encapsulates the city parameter and any other metadata needed for the request.
 *
 * Using DTOs is a best practice because:
 * 1. Separates API contract from internal domain models
 * 2. Makes validation easier
 * 3. Allows for independent versioning of APIs
 * 4. Provides clear documentation of expected input
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRequest {

    /**
     * City name for which we want to fetch weather information.
     *
     * Example: "London", "New York", "Tokyo"
     */
    private String city;

    /**
     * Optional: API endpoint to call (in case we have multiple endpoints)
     *
     * Default: "weather"
     */
    private String endpoint;
}

