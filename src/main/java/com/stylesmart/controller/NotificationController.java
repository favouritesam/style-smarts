package com.stylesmart.controller;

import com.stylesmart.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * NotificationController exposes HTTP endpoints to trigger email notifications.
 *
 * Endpoints:
 * 1. POST /api/notifications/send-digest-all    → Send weekly digest to ALL users
 * 2. POST /api/notifications/send-digest-single → Send welcome digest to ONE specific user
 *
 * These endpoints are secured — the caller must have a valid JWT token.
 * In a production app, you would also restrict these to ADMIN role only.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    // Inject NotificationService which handles all the email sending logic
    @Autowired
    private NotificationService notificationService;

    /**
     * Endpoint 1: Send the weekly style digest email to ALL registered users.
     *
     * How to use in Postman:
     *   Method:  POST
     *   URL:     http://localhost:8082/api/notifications/send-digest-all
     *   Headers: Authorization: Bearer <your_jwt_token>
     *   Body:    (none required)
     *
     * @return A summary of how many emails were sent and how many failed
     */
    @PostMapping("/send-digest-all")
    public ResponseEntity<?> sendDigestToAllUsers() {
        try {
            // Step 1: Delegate the bulk email sending to NotificationService
            // This will loop through all users and send each one a style digest email
            String result = notificationService.sendWeeklyStyleDigestToAllUsers();

            // Step 2: Return 200 OK with a summary message
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // If something goes unexpectedly wrong at a higher level, return a 500 error
            return ResponseEntity.internalServerError().body("Error sending notifications: " + e.getMessage());
        }
    }

    /**
     * Endpoint 2: Send a welcome style digest email to a SINGLE user by email address.
     * Useful for sending when a user first enables email notifications.
     *
     * How to use in Postman:
     *   Method:  POST
     *   URL:     http://localhost:8082/api/notifications/send-digest-single
     *   Headers: Authorization: Bearer <your_jwt_token>
     *            Content-Type: application/json
     *   Body (raw JSON):
     *     {
     *       "email": "user@example.com",
     *       "username": "Favsam"
     *     }
     *
     * @param request - A simple map containing "email" and "username" fields
     * @return A success or failure message
     */
    @PostMapping("/send-digest-single")
    public ResponseEntity<?> sendDigestToSingleUser(@RequestBody java.util.Map<String, String> request) {
        try {
            // Step 1: Extract the email and username from the JSON request body
            String email = request.get("email");
            String username = request.get("username");

            // Step 2: Validate that both fields are present and not empty
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("'email' field is required");
            }
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("'username' field is required");
            }

            // Step 3: Call the service to send the welcome digest email to this one user
            String result = notificationService.sendWelcomeDigestNotification(email.trim(), username.trim());

            // Step 4: Return the result message
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error sending notification: " + e.getMessage());
        }
    }
}
