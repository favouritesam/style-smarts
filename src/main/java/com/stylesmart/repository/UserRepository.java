package com.stylesmart.repository;

import com.stylesmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// This annotation marks this interface as a Spring Data Repository
// Spring will automatically create an implementation at runtime
@Repository
// This interface extends JpaRepository, which provides built-in CRUD operations
// JpaRepository<EntityType, IDType> - User is our entity, Long is the ID type
public interface UserRepository extends JpaRepository<User, Long> {

    // This method will automatically find a user by username
    // Spring Data JPA generates the SQL query based on the method name
    // Returns Optional<User> to handle cases where user might not exist
    Optional<User> findByUsername(String username);

    // This method will automatically find a user by email
    // Useful for checking if email already exists during registration
    Optional<User> findByEmail(String email);

    // This method checks if a username already exists in the database
    // Returns true if username exists, false otherwise
    boolean existsByUsername(String username);

    // This method checks if an email already exists in the database
    // Returns true if email exists, false otherwise
    boolean existsByEmail(String email);
}
