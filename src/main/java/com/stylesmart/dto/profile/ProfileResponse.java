package com.stylesmart.dto.profile;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) containing details of the user's profile.
 * Sent back to the client as a JSON response.
 */
@Getter
@Setter
public class ProfileResponse {

    // Unique database ID of the user
    private Long id;

    // Username of the user
    private String username;

    // Email of the user
    private String email;

    // URL of the user's profile picture hosted on Cloudinary
    private String profilePictureUrl;

    // The formatted date when the user registered/joined (e.g. "June 25, 2026")
    private String joinedDate;

    // The day of the week when the user registered/joined (e.g. "Thursday")
    private String joinedDay;

    // Default constructor - required for JSON serialization
    public ProfileResponse() {
    }

    // Parametric constructor - simplifies instantiation in services
    public ProfileResponse(Long id, String username, String email, String profilePictureUrl, String joinedDate, String joinedDay) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.joinedDate = joinedDate;
        this.joinedDay = joinedDay;
    }
}
