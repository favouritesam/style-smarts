package com.stylesmart.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// This is a Data Transfer Object (DTO) for login requests
// It will receive login credentials from the client/frontend
@Getter
@Setter
public class LoginRequest {

    // This annotation ensures the email is not null or empty
    @NotBlank(message = "Email is required")
    // This annotation validates that the email is in proper email format
    @Email(message = "Email should be valid")
    private String email;

    // This annotation ensures the password is not null or empty
    @NotBlank(message = "Password is required")
    private String password;

    // Default constructor - required for JSON deserialization
    public LoginRequest() {
    }

    // Constructor with fields - useful for testing
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
