package com.example.module.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WeatherResponse DTO - Data Transfer Object for Weather API Response
 *
 * This class represents the response received from the external weather API.
 * It maps directly to the JSON response from api-ninjas.com weather endpoint.
 *
 * Why use @JsonIgnoreProperties(ignoreUnknown = true)?
 * - The API may return additional fields we don't need
 * - Makes the code more resilient to API changes
 * - Only maps the fields we care about
 *
 * Why use @JsonProperty?
 * - Maps JSON field names (with underscores) to Java camelCase field names
 * - Example: "temp_c" in JSON → "tempC" in Java
 * - Makes our Java code more idiomatic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    /**
     * Temperature in Celsius
     * Example: 15.2
     */
    @JsonProperty("temp_c")
    private Double tempC;

    /**
     * Temperature in Fahrenheit
     * Example: 59.4
     */
    @JsonProperty("temp_f")
    private Double tempF;

    /**
     * Feels like temperature in Celsius
     * Example: 12.5
     */
    @JsonProperty("feels_like_c")
    private Double feelsLikeC;

    /**
     * Feels like temperature in Fahrenheit
     * Example: 54.5
     */
    @JsonProperty("feels_like_f")
    private Double feelsLikeF;

    /**
     * Humidity percentage (0-100)
     * Example: 72
     */
    private Integer humidity;

    /**
     * Weather condition description
     * Example: "Partly cloudy", "Sunny", "Rainy"
     */
    private String condition;

    /**
     * Wind speed in kilometers per hour
     * Example: 12.5
     */
    @JsonProperty("wind_speed_kmph")
    private Double windSpeedKmph;

    /**
     * Wind speed in miles per hour
     * Example: 7.8
     */
    @JsonProperty("wind_speed_mph")
    private Double windSpeedMph;

    /**
     * City name
     * Example: "London"
     */
    private String city;

    /**
     * Timezone information
     * Example: "Europe/London"
     */
    private String timezone;
}

