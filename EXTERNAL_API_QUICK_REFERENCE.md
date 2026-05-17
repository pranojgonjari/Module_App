# 🚀 External API Integration - Quick Reference

## Files Created Summary

| File | Purpose | Key Components |
|------|---------|-----------------|
| `dto/WeatherRequest.java` | Request DTO | city, endpoint |
| `dto/WeatherResponse.java` | Response DTO | temp, humidity, condition, windSpeed |
| `dto/ApiResponse.java` | Response Wrapper | success, message, data, statusCode, timestamp |
| `service/WeatherService.java` | Business Logic | getWeatherByCity(), getWeatherByPost() |
| `controller/WeatherController.java` | REST Endpoints | GET /weather/get, POST /weather/post |
| `config/AppConfig.java` | Spring Configuration | RestTemplate Bean |
| `application.yml` | Application Config | weather.api.base-url, weather.api.key |

---

## 🎯 Core Components Explained

### 1. RestTemplate Bean Creation
```java
@Bean
public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .setConnectTimeout(Duration.ofSeconds(5))
        .setReadTimeout(Duration.ofSeconds(10))
        .build();
}
```

### 2. Injecting RestTemplate
```java
@Service
public class WeatherService {
    @Autowired
    private RestTemplate restTemplate;
}
```

### 3. Setting Headers
```java
HttpHeaders headers = new HttpHeaders();
headers.set("X-Api-Key", apiKey);
headers.set("Accept", "application/json");
```

### 4. Creating HTTP Entity
```java
// For GET (headers only)
HttpEntity<String> entity = new HttpEntity<>(headers);

// For POST (headers + body)
HttpEntity<WeatherRequest> entity = 
    new HttpEntity<>(weatherRequest, headers);
```

### 5. Making Exchange Request
```java
ResponseEntity<WeatherResponse> response = restTemplate.exchange(
    url,                        // URL
    HttpMethod.GET,            // Method
    entity,                    // Headers + body
    WeatherResponse.class      // Response type
);
```

---

## 📡 API Endpoints Cheat Sheet

### GET Request
```bash
curl -X GET "http://localhost:8080/weather/get?city=London"
```

**Inside Code:**
```java
String url = "https://api.api-ninjas.com/v1/weather?city=" + city;
HttpEntity<String> entity = new HttpEntity<>(headers);
restTemplate.exchange(url, HttpMethod.GET, entity, WeatherResponse.class);
```

### POST Request
```bash
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{"city":"London"}'
```

**Inside Code:**
```java
String url = "https://api.api-ninjas.com/v1/weather?city=" + city;
HttpEntity<WeatherRequest> entity = 
    new HttpEntity<>(weatherRequest, headers);
restTemplate.exchange(url, HttpMethod.POST, entity, WeatherResponse.class);
```

---

## 🔐 Configuration Properties

### In `application.yml`:
```yaml
weather:
  api:
    base-url: https://api.api-ninjas.com/v1/weather
    key: GfuKjCTXvstEGHRZzBsSKrcLBpTWo4M0dS1BFjG6
```

### In Code:
```java
@Value("${weather.api.base-url}")
private String apiBaseUrl;

@Value("${weather.api.key}")
private String apiKey;
```

### In Environment (Production):
```bash
export WEATHER_API_KEY=your_actual_key_here
```

Then in `application.yml`:
```yaml
weather:
  api:
    key: ${WEATHER_API_KEY}
```

---

## ✅ Implementation Checklist

- [x] Create DTO classes (WeatherRequest, WeatherResponse, ApiResponse)
- [x] Create WeatherService with GET and POST methods
- [x] Create WeatherController with endpoints
- [x] Configure RestTemplate bean in AppConfig
- [x] Add API configuration to application.yml
- [x] Add @Slf4j logging throughout
- [x] Handle errors gracefully
- [x] Document with comments
- [x] Create comprehensive guides
- [x] Provide test examples

---

## 🧪 Quick Test Commands

### Start Application
```bash
mvn clean spring-boot:run
```

### Test Health
```bash
curl http://localhost:8080/weather/health
```

### Test GET
```bash
curl http://localhost:8080/weather/get?city=London
```

### Test POST
```bash
curl -X POST http://localhost:8080/weather/post \
  -H "Content-Type: application/json" \
  -d '{"city":"London"}'
```

### View Logs
```bash
# Real-time in console (during mvn spring-boot:run)
# Historical in file:
tail -f logs/module.log
```

---

## 📊 HTTP Methods Comparison

| Aspect | GET | POST |
|--------|-----|------|
| Purpose | Retrieve data | Send/create data |
| Parameters | Query string (?city=X) | Request body |
| Visibility | Visible in URL | Hidden in body |
| Caching | Yes | No |
| Idempotent | Yes | No |
| Data size | Limited | Large |
| Security | Lower | Higher |

---

## 🛠️ Common Issues & Fixes

| Issue | Cause | Fix |
|-------|-------|-----|
| "Connection refused" | App not running | `mvn spring-boot:run` |
| "Cannot find symbol: RestTemplate" | Missing dependency | Check pom.xml has spring-web |
| "Invalid API Key" | Wrong key | Update application.yml |
| "Timeout" | API not responding | Check internet, increase timeout |
| "JSON parse error" | Invalid response | Check API response format |
| "404 Not Found" | Wrong endpoint | Verify URL path |

---

## 📚 Code Snippets Reference

### Service Layer Example
```java
@Service
@Slf4j
public class WeatherService {
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${weather.api.base-url}")
    private String apiBaseUrl;
    
    public ApiResponse<WeatherResponse> getWeatherByCity(String city) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiKey);
        
        String url = apiBaseUrl + "?city=" + city;
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<WeatherResponse> response = 
                restTemplate.exchange(url, HttpMethod.GET, 
                                    entity, WeatherResponse.class);
            return ApiResponse.<WeatherResponse>builder()
                .success(true)
                .data(response.getBody())
                .statusCode(200)
                .build();
        } catch (Exception e) {
            log.error("API error", e);
            return ApiResponse.<WeatherResponse>builder()
                .success(false)
                .message("API call failed")
                .statusCode(500)
                .build();
        }
    }
}
```

### Controller Layer Example
```java
@RestController
@RequestMapping("/weather")
@Slf4j
public class WeatherController {
    @Autowired
    private WeatherService weatherService;
    
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<WeatherResponse>> getWeather(
            @RequestParam String city) {
        log.info("GET /weather/get called for city: {}", city);
        
        ApiResponse<WeatherResponse> response = 
            weatherService.getWeatherByCity(city);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity
                .status(response.getStatusCode())
                .body(response);
        }
    }
}
```

---

## 🎓 Learning Path

1. **Understand REST APIs**
   - HTTP methods (GET, POST, PUT, DELETE)
   - Request/response structure
   - Status codes

2. **Learn RestTemplate**
   - Creating beans
   - Dependency injection
   - Making requests

3. **Master External API Integration**
   - Headers and authentication
   - Error handling
   - Logging

4. **Implement Best Practices**
   - DTOs for clean code
   - Service layer pattern
   - Configuration externalization

5. **Advanced Topics**
   - Caching responses
   - Rate limiting
   - Circuit breaker pattern
   - WebClient (reactive)

---

## 🔗 External Resources

- [Spring RestTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)
- [HTTP Methods](https://tools.ietf.org/html/rfc7231)
- [api-ninjas](https://api-ninjas.com)
- [Lombok @Slf4j](https://projectlombok.org/features/log)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

---

## 💡 Pro Tips

1. **Always use HTTPS** for external APIs
2. **Never hardcode API keys** - use environment variables
3. **Set appropriate timeouts** - default 5s connect, 10s read
4. **Log all API calls** - helpful for debugging
5. **Validate input** - before sending to API
6. **Handle errors gracefully** - provide meaningful messages
7. **Cache responses** - if API has rate limits
8. **Use circuit breaker** - for resilience
9. **Monitor API usage** - track costs
10. **Document API contracts** - for maintainability

---

## 📝 Next Steps

After implementing this example:

1. **Deploy to production** with environment variables for API key
2. **Add unit tests** for WeatherService
3. **Add integration tests** for WeatherController
4. **Implement caching** for better performance
5. **Add rate limiting** to prevent abuse
6. **Monitor API usage** and costs
7. **Implement retry logic** with exponential backoff
8. **Add circuit breaker** for resilience
9. **Consider WebClient** for reactive/async calls
10. **Optimize error handling** with custom exceptions

---

Need help? Check these files:
- `EXTERNAL_API_INTEGRATION_GUIDE.md` - Detailed explanation
- `EXTERNAL_API_TESTING_EXAMPLES.md` - Test examples and requests
- Code comments - Inline documentation

Happy Coding! 🚀

