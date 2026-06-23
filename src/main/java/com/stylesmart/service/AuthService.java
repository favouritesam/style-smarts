package com.stylesmart.service;

import com.stylesmart.dto.auth.AuthResponse;
import com.stylesmart.dto.auth.LoginRequest;
import com.stylesmart.dto.auth.RegisterRequest;
import com.stylesmart.entity.User;
import com.stylesmart.repository.UserRepository;
import com.stylesmart.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// This annotation marks this class as a Spring Service
// Services contain business logic and are used by controllers
@Service
public class AuthService {

    // This annotation automatically injects the UserRepository dependency
    // Spring will provide an instance of UserRepository at runtime
    @Autowired
    private UserRepository userRepository;

    // This annotation automatically injects the PasswordEncoder dependency
    // PasswordEncoder will be used to hash passwords before storing them
    @Autowired
    private PasswordEncoder passwordEncoder;

    // This annotation automatically injects the JwtService dependency
    // JwtService will be used to generate JWT tokens
    @Autowired
    private JwtService jwtService;

    // This method handles the user registration logic
    // It takes a RegisterRequest DTO and returns an AuthResponse DTO
    public AuthResponse register(RegisterRequest request) {
        // Step 1: Check if username already exists in the database
        // If it exists, throw an exception to prevent duplicate usernames
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Step 2: Check if email already exists in the database
        // If it exists, throw an exception to prevent duplicate emails
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Step 3: Encode (hash) the password before storing it
        // This is crucial for security - we never store plain text passwords
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Step 4: Create a new User entity using the field constructor
        // This is cleaner than using default constructor + individual setters
        User user = new User(
            request.getUsername(),
            request.getEmail(),
            encodedPassword,
            "USER"
        );

        // Step 5: Save the user to the database
        // The save method returns the saved entity with the generated ID
        User savedUser = userRepository.save(user);

        // Step 6: Create and return the AuthResponse using the field constructor
        // This is cleaner than using default constructor + individual setters
        // For now, token is null - will be added when we implement JWT
        return new AuthResponse(
            null,
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getRole()
        );
    }

    // This method handles the user login logic
    // It takes a LoginRequest DTO and returns an AuthResponse DTO with JWT token
    public AuthResponse login(LoginRequest request) {
        // Step 1: Find the user by email from the database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Verify the password
        // We compare the provided password with the stored hashed password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Step 3: Create UserDetails object for JWT generation
        // Spring Security uses UserDetails for authentication
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();

        // Step 4: Generate JWT token
        String token = jwtService.generateToken(userDetails);

        // Step 5: Create and return the AuthResponse with the JWT token
        return new AuthResponse(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getRole()
        );
    }
}
