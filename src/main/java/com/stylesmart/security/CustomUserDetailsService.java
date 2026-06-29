package com.stylesmart.security;

import com.stylesmart.entity.User;
import com.stylesmart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService implements Spring Security's UserDetailsService interface.
 * It is used by Spring Security to load user-specific data during authentication.
 * By defining this as a @Service bean, Spring Security will automatically use it
 * to verify user credentials during authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Inject UserRepository to query user data from the database
    @Autowired
    private UserRepository userRepository;

    /**
     * Locates the user based on the username. In our system, the username is used
     * as the unique identifier for security context authentication.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never null)
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Step 1: Retrieve user from database. If not found, throw an exception.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Step 2: Build and return a Spring Security UserDetails object.
        // We map the user's role to Spring Security's roles.
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Stored encrypted password
                .roles(user.getRole())         // User role (e.g. USER, ADMIN)
                .build();
    }
}
