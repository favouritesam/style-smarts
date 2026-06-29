package com.stylesmart.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// This annotation marks this class as a Spring Configuration class
// It contains bean definitions and security configuration
@Configuration
// This annotation enables Spring Security web security support
@EnableWebSecurity
public class SecurityConfig {

    // Inject JwtFilter to authenticate requests with JWT tokens
    @Autowired
    private JwtFilter jwtFilter;

    // Inject JwtAuthenticationEntryPoint to handle unauthorized requests
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

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

        // Step 2: Configure stateless session management
        // We tell Spring Security not to create sessions on the server (stateless)
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Step 3: Configure unauthorized request handling
        // If authentication fails, use our custom JwtAuthenticationEntryPoint
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        );

        // Step 4: Configure authorization rules
        http.authorizeHttpRequests(auth -> auth
                // Allow all requests to /api/auth/register and /api/auth/login without authentication
                .requestMatchers("/api/auth/**").permitAll()
                // Allow Swagger UI endpoints without authentication
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
        );

        // Step 5: Add our custom JWT Filter before the standard UsernamePasswordAuthenticationFilter
        // This ensures the JWT is verified and security context set before reaching Spring's authentication filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Step 6: Build and return the SecurityFilterChain
        return http.build();
    }
}
