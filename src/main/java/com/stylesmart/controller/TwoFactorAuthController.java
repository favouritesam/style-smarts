package com.stylesmart.controller;

import com.stylesmart.service.TwoFactorAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * TwoFactorAuthController exposes HTTP REST endpoints for Two-Factor Authentication (2FA).
 * 
 * All endpoints require a valid JWT token passed in the Authorization header.
 * The JWT token is automatically validated by Spring Security before reaching these methods.
 * 
 * Available endpoints:
 * 1. POST /api/2fa/setup - Start 2FA setup (generates and sends verification code via email)
 * 2. POST /api/2fa/verify - Verify the 6-digit code and enable 2FA
 * 3. POST /api/2fa/disable - Disable 2FA for the authenticated user
 * 4. GET /api/2fa/status - Check if 2FA is enabled for the authenticated user
 */
@RestController
@RequestMapping("/api/2fa")
public class TwoFactorAuthController {

    // Inject TwoFactorAuthService which handles all 2FA business logic
    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    /**
     * Endpoint 1: Start 2FA Setup
     * 
     * This endpoint is called when the user clicks "Enable 2FA" in the settings page.
     * It generates a random 6-digit verification code and sends it to the user's email.
     * 
     * How to use in Postman:
     *   Method:  POST
     *   URL:     http://localhost:8082/api/2fa/setup
     *   Headers: Authorization: Bearer <your_jwt_token>
     *   Body:    (none required)
     * 
     * What happens:
     * 1. Spring Security validates the JWT token and extracts the username
     * 2. Service generates a random 6-digit code
     * 3. Service sends the code to user's email via Gmail SMTP
     * 4. User receives email with the code
     * 5. User enters the code in the UI to complete setup
     * 
     * @param authentication - The authenticated user credentials (injected by Spring Security)
     * @return Success message indicating the code was sent
     */
    @PostMapping("/setup")
    public ResponseEntity<?> startTwoFactorSetup(Authentication authentication) {
        try {
            // Step 1: Extract the username from the authenticated user's JWT token
            // Spring Security automatically provides this from the validated JWT
            String username = authentication.getName();

            // Step 2: Call the service to generate and send the verification code
            // This will generate a 6-digit code and send it via email
            String result = twoFactorAuthService.startTwoFactorSetup(username);

            // Step 3: Return 200 OK with the success message
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // Step 4: If something goes wrong, return a 500 error with the error message
            return ResponseEntity.internalServerError().body("Error starting 2FA setup: " + e.getMessage());
        }
    }

    /**
     * Endpoint 2: Verify 2FA Code
     * 
     * This endpoint is called when the user enters the 6-digit verification code from the email.
     * It verifies the code and enables 2FA for the user if the code is valid.
     * 
     * How to use in Postman:
     *   Method:  POST
     *   URL:     http://localhost:8082/api/2fa/verify
     *   Headers: Authorization: Bearer <your_jwt_token>
     *            Content-Type: application/json
     *   Body (raw JSON):
     *     {
     *       "code": "123456"
     *     }
     * 
     * What happens:
     * 1. User enters the 6-digit code from the email
     * 2. Frontend sends the code to this endpoint
     * 3. Backend verifies the code matches the stored code
     * 4. Backend checks if code has expired (5 minutes)
     * 5. If valid, 2FA is enabled for the user
     * 6. If invalid, an error is returned
     * 
     * @param authentication - The authenticated user credentials (injected by Spring Security)
     * @param request - JSON body containing the "code" field with the 6-digit verification code
     * @return Success message if code is valid, error message if invalid
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyTwoFactorCode(
            Authentication authentication,
            @RequestBody Map<String, String> request
    ) {
        try {
            // Step 1: Extract the username from the authenticated user's JWT token
            String username = authentication.getName();

            // Step 2: Extract the verification code from the JSON request body
            String code = request.get("code");

            // Step 3: Validate that the code field is present and not empty
            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("'code' field is required");
            }

            // Step 4: Call the service to verify the code and enable 2FA
            String result = twoFactorAuthService.verifyTwoFactorCode(username, code.trim());

            // Step 5: Return 200 OK with the success message
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // Step 6: If verification fails (invalid code, expired code, etc.), return a 400 error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint 3: Disable 2FA
     * 
     * This endpoint is called when the user clicks "Disable 2FA" in the settings page.
     * It disables 2FA for the authenticated user.
     * 
     * How to use in Postman:
     *   Method:  POST
     *   URL:     http://localhost:8082/api/2fa/disable
     *   Headers: Authorization: Bearer <your_jwt_token>
     *   Body:    (none required)
     * 
     * What happens:
     * 1. User clicks "Disable 2FA" in settings
     * 2. Frontend sends request to this endpoint
     * 3. Backend sets twoFactorEnabled to false in database
     * 4. Backend clears any stored verification codes
     * 5. User can now login without 2FA code
     * 
     * @param authentication - The authenticated user credentials (injected by Spring Security)
     * @return Success message indicating 2FA was disabled
     */
    @PostMapping("/disable")
    public ResponseEntity<?> disableTwoFactorAuth(Authentication authentication) {
        try {
            // Step 1: Extract the username from the authenticated user's JWT token
            String username = authentication.getName();

            // Step 2: Call the service to disable 2FA for this user
            String result = twoFactorAuthService.disableTwoFactorAuth(username);

            // Step 3: Return 200 OK with the success message
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // Step 4: If something goes wrong, return a 500 error with the error message
            return ResponseEntity.internalServerError().body("Error disabling 2FA: " + e.getMessage());
        }
    }

    /**
     * Endpoint 4: Check 2FA Status
     * 
     * This endpoint is called by the frontend to check if 2FA is enabled for the current user.
     * This is useful for showing/hiding the "Enable 2FA" or "Disable 2FA" buttons in the UI.
     * 
     * How to use in Postman:
     *   Method:  GET
     *   URL:     http://localhost:8082/api/2fa/status
     *   Headers: Authorization: Bearer <your_jwt_token>
     *   Body:    (none required)
     * 
     * What happens:
     * 1. Frontend calls this endpoint when loading the settings page
     * 2. Backend checks if 2FA is enabled for the user
     * 3. Backend returns true/false status
     * 4. Frontend shows appropriate UI based on status
     * 
     * @param authentication - The authenticated user credentials (injected by Spring Security)
     * @return JSON object with "enabled" field (true/false)
     */
    @GetMapping("/status")
    public ResponseEntity<?> getTwoFactorStatus(Authentication authentication) {
        try {
            // Step 1: Extract the username from the authenticated user's JWT token
            String username = authentication.getName();

            // Step 2: Call the service to check if 2FA is enabled for this user
            boolean isEnabled = twoFactorAuthService.isTwoFactorEnabled(username);

            // Step 3: Return 200 OK with the status in JSON format
            return ResponseEntity.ok(Map.of("enabled", isEnabled));

        } catch (Exception e) {
            // Step 4: If something goes wrong, return a 500 error with the error message
            return ResponseEntity.internalServerError().body("Error checking 2FA status: " + e.getMessage());
        }
    }
}
