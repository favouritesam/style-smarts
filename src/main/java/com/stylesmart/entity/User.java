package com.stylesmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// This annotation marks this class as a JPA entity, which means it represents a table in the database
@Getter
@Entity
// This annotation specifies the table name in the database
@Table(name = "users")
public class User {

    // Getter for ID
    // This annotation marks this field as the primary key of the table
    @Id
    // This annotation tells Hibernate to automatically generate a unique ID for each new user
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Getter and Setter methods for username
    // This annotation maps this field to a column in the database table
    @Column(nullable = false, unique = true, length = 50)
    // Username field - cannot be null, must be unique, max 50 characters
    private String username;

    // Getter and Setter methods for email
    // Email field - cannot be null, must be unique, max 100 characters
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // Getter and Setter methods for password
    // Password field - cannot be null, will store the encoded (hashed) password
    @Column(nullable = false)
    private String password;

    // Getter and Setter methods for role
    // Role field - defaults to "USER" for new registrations
    @Column(nullable = false)
    private String role = "USER";

    // Getter for createdAt
    // This annotation automatically sets the creation timestamp when a new user is created
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Getter for updatedAt
    // This annotation automatically updates the timestamp when the user record is modified
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor - required by JPA
    public User() {
    }

    // Constructor with fields - useful for creating new user objects
    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
