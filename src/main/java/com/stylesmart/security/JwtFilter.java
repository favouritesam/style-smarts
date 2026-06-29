package com.stylesmart.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtFilter intercepts every HTTP request once (OncePerRequestFilter) to validate JWT tokens.
 * If a valid JWT token is found in the Authorization header, it authenticates the user
 * and sets the security context so they can access secure endpoints.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    // Service to parse and validate JWT tokens
    @Autowired
    private JwtService jwtService;

    // Custom user details service to load user info from the database
    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Filters incoming HTTP requests, inspecting the Authorization header for a JWT token.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Step 1: Retrieve the Authorization header from the HTTP request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Step 2: If the header is missing or does not start with "Bearer ", skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the JWT token (skip "Bearer " which is 7 characters)
        jwt = authHeader.substring(7);
        
        try {
            // Step 4: Extract username from the JWT token
            username = jwtService.extractUsername(jwt);

            // Step 5: If username is found and user is not already authenticated in this session context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Step 6: Load UserDetails from database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // Step 7: Check if the token is valid for the loaded user details
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    
                    // Step 8: Create an authentication token using the user's details and authorities
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Step 9: Add request details to the authentication token
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Step 10: Set the authentication context in SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log authentication or parsing errors (such as expired or malformed JWT)
            // We do not throw exceptions here; unauthenticated requests will be blocked by Spring Security configuration
            logger.warn("JWT token parsing or validation failed: " + e.getMessage());
        }

        // Step 11: Continue the filter chain execution
        filterChain.doFilter(request, response);
    }
}
