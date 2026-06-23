package com.stylesmart.dto.auth;

import lombok.Getter;
import lombok.Setter;

// This is a Data Transfer Object (DTO) for sending authentication response back to client
// It will contain the JWT token and user information after successful registration/login
@Setter
@Getter
public class AuthResponse {

    // Getter and Setter for token
    // The JWT token that will be used for authenticated requests
    private String token;

    // Getter and Setter for type
    // The type of token (usually "Bearer")
    private String type = "Bearer";

    // Getter and Setter for username
    // The username of the authenticated user
    private String username;

    // Getter and Setter for email
    // The email of the authenticated user
    private String email;

    // Getter and Setter for role
    // The role of the authenticated user
    private String role;

    // Default constructor - required for JSON serialization
    public AuthResponse() {
    }

    // Constructor with fields - useful for creating response objects
    public AuthResponse(String token, String username, String email, String role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
    }

}
