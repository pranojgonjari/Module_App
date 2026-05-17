package com.example.module.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponse DTO - Standardized Response Format
 *
 * This is a wrapper class that provides a consistent response format
 * for all API endpoints in our application.
 *
 * Benefits:
 * 1. Consistent API contract - all endpoints follow same format
 * 2. Easy error handling - errors, success, and data in one structure
 * 3. Frontend knows exactly what to expect
 * 4. Easy to add metadata (timestamps, request IDs, etc.)
 *
 * Example:
 * {
 *   "success": true,
 *   "message": "Weather data retrieved successfully",
 *   "data": { "temp_c": 15, "humidity": 72 },
 *   "timestamp": "2026-05-17T14:22:10Z"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Boolean flag indicating if the operation was successful
     */
    private Boolean success;

    /**
     * Human-readable message describing the result
     *
     * Examples:
     * - "Weather data retrieved successfully"
     * - "Invalid city name provided"
     * - "External API error occurred"
     */
    private String message;

    /**
     * The actual response data (generic type T)
     *
     * Can be any type:
     * - WeatherResponse
     * - User
     * - List<T>
     * - etc.
     */
    private T data;

    /**
     * Timestamp when the response was generated
     * Useful for debugging and logging
     */
    private String timestamp;

    /**
     * HTTP Status code
     * Examples: 200, 400, 401, 404, 500
     */
    private Integer statusCode;

    /**
     * Error details (if any)
     * Useful for debugging API errors
     */
    private String errorDetails;
}

