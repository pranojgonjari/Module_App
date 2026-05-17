package com.example.module.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

/**
 * AppConfig - Application Configuration for RestTemplate and other Beans
 *
 * This configuration class defines application-wide beans that can be injected
 * across the application.
 *
 * Why separate config classes?
 * - SecurityConfig handles Spring Security configuration
 * - AppConfig handles general application beans (RestTemplate, etc.)
 * - Follows Single Responsibility Principle
 * - Easier to maintain and test
 *
 * What is a Bean?
 * - An object managed by Spring Framework
 * - Created once (singleton) and injected wherever needed
 * - Lifecycle managed by Spring (creation, initialization, destruction)
 * - Can be configured with properties
 *
 * Why RestTemplate as a Bean?
 * 1. Configurable - can set timeouts, connection pools, etc. in one place
 * 2. Reusable - inject into any service/controller
 * 3. Testable - can mock the bean in unit tests
 * 4. Manages resources - Spring handles cleanup
 * 5. Singleton - only one instance for entire application (thread-safe)
 */
@Configuration
public class AppConfig {

    /**
     * RestTemplate Bean Definition
     *
     * What is RestTemplate?
     * - Spring's synchronous HTTP client
     * - Used to make REST API calls to external services
     * - Handles:
     *   - HTTP connections
     *   - Request/response serialization
     *   - Error handling
     *   - Connection pooling
     *   - SSL/TLS certificates
     *
     * Why use RestTemplateBuilder?
     * - Provides fluent API for configuring RestTemplate
     * - Automatically handles connection pooling
     * - Allows setting timeouts, interceptors, etc.
     * - Spring Boot automatically configures it
     *
     * Alternatives to RestTemplate:
     * 1. WebClient (preferred in Spring Boot 5.3+)
     *    - Reactive/non-blocking
     *    - Better for high-concurrency scenarios
     *    - More modern approach
     *
     * 2. RestTemplate (used here)
     *    - Blocking/synchronous
     *    - Simpler to use and understand
     *    - Good for most traditional applications
     *    - Still widely used
     *
     * 3. OkHttp
     *    - Third-party HTTP client
     *    - Lightweight and popular
     *
     * 4. Apache HttpClient
     *    - Powerful but complex
     *    - Good for advanced use cases
     *
     * Configuration in this example:
     * - Connection Timeout: 5 seconds
     *   (How long to wait for server to accept connection)
     * - Read Timeout: 10 seconds
     *   (How long to wait for server to respond)
     * - Buffer Size: 4096 bytes
     *   (For large responses)
     *
     * @param builder Spring Boot auto-configured RestTemplateBuilder
     * @return Configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // Connection Timeout: 5 seconds
                // If server doesn't accept connection in 5 seconds, fail with timeout error
                .setConnectTimeout(Duration.ofSeconds(5))

                // Read Timeout: 10 seconds
                // If server doesn't respond within 10 seconds, fail with timeout error
                .setReadTimeout(Duration.ofSeconds(10))

                // Build and return the configured RestTemplate
                .build();
    }

    /**
     * Alternative RestTemplate Configuration (More Advanced)
     *
     * If you need more control, you can use HttpComponentsClientHttpRequestFactory:
     *
     * @Bean
     * public RestTemplate advancedRestTemplate() {
     *     HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
     *
     *     // Connection timeout
     *     factory.setConnectTimeout(5000);
     *
     *     // Read timeout
     *     factory.setReadTimeout(10000);
     *
     *     // Buffer size
     *     factory.setBufferRequestBody(true);
     *
     *     return new RestTemplate(factory);
     * }
     *
     * Benefits of advanced config:
     * - External HTTP client configuration
     * - Connection pooling settings
     * - SSL certificate handling
     * - More granular control
     *
     * For this example, RestTemplateBuilder approach is sufficient
     * and recommended by Spring Boot.
     */
}

