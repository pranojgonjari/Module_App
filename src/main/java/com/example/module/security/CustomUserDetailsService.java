package com.example.module.security;

import com.example.module.entity.Users;
import com.example.module.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * CustomUserDetailsService - Implements Spring Security's UserDetailsService.
 *
 * Purpose:
 * - Loads user information from MongoDB during authentication
 * - Converts database user to Spring Security's UserDetails
 * - Used by Spring Security's authentication provider
 *
 * Authentication Flow:
 * 1. Client sends Basic Auth header (base64 encoded username:password)
 * 2. Spring Security's BasicAuthenticationFilter extracts credentials
 * 3. Authentication Manager calls loadUserByUsername(username)
 * 4. This method fetches user from MongoDB
 * 5. Password from DB is compared with provided password (BCrypt comparison)
 * 6. If match, user is authenticated and granted their roles
 *
 * Why implement UserDetailsService?
 * - Spring Security requires this interface to authenticate users
 * - Allows customization of where and how users are loaded
 * - in this case, from MongoDB instead of traditional SQL database
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Load user details by username from MongoDB.
     *
     * This method is called by Spring Security during authentication.
     *
     * How it works:
     * 1. Queries MongoDB for user with given username
     * 2. If found, converts user roles to GrantedAuthority objects
     * 3. Creates and returns Spring Security's User object
     * 4. If not found, throws UsernameNotFoundException
     *
     * Why UserDetails instead of Users entity?
     * - UserDetails is Spring Security's standard interface
     * - It knows about password encoding, enabled/disabled states, etc.
     * - Spring Security expects UserDetails, not custom entities
     *
     * @param username The username to load
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if user not found in database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from MongoDB using the repository
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert user roles (String) to GrantedAuthority objects
        // GrantedAuthority represents a user's authority/role in Spring Security
        Collection<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Create and return Spring Security's User object
        // org.springframework.security.core.userdetails.User is different from our Users entity
        // This User class is what Spring Security uses internally
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword()) // Password is already BCrypt encoded in DB
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}

