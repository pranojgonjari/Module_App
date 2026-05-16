package com.example.module;

import com.example.module.entity.Users;
import com.example.module.repository.UserRepository;
import com.example.module.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserDetailesServiceImplTest - Unit tests for CustomUserDetailsService
 *
 * This test class demonstrates:
 * - @Mock: Creates mock objects for dependencies
 * - @InjectMocks: Injects mock dependencies into the service under test
 * - @BeforeEach: Sets up test fixtures before each test
 * - @Test: Marks methods as test cases
 */
public class UserDetailesServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    /**
     * @BeforeEach - Runs before each test method
     * Sets up mock objects and initializes the service
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * @Test - Test case: Load user by username successfully
     * Tests the happy path where user exists in database
     */
    @Test
    public void testLoadUserByUsernameSuccess() {
        // Arrange: Create a test user
        Users testUser = new Users();
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$hashedPassword");
        testUser.setRoles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));

        // Mock the repository to return the test user
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // Act: Call the service method
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Assert: Verify the results
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("$2a$10$hashedPassword", userDetails.getPassword());
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());

        // Verify that the repository was called exactly once
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    /**
     * @Test - Test case: User not found throws exception
     * Tests behavior when user doesn't exist in database
     */
    @Test
    public void testLoadUserByUsernameNotFound() {
        // Arrange: Mock the repository to return empty Optional
        when(userRepository.findByUsername("nonexistent"))
                .thenReturn(Optional.empty());

        // Act & Assert: Verify UsernameNotFoundException is thrown
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent");
        });

        // Verify that the repository was called
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    /**
     * @Test - Test case: User permissions are converted correctly
     * Tests that user roles are properly converted to GrantedAuthority
     */
    @Test
    public void testUserRolesConvertedToAuthorities() {
        // Arrange: Create a user with specific roles
        Users testUser = new Users();
        testUser.setUsername("admin");
        testUser.setPassword("$2a$10$adminPassword");
        testUser.setRoles(Arrays.asList("ROLE_ADMIN", "ROLE_MODERATOR"));

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(testUser));

        // Act: Load user details
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        // Assert: Verify authorities contain expected roles
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MODERATOR")));

        verify(userRepository, times(1)).findByUsername("admin");
    }
}
