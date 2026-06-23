package com.stylesmart.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// This is a Data Transfer Object (DTO) - used to transfer data between layers
// Specifically, this DTO will receive registration data from the client/frontend
@Setter
@Getter
public class RegisterRequest {

    // Getter and Setter for username
    // This annotation ensures the username is not null or empty
    @NotBlank(message = "Username is required")
    // This annotation ensures username is between 3 and 50 characters
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    // Getter and Setter for email
    // This annotation ensures the email is not null or empty
    @NotBlank(message = "Email is required")
    // This annotation validates that the email is in proper email format
    @Email(message = "Email should be valid")
    private String email;

    // Getter and Setter for password
    // This annotation ensures the password is not null or empty
    @NotBlank(message = "Password is required")
    // This annotation ensures password is at least 6 characters long
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // Default constructor - required for JSON deserialization
    public RegisterRequest() {
    }

    // Constructor with fields - useful for testing
    public RegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}
