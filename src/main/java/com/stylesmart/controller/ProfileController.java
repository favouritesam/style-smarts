package com.stylesmart.controller;

import com.stylesmart.dto.profile.ProfileResponse;
import com.stylesmart.dto.profile.ProfileUpdateRequest;
import com.stylesmart.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * ProfileController exposes HTTP REST endpoints for profile and account management.
 * All endpoints require a valid JWT token passed in the Authorization header.
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * Endpoint 4: Get profile details of the authenticated user.
     * Accessible via GET /api/profile.
     *
     * @param authentication the authenticated user credentials injected by Spring Security.
     * @return the ProfileResponse DTO containing user info and join date.
     */
    @GetMapping
    public ResponseEntity<?> getProfileDetails(Authentication authentication) {
        try {
            // Get the username of the logged-in user from the security context
            String username = authentication.getName();
            
            // Fetch and return profile details
            ProfileResponse response = profileService.getProfileDetails(username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return bad request response on error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint 3: Update profile details (username, email, password) of the authenticated user.
     * Accessible via PUT /api/profile.
     *
     * @param authentication the authenticated user credentials injected by Spring Security.
     * @param request DTO containing updated profile fields.
     * @return the updated ProfileResponse.
     */
    @PutMapping
    public ResponseEntity<?> updateProfileDetails(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        try {
            // Retrieve current username from the authenticated session
            String currentUsername = authentication.getName();
            
            // Execute update logic
            ProfileResponse response = profileService.updateProfileDetails(currentUsername, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint 1: Upload profile picture for the first time.
     * Accessible via POST /api/profile/picture.
     * Consumes multipart/form-data.
     *
     * @param authentication the authenticated user credentials.
     * @param file the image file payload.
     * @return the secure URL of the uploaded image.
     */
    @PostMapping("/picture")
    public ResponseEntity<?> uploadProfilePicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // Get username from context
            String username = authentication.getName();
            
            // Perform Cloudinary upload and database updates
            String url = profileService.uploadProfilePicture(username, file);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint 2: Update existing profile picture.
     * Accessible via PUT /api/profile/picture.
     * Consumes multipart/form-data.
     * Matches POST functionality, but deletes the old profile picture from Cloudinary.
     *
     * @param authentication the authenticated user credentials.
     * @param file the new image file payload.
     * @return the secure URL of the updated image.
     */
    @PutMapping("/picture")
    public ResponseEntity<?> updateProfilePicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // Get username from context
            String username = authentication.getName();
            
            // Perform Cloudinary update (replaces old picture, uploads new one)
            String url = profileService.uploadProfilePicture(username, file);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint 5: Delete user account.
     * Accessible via DELETE /api/profile.
     * Deletes user records from the database and cleans up their Cloudinary image.
     *
     * @param authentication the authenticated user credentials.
     * @return success message string.
     */
    @DeleteMapping
    public ResponseEntity<?> deleteAccount(Authentication authentication) {
        try {
            // Get username from context
            String username = authentication.getName();
            
            // Perform delete logic
            profileService.deleteAccount(username);
            return ResponseEntity.ok("Account deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
