package com.example.module.config;

import com.example.module.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Spring Security Configuration using SecurityFilterChain (Spring Boot 3 approach).
 *
 * Why use SecurityFilterChain instead of WebSecurityConfigurerAdapter?
 * - WebSecurityConfigurerAdapter is deprecated in Spring Security 6
 * - SecurityFilterChain is the modern approach in Spring Boot 3
 * - More flexible and easier to test
 *
 * What does this class do?
 * 1. Configures HTTP security (authentication, authorization)
 * 2. Enables HTTP Basic Authentication
 * 3. Sets up password encoding using BCrypt
 * 4. Configures stateless session management
 * 5. Defines which URLs are public and which require authentication
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * BCryptPasswordEncoder Bean.
     *
     * What is BCrypt?
     * - One-way hash function with built-in salting
     * - Uses adaptive hashing that gets stronger over time
     * - Much safer than MD5, SHA1, or plaintext passwords
     *
     * Why BCrypt?
     * 1. Slow by design - makes brute force attacks impractical
     * 2. Includes salt - prevents rainbow table attacks
     * 3. Adaptive - can increase strength without changing hashes
     * 4. Industry standard - widely used and trusted
     *
     * How it works:
     * - Encoding: BCrypt takes password + random salt → generates hash
     * - Verification: BCrypt takes password + stored hash → compares
     * - Never stores actual password, only the hash
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DaoAuthenticationProvider Bean.
     *
     * What does DaoAuthenticationProvider do?
     * - Uses UserDetailsService to load user from database
     * - Compares provided password with password in database (using BCrypt)
     * - Returns Authentication object if credentials match
     *
     * Why is this needed?
     * - Spring Security uses this to authenticate users during login
     * - Links our CustomUserDetailsService with password encoding
     *
     * @return DaoAuthenticationProvider instance
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager Bean.
     *
     * What is AuthenticationManager?
     * - Central component that processes Authentication requests
     * - Uses AuthenticationProvider to authenticate users
     * - Returns authenticated user if credentials are valid
     *
     * Why return it as a bean?
     * - Can be injected in other components
     * - Used for programmatic authentication if needed
     *
     * @param http HttpSecurity object
     * @return AuthenticationManager instance
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }

    /**
     * SecurityFilterChain Bean - Configure HTTP Security.
     *
     * This is the heart of Spring Security configuration.
     * It defines:
     * - Which endpoints require authentication
     * - Which endpoints are public
     * - How to authenticate (HTTP Basic, JWT, Form, etc.)
     * - CSRF protection
     * - Session management
     *
     * Spring Boot 3 approach using SecurityFilterChain:
     * - Replaces deprecated WebSecurityConfigurerAdapter
     * - More flexible and compositional
     * - Each bean configures one aspect of security
     *
     * @param http HttpSecurity object to configure
     * @return SecurityFilterChain instance
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/public/**", "/auth/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.sendError(401, "Unauthorized");
                    })
            )
            .logout(logout -> logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessUrl("/public/hello")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            );

        return http.build();
    }
}

