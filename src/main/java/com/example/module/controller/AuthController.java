package com.example.module.controller;

import com.example.module.entity.Users;
import com.example.module.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

/**
 * AuthController - Handles user authentication (login and signup)
 *
 * @Slf4j: Lombok annotation that automatically generates a logger
 * This allows us to use 'log' variable without manual initialization
 *
 * Behind the scenes, Lombok creates:
 * private static final Logger log = LoggerFactory.getLogger(AuthController.class);
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Users user) {
        log.info("Signup request received for username: {}", user.getUsername());

        if (userService.findByUsername(user.getUsername()) != null) {
            log.warn("Warning message: Username '{}' already exists in the system", user.getUsername());
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }

        try {
            user.setRoles(Arrays.asList("ROLE_USER"));
            userService.saveNewUser(user);
            log.info("User '{}' registered successfully with role ROLE_USER", user.getUsername());
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error occurred while registering user '{}': {}", user.getUsername(), e.getMessage(), e);
            return new ResponseEntity<>("An error occurred during registration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user, HttpServletRequest request, HttpServletResponse response) {
        log.info("Login request received for username: {}", user.getUsername());

        try {
            log.debug("Attempting to authenticate user: {}", user.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            
            log.debug("Authentication successful for user: {}", user.getUsername());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            
            log.info("User '{}' logged in successfully", user.getUsername());
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Warning message: Login failed for username: {} - Reason: {}", user.getUsername(), e.getMessage());
            log.error("Error occurred during login attempt for user '{}': {}", user.getUsername(), e.getMessage(), e);
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }
}
