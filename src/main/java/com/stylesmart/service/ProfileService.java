package com.stylesmart.service;

import com.stylesmart.dto.profile.ProfileResponse;
import com.stylesmart.dto.profile.ProfileUpdateRequest;
import com.stylesmart.entity.User;
import com.stylesmart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * ProfileService manages all business logic related to user profiles.
 * This includes retrieving profile details, updating profile details,
 * uploading/updating profile pictures, and deleting user accounts.
 */
@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Standard date format formatter (e.g. "June 25, 2026")
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    /**
     * Retrieves the profile details of the user.
     * Includes formatting the creation date into user-friendly joined day and date.
     *
     * @param username the username of the logged-in user.
     * @return a ProfileResponse DTO containing formatted data.
     */
    public ProfileResponse getProfileDetails(String username) {
        // Step 1: Find user in the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Format the joined date and day if createdAt is not null
        String joinedDate = "";
        String joinedDay = "";
        if (user.getCreatedAt() != null) {
            joinedDate = user.getCreatedAt().format(dateFormatter);
            joinedDay = user.getCreatedAt().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        }

        // Step 3: Map user entity to ProfileResponse DTO
        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePictureUrl(),
                joinedDate,
                joinedDay
        );
    }

    /**
     * Updates the username, email, or password of the authenticated user.
     * Performs validation to prevent duplicate usernames or email addresses.
     *
     * @param currentUsername the username of the currently authenticated user.
     * @param request the profile details to update.
     * @return a ProfileResponse containing the updated profile.
     */
    public ProfileResponse updateProfileDetails(String currentUsername, ProfileUpdateRequest request) {
        // Step 1: Find the existing user from database
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Validate and update username if provided
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            String newUsername = request.getUsername().trim();
            // Check if username is changing and is already taken by another user
            if (!newUsername.equalsIgnoreCase(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(newUsername);
        }

        // Step 3: Validate and update email if provided
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim();
            // Check if email is changing and is already taken by another user
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(newEmail);
        }

        // Step 4: Encode and update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
        }

        // Step 5: Save the modified user back to the database
        User updatedUser = userRepository.save(user);

        // Step 6: Return the updated profile DTO
        return getProfileDetails(updatedUser.getUsername());
    }

    /**
     * Handles uploading or updating the profile picture.
     * If the user already had a picture, it deletes the old one from Cloudinary.
     *
     * @param username the username of the user uploading the image.
     * @param file the uploaded multipart file.
     * @return the secure URL of the new profile picture.
     * @throws IOException if Cloudinary upload fails.
     */
    public String uploadProfilePicture(String username, MultipartFile file) throws IOException {
        // Step 1: Verify the file is not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }

        // Step 2: Retrieve user entity
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 3: Delete old profile picture from Cloudinary if it exists
        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            String oldPublicId = cloudinaryService.extractPublicId(user.getProfilePictureUrl());
            if (oldPublicId != null) {
                try {
                    cloudinaryService.deleteImage(oldPublicId);
                } catch (Exception e) {
                    // Log error but proceed so upload doesn't break
                    System.err.println("Failed to delete old image from Cloudinary: " + e.getMessage());
                }
            }
        }

        // Step 4: Upload new image to Cloudinary and get the secure URL
        String newUrl = cloudinaryService.uploadImage(file);

        // Step 5: Save URL in the user record
        user.setProfilePictureUrl(newUrl);
        userRepository.save(user);

        return newUrl;
    }

    /**
     * Deletes the user account completely, cleaning up their profile image from Cloudinary.
     *
     * @param username the username of the user deleting their account.
     */
    public void deleteAccount(String username) {
        // Step 1: Fetch user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Cleanup image asset from Cloudinary
        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            String publicId = cloudinaryService.extractPublicId(user.getProfilePictureUrl());
            if (publicId != null) {
                try {
                    cloudinaryService.deleteImage(publicId);
                } catch (Exception e) {
                    // Log error but continue deleting the database record
                    System.err.println("Failed to clean up Cloudinary asset: " + e.getMessage());
                }
            }
        }

        // Step 3: Remove user from repository
        userRepository.delete(user);
    }
}
