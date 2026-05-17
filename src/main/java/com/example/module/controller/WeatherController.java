package com.example.module.controller;

import com.example.module.dto.ApiResponse;
import com.example.module.dto.WeatherRequest;
import com.example.module.dto.WeatherResponse;
import com.example.module.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * WeatherController - REST API Controller for Weather Integration
 *
 * This controller exposes endpoints for fetching weather data from external API.
 *
 * Key Concepts:
 *
 * 1. @RestController
 *    - Combination of @Controller + @ResponseBody
 *    - All methods automatically serialize return values to JSON
 *    - Directly returns data instead of view names
 *
 * 2. @RequestMapping("/weather")
 *    - Base URL path for all endpoints in this controller
 *    - All endpoints start with /weather
 *
 * 3. @Slf4j
 *    - Lombok annotation that auto-generates logger field
 *    - No need to write: private static final Logger log = LoggerFactory.getLogger(...)
 *    - Just use: log.info(), log.debug(), log.error()
 *
 * 4. Dependency Injection
 *    - @Autowired injects WeatherService
 *    - Service handles business logic
 *    - Controller handles HTTP layer only
 *
 * API Endpoints:
 * 1. GET  /weather/get?city=London      - Fetch weather using HTTP GET
 * 2. POST /weather/post                 - Fetch weather using HTTP POST
 *
 * Example Usage:
 * curl -X GET "http://localhost:8080/weather/get?city=London"
 * curl -X POST "http://localhost:8080/weather/post" \
 *      -H "Content-Type: application/json" \
 *      -d '{"city":"London"}'
 */
@RestController
@RequestMapping("/weather")
@Slf4j
public class WeatherController {

    /**
     * WeatherService - Injected service layer
     * Contains all business logic for weather API calls
     */
    @Autowired
    private WeatherService weatherService;

    /**
     * GET Endpoint - Fetch weather using HTTP GET method
     *
     * URL: GET /weather/get?city=London
     * Query Parameter: city (required)
     *
     * Why GET?
     * - Standard HTTP method for retrieving data
     * - City parameter in query string (?city=London)
     * - Stateless and safe
     * - Cacheable by browser/proxies
     *
     * Response Format:
     * {
     *   "success": true,
     *   "message": "Weather data retrieved successfully",
     *   "data": {
     *     "tempC": 15.2,
     *     "humidity": 72,
     *     "condition": "Partly cloudy",
     *     "windSpeedKmph": 12.5
     *   },
     *   "statusCode": 200,
     *   "timestamp": "2026-05-17T14:22:10"
     * }
     *
     * @param city City name (e.g., "London", "New York", "Tokyo")
     * @return ResponseEntity with ApiResponse containing WeatherResponse
     */
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<WeatherResponse>> getWeather(
            @RequestParam(value = "city", required = true) String city) {

        log.info("GET /weather/get endpoint called with city: {}", city);

        // Validate city parameter
        if (city == null || city.trim().isEmpty()) {
            log.warn("Invalid city parameter: empty or null");
            ApiResponse<WeatherResponse> errorResponse = ApiResponse.<WeatherResponse>builder()
                    .success(false)
                    .message("City parameter is required")
                    .statusCode(400)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Call service to fetch weather
        ApiResponse<WeatherResponse> response = weatherService.getWeatherByCity(city);

        // Return appropriate HTTP status based on response
        if (response.getSuccess()) {
            log.info("Successfully retrieved weather for city: {}", city);
            return ResponseEntity.ok(response);
        } else {
            log.error("Failed to retrieve weather for city: {}", city);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
    }

    /**
     * POST Endpoint - Fetch weather using HTTP POST method
     *
     * URL: POST /weather/post
     * Request Body:
     * {
     *   "city": "London",
     *   "endpoint": "weather"
     * }
     *
     * Why POST?
     * - Used when sending data to the server
     * - Demonstrates POST method usage with request body
     * - Supports more complex data structures
     * - Data not visible in URL (slightly more secure)
     *
     * When to use POST in real APIs:
     * - Creating resources
     * - Form submissions
     * - Sensitive data (passwords, tokens)
     * - Complex query parameters (too long for URL)
     *
     * Response Format: (Same as GET endpoint)
     * {
     *   "success": true,
     *   "message": "Weather data retrieved via POST successfully",
     *   "data": { ... },
     *   "statusCode": 200,
     *   "timestamp": "2026-05-17T14:22:10"
     * }
     *
     * @param weatherRequest Request body containing city information
     * @return ResponseEntity with ApiResponse containing WeatherResponse
     */
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<WeatherResponse>> getWeatherByPost(
            @RequestBody WeatherRequest weatherRequest) {

        log.info("POST /weather/post endpoint called with city: {}", weatherRequest.getCity());

        // Validate request
        if (weatherRequest.getCity() == null || weatherRequest.getCity().trim().isEmpty()) {
            log.warn("Invalid request: city name is empty or null");
            ApiResponse<WeatherResponse> errorResponse = ApiResponse.<WeatherResponse>builder()
                    .success(false)
                    .message("City name is required in request body")
                    .statusCode(400)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Call service to fetch weather
        ApiResponse<WeatherResponse> response = weatherService.getWeatherByPost(weatherRequest);

        // Return appropriate HTTP status based on response
        if (response.getSuccess()) {
            log.info("Successfully retrieved weather via POST for city: {}", weatherRequest.getCity());
            return ResponseEntity.ok(response);
        } else {
            log.error("Failed to retrieve weather via POST for city: {}", weatherRequest.getCity());
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
    }

    /**
     * Health Check Endpoint
     *
     * URL: GET /weather/health
     *
     * Simple endpoint to verify the weather controller is running
     * Useful for monitoring and debugging
     *
     * @return Simple success message
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        log.debug("Weather API health check endpoint called");
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Weather API is running")
                .data("OK")
                .statusCode(200)
                .build();
        return ResponseEntity.ok(response);
    }
}

