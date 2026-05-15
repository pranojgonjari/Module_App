package com.example.module.repository;

import com.example.module.entity.Users;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Data access layer for User entity.
 *
 * Why extend MongoRepository?
 * - Provides built-in CRUD operations (save, findAll, delete, etc.)
 * - Reduces boilerplate code for database operations
 * - Automatically creates MongoDB query implementations
 *
 * Why @Repository annotation?
 * - Marks this as a Spring component
 * - Provides exception translation from MongoDB to Spring exceptions
 * - Enables auto-wiring in other components
 */
@Repository
public interface UserRepository extends MongoRepository<Users, ObjectId> {

    /**
     * Find a user by username.
     * Spring Data MongoDB automatically generates the implementation
     * by analyzing the method name.
     *
     * Why Optional<Users>?
     * - Avoids NullPointerException
     * - Makes it explicit that a user might not exist
     * - Encourages safe programming practices
     *
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<Users> findByUsername(String username);
}
