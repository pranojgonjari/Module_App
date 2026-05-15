package com.example.module.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * SecureController - Endpoints that require authentication.
 *
 * These endpoints are configured in SecurityConfig to require authentication.
 * Basic Authentication credentials must be provided to access these endpoints.
 *
 * How to use:
 * - Send HTTP Basic Auth header with each request
 * - Header format: Authorization: Basic base64(username:password)
 * - Example: Authorization: Basic YWRtaW46YWRtaW4xMjM=
 */
@RestController
@RequestMapping("/api")
public class SecureController {

    /**
     * Secure endpoint - Requires authentication.
     * Only authenticated users can access this.
     *
     * How Spring Security passes the authenticated user?
     * - Authentication object is automatically injected by Spring Security
     * - Contains authenticated user information
     * - Available only for authenticated requests
     *
     * @param authentication Object containing authenticated user info
     * @return Personalized greeting with user details
     */
    @GetMapping("/hello")
    public String secureHello(Authentication authentication) {
        return "Hello " + authentication.getName() + "! This is a secured endpoint.";
    }

    /**
     * Get current user information.
     * Returns detailed information about the authenticated user.
     *
     * @param authentication Object containing authenticated user info
     * @return User information in JSON format
     */
    @GetMapping("/user-info")
    public UserInfoResponse getUserInfo(Authentication authentication) {
        // Get user's roles/authorities
        String roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        return new UserInfoResponse(
                authentication.getName(),
                roles,
                authentication.isAuthenticated()
        );
    }

    /**
     * Admin-only endpoint - Only users with ROLE_ADMIN can access.
     * Even if authenticated, users without ROLE_ADMIN cannot access.
     *
     * Note: To implement role-based access, we would use @PreAuthorize annotation:
     * @PreAuthorize("hasRole('ADMIN')")
     * But for this example, we're keeping it simple.
     *
     * @param authentication Object containing authenticated user info
     * @return Admin message
     */
    @GetMapping("/admin")
    public String adminEndpoint(Authentication authentication) {
        return "Welcome Admin " + authentication.getName() + "!";
    }

    /**
     * DTO (Data Transfer Object) for user information response.
     * Used to send user info in JSON format.
     */
    public static class UserInfoResponse {
        public String username;
        public String roles;
        public boolean authenticated;

        public UserInfoResponse(String username, String roles, boolean authenticated) {
            this.username = username;
            this.roles = roles;
            this.authenticated = authenticated;
        }

        // Getters for JSON serialization
        public String getUsername() { return username; }
        public String getRoles() { return roles; }
        public boolean isAuthenticated() { return authenticated; }
    }
}

