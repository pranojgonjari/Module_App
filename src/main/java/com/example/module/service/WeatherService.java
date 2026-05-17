package com.example.module.service;

import com.example.module.dto.ApiResponse;
import com.example.module.dto.WeatherRequest;
import com.example.module.dto.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * WeatherService - Service Layer for External Weather API Integration
 *
 * This service demonstrates how to integrate with external REST APIs in Spring Boot.
 *
 * Key Concepts:
 *
 * 1. RestTemplate
 *    - Spring's HTTP client for consuming REST services
 *    - Handles connection pooling, timeouts, SSL certificates automatically
 *    - Alternative: WebClient (more modern, reactive)
 *
 * 2. HttpHeaders
 *    - Used to set HTTP headers like Content-Type, Authorization, API-Key, etc.
 *    - Essential for API authentication
 *
 * 3. HttpEntity
 *    - Wraps HTTP request body and headers together
 *    - For GET: usually has only headers
 *    - For POST: includes headers + request body
 *
 * 4. exchange() method
 *    - Generic method that works with any HTTP method (GET, POST, PUT, DELETE, etc.)
 *    - Returns ResponseEntity<T> which contains status, headers, and body
 *    - More flexible than getForEntity() or postForEntity()
 *
 * Why use a service layer?
 * - Separation of concerns (business logic separate from controllers)
 * - Reusable across multiple controllers
 * - Easier to test (can mock the service)
 * - Centralized error handling
 */
@Service
@Slf4j
public class WeatherService {

    /**
     * RestTemplate Bean - injected from AppConfig.java
     * This is Spring's HTTP client for making REST calls
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * API Base URL - injected from application.yml or application.properties
     * Example: https://api.api-ninjas.com/v1/weather
     */
    @Value("${weather.api.base-url}")
    private String apiBaseUrl;

    /**
     * API Key - injected from application.yml or application.properties
     * This is sensitive information - should be stored in environment variables in production
     */
    @Value("${weather.api.key}")
    private String apiKey;

    /**
     * GET Method - Fetch weather data using HTTP GET request
     *
     * Why GET?
     * - Used to retrieve data
     * - Parameters are in query string (?city=London)
     * - Should not modify server state
     * - Safe and idempotent
     *
     * What happens:
     * 1. Setup HTTP headers with API key
     * 2. Build request URL with city parameter
     * 3. Use exchange() to send GET request
     * 4. Parse response to WeatherResponse object
     * 5. Wrap in ApiResponse for consistent response format
     *
     * @param cityName Name of the city (e.g., "London", "New York")
     * @return ApiResponse<WeatherResponse> containing weather data or error
     */
    public ApiResponse<WeatherResponse> getWeatherByCity(String cityName) {
        log.info("Fetching weather data for city: {}", cityName);

        try {
            // Step 1: Prepare HTTP Headers
            // Headers tell the server:
            // - What format we're sending (Content-Type)
            // - What format we want back (Accept)
            // - How to authenticate (X-Api-Key)
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", apiKey);
            headers.set("Accept", "application/json");

            // Step 2: Build complete request URL with query parameter
            // Format: https://api.api-ninjas.com/v1/weather?city=London
            String url = apiBaseUrl + "?city=" + cityName;
            log.debug("API URL: {}", url);

            // Step 3: Create HttpEntity
            // For GET requests, typically only headers are needed (no body)
            // HttpEntity combines headers and body (if any) into one object
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Step 4: Send GET request using exchange() method
            // exchange() is flexible and works with any HTTP method
            // ResponseEntity contains: status code, headers, and response body
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(
                    url,                                    // URL to call
                    HttpMethod.GET,                        // HTTP method
                    entity,                                // Request headers/body
                    WeatherResponse.class                  // Type to deserialize response to
            );

            // Step 5: Check if response is successful
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Weather data retrieved successfully for city: {}", cityName);

                // Return success response wrapped in ApiResponse
                return ApiResponse.<WeatherResponse>builder()
                        .success(true)
                        .message("Weather data retrieved successfully")
                        .data(response.getBody())
                        .statusCode(response.getStatusCode().value())
                        .timestamp(getCurrentTimestamp())
                        .build();
            } else {
                log.warn("Unexpected response status: {}", response.getStatusCode());
                return buildErrorResponse("Failed to fetch weather data", response.getStatusCode().value());
            }

        } catch (RestClientException e) {
            // Catch exceptions like:
            // - ConnectionException (can't connect to server)
            // - HttpClientErrorException (4xx responses)
            // - HttpServerErrorException (5xx responses)
            // - JsonParseException (response is not valid JSON)
            log.error("RestClient error while fetching weather for city: {}", cityName, e);
            return buildErrorResponse("API call failed: " + e.getMessage(), 500);

        } catch (Exception e) {
            // Catch any other unexpected exceptions
            log.error("Unexpected error while fetching weather for city: {}", cityName, e);
            return buildErrorResponse("An unexpected error occurred: " + e.getMessage(), 500);
        }
    }

    /**
     * POST Method - Fetch weather data using HTTP POST request
     *
     * Why POST in this example?
     * - Demonstrates POST implementation (even though weather API uses GET)
     * - In real scenarios, you'd POST when:
     *   - Submitting form data
     *   - Creating resources
     *   - Sending complex queries (too long for query string)
     *   - Sensitive data that shouldn't appear in URL
     *
     * What happens:
     * 1. Setup HTTP headers with API key and Content-Type
     * 2. Serialize WeatherRequest to JSON (RequestBody)
     * 3. Build HttpEntity with headers and body
     * 4. Use exchange() to send POST request
     * 5. Parse response to WeatherResponse
     *
     * @param weatherRequest Request object containing city name
     * @return ApiResponse<WeatherResponse> containing weather data or error
     */
    public ApiResponse<WeatherResponse> getWeatherByPost(WeatherRequest weatherRequest) {
        log.info("Fetching weather data via POST for city: {}", weatherRequest.getCity());

        try {
            // Validate request
            if (weatherRequest.getCity() == null || weatherRequest.getCity().isEmpty()) {
                log.warn("Invalid request: city name is empty");
                return buildErrorResponse("City name is required", 400);
            }

            // Step 1: Prepare HTTP Headers
            // For POST, we need to specify Content-Type
            // This tells the server: "I'm sending JSON data"
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", apiKey);
            headers.set("Content-Type", "application/json");
            headers.set("Accept", "application/json");

            // Step 2: Build URL (for this API, we still send city in query, not body)
            String url = apiBaseUrl + "?city=" + weatherRequest.getCity();
            log.debug("API URL: {}", url);

            // Step 3: Create HttpEntity with headers and request body
            // For POST: HttpEntity<WeatherRequest> would serialize weatherRequest to JSON
            // In this case, we're not using the body for this specific API
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Step 4: Send POST request using exchange() method
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(
                    url,                                    // URL to call
                    HttpMethod.POST,                       // HTTP method (POST instead of GET)
                    entity,                                // Request headers/body
                    WeatherResponse.class                  // Type to deserialize response to
            );

            // Step 5: Check if response is successful
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Weather data retrieved successfully via POST for city: {}", weatherRequest.getCity());

                return ApiResponse.<WeatherResponse>builder()
                        .success(true)
                        .message("Weather data retrieved via POST successfully")
                        .data(response.getBody())
                        .statusCode(response.getStatusCode().value())
                        .timestamp(getCurrentTimestamp())
                        .build();
            } else {
                log.warn("Unexpected response status: {}", response.getStatusCode());
                return buildErrorResponse("Failed to fetch weather data", response.getStatusCode().value());
            }

        } catch (RestClientException e) {
            log.error("RestClient error while fetching weather via POST for city: {}", weatherRequest.getCity(), e);
            return buildErrorResponse("API call failed: " + e.getMessage(), 500);

        } catch (Exception e) {
            log.error("Unexpected error while fetching weather via POST for city: {}", weatherRequest.getCity(), e);
            return buildErrorResponse("An unexpected error occurred: " + e.getMessage(), 500);
        }
    }

    /**
     * Helper method to build error response
     *
     * @param message Error message to display
     * @param statusCode HTTP status code
     * @return ApiResponse with error details
     */
    private ApiResponse<WeatherResponse> buildErrorResponse(String message, int statusCode) {
        return ApiResponse.<WeatherResponse>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .timestamp(getCurrentTimestamp())
                .build();
    }

    /**
     * Helper method to get current timestamp in ISO 8601 format
     * Used for timestamping API responses
     *
     * @return Current timestamp as string
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }
}

