package com.example.module;

import com.example.module.entity.Users;
import com.example.module.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // ---------------------------------------------------------
    // Test findByUsername()
    // ---------------------------------------------------------
    @Test
    @Order(1)
    void testFindByUsername() {

        System.out.println("Running testFindByUsername()");

        Optional<Users> user = userRepository.findByUsername("admin");

        assertNotNull(user, "User object should not be null");

        assertTrue(user.isPresent(), "User should exist");

        assertEquals("admin", user.get().getUsername());

        System.out.println("PASS : testFindByUsername()");
    }

    // ---------------------------------------------------------
    // Test assertEquals()
    // ---------------------------------------------------------
    @Test
    @Order(2)
    void testAssertEquals() {

        System.out.println("Running testAssertEquals()");

        String expected = "ROLE_ADMIN";

        Optional<Users> user = userRepository.findByUsername("admin");

        String actual = user.get().getRoles().get(0);

        assertEquals(expected, actual);

        System.out.println("PASS : testAssertEquals()");
    }

    // ---------------------------------------------------------
    // Test assertTrue()
    // ---------------------------------------------------------
    @Test
    @Order(3)
    void testAssertTrue() {

        System.out.println("Running testAssertTrue()");

        Optional<Users> user = userRepository.findByUsername("user");

        assertTrue(user.isPresent());

        System.out.println("PASS : testAssertTrue()");
    }

    // ---------------------------------------------------------
    // Test isEmpty()
    // ---------------------------------------------------------
    @Test
    @Order(4)
    void testGetEmpty() {

        System.out.println("Running testGetEmpty()");

        Optional<Users> user = userRepository.findByUsername("unknown");

        assertTrue(user.isEmpty());

        System.out.println("PASS : testGetEmpty()");
    }

    // ---------------------------------------------------------
    // Parameterized Test using CsvSource
    // ---------------------------------------------------------
    @ParameterizedTest
    @CsvSource({
            "admin, ROLE_ADMIN",
            "user, ROLE_USER",
            "Amol, ROLE_USER"
    })
    @Order(5)
    void parameterizedTest(String username, String role) {

        System.out.println("Running parameterizedTest() for : " + username);

        Optional<Users> user = userRepository.findByUsername(username);

        assertTrue(user.isPresent());

        assertEquals(role, user.get().getRoles().get(0));

        System.out.println("PASS : parameterizedTest() -> " + username);
    }

    // ---------------------------------------------------------
    // ArgumentsSource Example
    // ---------------------------------------------------------
    @ParameterizedTest
    @ArgumentsSource(UserArgumentsProvider.class)
    @Order(6)
    void testWithArgumentsSource(String username) {

        System.out.println("Running testWithArgumentsSource()");

        Optional<Users> user = userRepository.findByUsername(username);

        assertNotNull(user);

        assertTrue(user.isPresent());

        System.out.println("PASS : testWithArgumentsSource() -> " + username);
    }
}


// =====================================================
// Arguments Provider Class
// =====================================================

class UserArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(org.junit.jupiter.api.extension.ExtensionContext context) {

        return Stream.of(
                Arguments.of("admin"),
                Arguments.of("user"),
                Arguments.of("Amol")
        );
    }
}