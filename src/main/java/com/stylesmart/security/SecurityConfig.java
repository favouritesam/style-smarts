package com.stylesmart.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// This annotation marks this class as a Spring Configuration class
// It contains bean definitions and security configuration
@Configuration
// This annotation enables Spring Security web security support
@EnableWebSecurity
public class SecurityConfig {

    // This annotation defines a bean that Spring will manage
    // A PasswordEncoder bean is required to encode passwords before storing them
    @Bean
    public PasswordEncoder passwordEncoder() {
        // We're using BCryptPasswordEncoder, which is a strong, industry-standard password encoder
        // BCrypt automatically handles salt generation and hashing
        return new BCryptPasswordEncoder();
    }

    // This annotation defines a SecurityFilterChain bean
    // This configures the HTTP security rules for our application
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Step 1: Disable CSRF (Cross-Site Request Forgery) protection
        // For REST APIs with JWT, CSRF is typically disabled
        http.csrf(csrf -> csrf.disable());

        // Step 2: Configure authorization rules
        http.authorizeHttpRequests(auth -> auth
                // Allow all requests to /api/auth/register without authentication
                // This means anyone can register without being logged in
                .requestMatchers("/api/auth/register").permitAll()
                // Allow all requests to /api/auth/login without authentication
                // This means anyone can login without being logged in
                .requestMatchers("/api/auth/login").permitAll()
                // Allow Swagger UI endpoints without authentication
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // All other requests require authentication
                // Later, we'll add more endpoints here (like wardrobe, etc.)
                .anyRequest().authenticated()
        );

        // Step 3: Build and return the SecurityFilterChain
        return http.build();
    }
}
