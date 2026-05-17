# 🌤️ Spring Boot External API Integration - Complete Guide

## Overview

This guide demonstrates how to integrate external REST APIs into your Spring Boot application using `RestTemplate`. We've implemented a weather API integration that fetches weather data from **api-ninjas.com**.

---

## 📋 Quick Summary

| Component | Details |
|-----------|---------|
| **External API** | https://api.api-ninjas.com/v1/weather |
| **API Key** | GfuKjCTXvstEGHRZzBsSKrcLBpTWo4M0dS1BFjG6 |
| **HTTP Methods** | GET and POST |
| **GET Endpoint** | `/weather/get?city=London` |
| **POST Endpoint** | `/weather/post` |
| **Response Format** | JSON wrapped in ApiResponse DTO |

---

## 🏗️ Architecture & Files Created

### 1. **DTOs (Data Transfer Objects)** - `dto/` folder
These classes represent request/response data structures:

```
WeatherRequest.java
├── city: String        (City name for which we want weather)
└── endpoint: String    (Optional: API endpoint)

WeatherResponse.java
├── tempC: Double       (Temperature in Celsius)
├── tempF: Double       (Temperature in Fahrenheit)
├── feelsLikeC: Double  (Feels like temperature)
├── humidity: Integer   (Humidity percentage)
├── condition: String   (Weather condition)
├── windSpeedKmph: Double
└── timezone: String

ApiResponse<T>
├── success: Boolean    (Operation status)
├── message: String     (Human-readable message)
├── data: T             (Generic response data)
├── statusCode: Integer (HTTP status code)
├── timestamp: String   (Response timestamp)
└── errorDetails: String
```

### 2. **Service Layer** - `service/WeatherService.java`
Contains business logic for API calls:

```
WeatherService
├── getWeatherByCity(String city)
│   └── Uses HTTP GET method
│   └── City in query parameter (?city=London)
│   └── Returns ApiResponse<WeatherResponse>
└── getWeatherByPost(WeatherRequest request)
    └── Uses HTTP POST method
    └── Request passed as JSON body
    └── Returns ApiResponse<WeatherResponse>
```

### 3. **Controller** - `controller/WeatherController.java`
REST API endpoints:

```
WeatherController
├── GET /weather/get?city=London
│   └── Query parameter validation
│   └── Calls service.getWeatherByCity()
│   └── Returns ResponseEntity with status
└── POST /weather/post
    ├── Request body validation
    ├── Calls service.getWeatherByPost()
    └── Returns ResponseEntity with status
```

### 4. **Configuration** - `config/AppConfig.java`
Spring Bean configuration:

```
AppConfig
└── RestTemplate Bean
    ├── Connection Timeout: 5 seconds
    ├── Read Timeout: 10 seconds
    └── Configured via RestTemplateBuilder
```

### 5. **Application Properties** - `application.yml`
External API configuration:

```yaml
weather:
  api:
    base-url: https://api.api-ninjas.com/v1/weather
    key: GfuKjCTXvstEGHRZzBsSKrcLBpTWo4M0dS1BFjG6
```

---

## 🔑 Key Concepts Explained

### 1. **RestTemplate**
Spring's HTTP client for consuming REST APIs

**Why RestTemplate?**
- Simple and straightforward
- Handles connection pooling automatically
- Manages serialization/deserialization
- Integrated with Spring

**Alternative: WebClient**
- More modern (Spring 5.3+)
- Reactive/non-blocking
- Better for high-concurrency scenarios
- Preferred for new projects

### 2. **HttpHeaders**
Used to set HTTP request headers

```java
HttpHeaders headers = new HttpHeaders();
headers.set("X-Api-Key", apiKey);           // Authentication
headers.set("Accept", "application/json");  // Response format
headers.set("Content-Type", "application/json"); // Request format
```

**Common Headers:**
- `Authorization`: Bearer token or API key
- `Content-Type`: application/json, text/html, etc.
- `Accept`: Expected response format
- `User-Agent`: Client identification
- `X-Api-Key`: Custom API key header

### 3. **HttpEntity**
Wraps HTTP request body and headers together

```java
// For GET (usually only headers, no body)
HttpEntity<String> entity = new HttpEntity<>(headers);

// For POST (headers + request body)
HttpEntity<WeatherRequest> entity = new HttpEntity<>(weatherRequest, headers);
```

### 4. **exchange() Method**
Flexible method that works with any HTTP method

```java
ResponseEntity<WeatherResponse> response = restTemplate.exchange(
    url,                     // URL to call
    HttpMethod.GET,          // HTTP method
    entity,                  // Request entity (headers + body)
    WeatherResponse.class    // Response type to deserialize
);
```

**Why exchange()?**
- Works with all HTTP methods (GET, POST, PUT, DELETE, etc.)
- More flexible than getForEntity() or postForEntity()
- Access to full ResponseEntity including status, headers, body

**Alternatives:**
```java
// GET only
restTemplate.getForEntity(url, WeatherResponse.class);

// POST only
restTemplate.postForEntity(url, request, WeatherResponse.class);
```

---

## 📡 HTTP Methods Explained

### GET Request
```
Purpose: Retrieve data
Parameters: In query string (?city=London)
Body: None
Characteristics:
  - Safe (doesn't modify server)
  - Idempotent (same result multiple times)
  - Cacheable
  - Visible in URL

Flow:
1. Client sends: GET /v1/weather?city=London HTTP/1.1
2. Server processes query parameter
3. Server returns: 200 OK + JSON response
4. Client deserializes to WeatherResponse
```

**Code Example:**
```java
HttpHeaders headers = new HttpHeaders();
headers.set("X-Api-Key", apiKey);

String url = "https://api.api-ninjas.com/v1/weather?city=London";
HttpEntity<String> entity = new HttpEntity<>(headers);

ResponseEntity<WeatherResponse> response = restTemplate.exchange(
    url,
    HttpMethod.GET,
    entity,
    WeatherResponse.class
);
```

### POST Request
```
Purpose: Send data to server (create, update, etc.)
Parameters: In request body (JSON)
Body: Request payload
Characteristics:
  - Modifies server state
  - Not idempotent
  - Not cacheable by default
  - Not visible in URL (more secure for sensitive data)

Flow:
1. Client sends: POST /v1/weather HTTP/1.1
                 Content-Type: application/json
                 X-Api-Key: ...
                 
                 {"city": "London"}
2. Server processes request body
3. Server returns: 200 OK + JSON response
4. Client deserializes to WeatherResponse
```

**Code Example:**
```java
WeatherRequest request = new WeatherRequest("London", "weather");
HttpHeaders headers = new HttpHeaders();
headers.set("X-Api-Key", apiKey);
headers.set("Content-Type", "application/json");

String url = "https://api.api-ninjas.com/v1/weather";
HttpEntity<WeatherRequest> entity = new HttpEntity<>(request, headers);

ResponseEntity<WeatherResponse> response = restTemplate.exchange(
    url,
    HttpMethod.POST,
    entity,
    WeatherResponse.class
);
```

---

## 🧪 Testing the API

### 1. **Test GET Endpoint**

Using cURL:
```bash
curl -X GET "http://localhost:8080/weather/get?city=London" \
  -H "Content-Type: application/json"
```

Using Postman:
1. Create new GET request
2. URL: `http://localhost:8080/weather/get?city=London`
3. Click Send

Response:
```json
{
  "success": true,
  "message": "Weather data retrieved successfully",
  "data": {
    "tempC": 15.2,
    "tempF": 59.4,
    "humidity": 72,
    "condition": "Partly cloudy",
    "windSpeedKmph": 12.5,
    "city": "London",
    "timezone": "Europe/London"
  },
  "statusCode": 200,
  "timestamp": "2026-05-17T14:22:10"
}
```

### 2. **Test POST Endpoint**

Using cURL:
```bash
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{"city":"London","endpoint":"weather"}'
```

Using Postman:
1. Create new POST request
2. URL: `http://localhost:8080/weather/post`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "city": "London",
  "endpoint": "weather"
}
```
5. Click Send

Response: (Same format as GET)

---

## 🛡️ Error Handling

### Error Scenarios

#### 1. Invalid City Name
```json
{
  "success": false,
  "message": "City name is required in request body",
  "statusCode": 400,
  "timestamp": "2026-05-17T14:22:10"
}
```

#### 2. API Key Invalid
```json
{
  "success": false,
  "message": "API call failed: 401 Unauthorized",
  "statusCode": 401,
  "errorDetails": "Invalid API key",
  "timestamp": "2026-05-17T14:22:10"
}
```

#### 3. Connection Timeout
```json
{
  "success": false,
  "message": "API call failed: java.net.SocketTimeoutException",
  "statusCode": 500,
  "timestamp": "2026-05-17T14:22:10"
}
```

### Try-Catch Strategy
```java
try {
    // Make API call
    ResponseEntity<WeatherResponse> response = restTemplate.exchange(...);
} catch (RestClientException e) {
    // Handle API errors (4xx, 5xx, connection issues)
    log.error("API error", e);
    return buildErrorResponse("API call failed: " + e.getMessage(), 500);
} catch (Exception e) {
    // Handle unexpected errors
    log.error("Unexpected error", e);
    return buildErrorResponse("An unexpected error occurred", 500);
}
```

---

## 🔒 Security Best Practices

### 1. **Protect API Keys**
❌ **BAD:**
```yaml
weather:
  api:
    key: GfuKjCTXvstEGHRZzBsSKrcLBpTWo4M0dS1BFjG6  # Hardcoded in file
```

✅ **GOOD:**
```yaml
weather:
  api:
    key: ${WEATHER_API_KEY}  # Read from environment variable
```

Set environment variable:
```bash
export WEATHER_API_KEY=GfuKjCTXvstEGHRZzBsSKrcLBpTWo4M0dS1BFjG6
```

### 2. **Timeout Configuration**
```java
.setConnectTimeout(Duration.ofSeconds(5))  // Prevent hanging
.setReadTimeout(Duration.ofSeconds(10))   // Prevent hanging
```

### 3. **Input Validation**
```java
if (city == null || city.trim().isEmpty()) {
    return buildErrorResponse("City name is required", 400);
}
```

### 4. **Rate Limiting**
Consider implementing rate limiting to prevent abuse:
```java
@RateLimiter(limit = 100, duration = "1m")  // 100 requests per minute
public ResponseEntity<ApiResponse<WeatherResponse>> getWeather() { ... }
```

### 5. **HTTPS Enforcement**
Ensure API calls use HTTPS for data security
```java
String url = "https://api.api-ninjas.com/v1/weather";  // HTTPS, not HTTP
```

---

## 📊 Logging

All components use `@Slf4j` from Lombok for logging:

```java
log.info("Key information");     // Normal operations
log.debug("Debug information");   // Development debugging
log.warn("Warning message");      // Potential issues
log.error("Error occurred", e);   // Errors with exception
```

**Logs generated:**
```
INFO WeatherService - Fetching weather data for city: London
DEBUG WeatherService - API URL: https://api.api-ninjas.com/v1/weather?city=London
INFO WeatherService - Weather data retrieved successfully for city: London
```

Check logs in: `logs/module.log`

---

## 🚀 Performance Considerations

### 1. **Connection Pooling**
RestTemplate automatically uses connection pooling:
- Reuses TCP connections
- Reduces latency
- Configured in AppConfig

### 2. **Caching (Optional)**
```java
@Cacheable(value = "weather", key = "#city")
public WeatherResponse getWeatherByCity(String city) { ... }
```

### 3. **Async Calls (Optional)**
For better performance with many requests:
```java
@Async
public CompletableFuture<WeatherResponse> getWeatherAsync(String city) { ... }
```

### 4. **Circuit Breaker Pattern (Using Resilience4j)**
```java
@CircuitBreaker(name = "weather-api", fallbackMethod = "fallback")
public WeatherResponse getWeather(String city) { ... }
```

---

## 📚 Code Structure Summary

```
src/main/java/com/example/module/
├── config/
│   └── AppConfig.java               (RestTemplate configuration)
├── controller/
│   └── WeatherController.java       (REST endpoints)
├── service/
│   └── WeatherService.java          (Business logic)
└── dto/
    ├── WeatherRequest.java          (Request DTO)
    ├── WeatherResponse.java         (Response DTO)
    └── ApiResponse.java             (Wrapper DTO)

src/main/resources/
└── application.yml                  (Configuration)
```

---

## 🎯 What You Learned

✅ How to use RestTemplate for external API calls
✅ Difference between GET and POST methods
✅ How to set HTTP headers for authentication
✅ How to use HttpEntity and exchange() method
✅ DTO best practices for API integration
✅ Error handling for API calls
✅ Logging with @Slf4j
✅ Security best practices for API keys
✅ Spring Boot configuration for external APIs

---

## 🔗 Useful References

- [Spring RestTemplate Documentation](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)
- [HTTP Methods (RFC 7231)](https://tools.ietf.org/html/rfc7231)
- [api-ninjas.com Weather API](https://api-ninjas.com/)
- [Spring Boot WebClient (Modern Alternative)](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html)

---

## ✨ Next Steps

1. **Test the endpoints** using provided cURL or Postman examples
2. **Add caching** for better performance
3. **Implement circuit breaker** for resilience
4. **Add unit tests** for WeatherService
5. **Move API key to environment variables** for security
6. **Consider WebClient** for reactive/async calls
7. **Add rate limiting** to prevent API abuse

Happy coding! 🎉

