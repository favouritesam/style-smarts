package com.stylesmart.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) containing details to be updated.
 * Received from the client for updating profile data.
 * All fields are optional (non-blank validation is handled in service).
 */
@Getter
@Setter
public class ProfileUpdateRequest {

    // New username. Must be between 3 and 50 characters if provided.
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    // New email. Must be in valid email format if provided.
    @Email(message = "Email should be valid")
    private String email;

    // New password. Must be at least 6 characters if provided.
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // Default constructor - required for JSON serialization
    public ProfileUpdateRequest() {
    }

    // Parametric constructor - useful for testing
    public ProfileUpdateRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
