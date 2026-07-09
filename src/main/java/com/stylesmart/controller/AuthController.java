package com.stylesmart.controller;

import com.stylesmart.dto.auth.AuthResponse;
import com.stylesmart.dto.auth.LoginRequest;
import com.stylesmart.dto.auth.RegisterRequest;
import com.stylesmart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// This annotation marks this class as a Spring REST Controller
// It handles HTTP requests and returns JSON responses
@RestController
// This annotation specifies the base URL for all endpoints in this controller
// All endpoints will start with /api/auth
@RequestMapping("/api/auth")
public class AuthController {

    // This annotation automatically injects the AuthService dependency
    // Spring will provide an instance of AuthService at runtime
    @Autowired
    private AuthService authService;

    // This annotation maps HTTP POST requests to this method
    // The endpoint will be /api/auth/register
    @PostMapping("/register")
    // This annotation validates the request body using the validation annotations in RegisterRequest
    // If validation fails, it automatically returns a 400 Bad Request with error details
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            // Step 1: Call the authService to handle the registration logic
            // The service will validate, create the user, and return a response
            AuthResponse response = authService.register(request);

            // Step 2: Return a successful HTTP response (200 OK) with the AuthResponse body
            // ResponseEntity.ok() creates a response with HTTP 200 status
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Step 3: If an exception occurs (like duplicate username/email), handle it here
            // Return a 400 Bad Request with the error message
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // This annotation maps HTTP POST requests to this method
    // The endpoint will be /api/auth/login
    @PostMapping("/login")
    // This annotation validates the request body using the validation annotations in LoginRequest
    // If validation fails, it automatically returns a 400 Bad Request with error details
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            // Step 1: Call the authService to handle the login logic
            // The service will validate credentials and generate a JWT token
            AuthResponse response = authService.login(request);

            // Step 2: Return a successful HTTP response (200 OK) with the AuthResponse body
            // The response will contain the JWT token and user information
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Step 3: If an exception occurs (user not found, invalid password), handle it here
            // Return a 401 Unauthorized with the error message
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

}