package com.example.module;

import com.example.module.entity.Users;
import com.example.module.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.CommandLineRunner;

import java.util.Arrays;

/**
 * ModuleApplication - Main Spring Boot Application Class.
 *
 * What does @SpringBootApplication do?
 * - Combines @Configuration, @ComponentScan, and @EnableAutoConfiguration
 * - Enables auto-configuration based on classpath dependencies
 * - Enables Spring component scanning
 *
 * What does @EnableMongoRepositories do?
 * - Scans for MongoRepository interfaces
 * - Creates bean instances for each repository
 * - Enables Spring Data MongoDB features
 *
 * What does @EnableTransactionManagement do?
 * - Enables @Transactional annotation processing
 * - Provides transaction management for MongoDB operations
 * - Works with MongoTransactionManager
 */
@SpringBootApplication
@EnableMongoRepositories
@EnableTransactionManagement
public class ModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleApplication.class, args);
    }

    /**
     * Transaction Manager Bean for MongoDB.
     *
     * Why do we need MongoTransactionManager?
     * - MongoDB 4.0+ supports multi-document transactions
     * - Spring Security + @Transactional requires it
     * - Ensures ACID compliance for operations
     *
     * @param dbFactory MongoDB database factory
     * @return MongoTransactionManager instance
     */
    @Bean
    public PlatformTransactionManager transactionManager(
            MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    /**
     * Initialize Collections and Default Admin User.
     *
     * This CommandLineRunner:
     * 1. Creates MongoDB collections if they don't exist
     * 2. Creates a default admin user for testing
     *
     * Why use CommandLineRunner?
     * - Executes automatically when application starts
     * - Useful for initialization tasks
     * - ApplicationContext is fully initialized
     *
     * @param mongoOps MongoDB operations template
     * @param userRepository User repository for database operations
     * @param passwordEncoder BCrypt password encoder
     * @return CommandLineRunner bean
     */
    @Bean
    public CommandLineRunner initCollections(
            MongoOperations mongoOps,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Create collections if they don't exist
            if (!mongoOps.collectionExists("module_entries")) {
                mongoOps.createCollection("module_entries");
                System.out.println("✓ Created 'module_entries' collection");
            }
            if (!mongoOps.collectionExists("users")) {
                mongoOps.createCollection("users");
                System.out.println("✓ Created 'users' collection");
            }

            // Create default admin user if not exists
            // Check if admin user already exists
            if (userRepository.findByUsername("admin").isEmpty()) {
                // Create new admin user
                Users adminUser = new Users();
                adminUser.setUsername("admin");

                // IMPORTANT: Password MUST be BCrypt encoded!
                // BCryptPasswordEncoder.encode() is called here
                // This ensures the password is hashed before storing in database
                // Never store plaintext passwords!
                //
                // What does "admin123" become after BCrypt encoding?
                // Example: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DvDLaym
                // The hash includes:
                // - Hash algorithm marker: $2a$
                // - Cost factor: 10 (number of iterations)
                // - Random salt (22 characters)
                // - Actual hash
                adminUser.setPassword(passwordEncoder.encode("admin123"));

                // Set admin roles
                // This list determines what this user can do
                // ROLE_ADMIN - Full administrator access
                adminUser.setRoles(Arrays.asList("ROLE_ADMIN"));

                // Save to MongoDB
                userRepository.save(adminUser);
                System.out.println("✓ Created default admin user");
                System.out.println("  Username: admin");
                System.out.println("  Password: admin123 (BCrypt encoded in database)");
                System.out.println("  Role: ROLE_ADMIN");
            } else {
                System.out.println("✓ Admin user already exists");
            }

            // Optional: Create a regular user for testing
            if (userRepository.findByUsername("user").isEmpty()) {
                Users regularUser = new Users();
                regularUser.setUsername("user");
                regularUser.setPassword(passwordEncoder.encode("user123"));
                regularUser.setRoles(Arrays.asList("ROLE_USER"));
                userRepository.save(regularUser);
                System.out.println("✓ Created default regular user");
                System.out.println("  Username: user");
                System.out.println("  Password: user123 (BCrypt encoded in database)");
                System.out.println("  Role: ROLE_USER");
            }

            System.out.println("\n=== Application Initialized Successfully ===");
            System.out.println("Available Endpoints:");
            System.out.println("  PUBLIC (no auth): GET /public/hello");
            System.out.println("  PUBLIC (no auth): GET /public/health");
            System.out.println("  SECURED (auth needed): GET /api/hello");
            System.out.println("  SECURED (auth needed): GET /api/user-info");
            System.out.println("  SECURED (auth needed): GET /api/admin");
            System.out.println("\nTest with Basic Auth:");
            System.out.println("  Username: admin");
            System.out.println("  Password: admin123");
        };
    }
}