package com.example.module.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

/**
 * Users Entity - Represents a user in the MongoDB database.
 *
 * Why @Document annotation?
 * - Maps this class to a MongoDB collection named "users"
 * - Converts MongoDB documents to Java objects and vice versa
 *
 * Why @Indexed(unique = true)?
 * - Ensures no two users can have the same username
 * - MongoDB creates a unique index on this field
 * - Prevents duplicate user registrations
 */
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    /**
     * Unique identifier in MongoDB.
     * ObjectId is MongoDB's default unique identifier type.
     * Spring Data automatically generates this if not provided.
     */
    @Id
    private ObjectId id;

    /**
     * Username field - must be unique (enforced by @Indexed annotation).
     * Will be used for Basic Authentication.
     */
    @Indexed(unique = true)
    @NonNull
    private String username;

    /**
     * Password field - MUST be stored as BCrypt encoded.
     * Never store passwords in plaintext!
     * BCrypt is a one-way hash function that includes salt.
     * Even if the database is compromised, passwords are not readable.
     */
    @NonNull
    private String password;

    /**
     * User Roles - List of authorities/roles assigned to the user.
     * Example: ROLE_ADMIN, ROLE_USER
     * These are converted to GrantedAuthority in UserDetailsService.
     * Used for authorization decisions.
     */
    private List<String> roles = new ArrayList<>();

    /**
     * References to ModuleEntry documents.
     * @DBRef creates a manual reference to documents in another collection.
     * Allows establishing relationships between users and their entries.
     */
    @DBRef
    private List<ModuleEntry> moduleEntries = new ArrayList<>();
}
