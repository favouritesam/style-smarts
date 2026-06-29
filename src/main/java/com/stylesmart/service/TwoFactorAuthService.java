package com.stylesmart.service;

import com.stylesmart.entity.User;
import com.stylesmart.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * TwoFactorAuthService handles all business logic for Two-Factor Authentication (2FA).
 * 
 * How 2FA works in this application:
 * 1. User clicks "Enable 2FA" in settings
 * 2. Backend generates a random 6-digit code
 * 3. Backend sends the code to user's email via Gmail SMTP
 * 4. User receives email and enters the code in the UI
 * 5. Backend verifies the code and enables 2FA for the user
 * 6. On future logins, user must provide a verification code
 */
@Service
public class TwoFactorAuthService {

    // Inject UserRepository to fetch and update user records
    @Autowired
    private UserRepository userRepository;

    // Inject EmailService to send verification codes via email
    @Autowired
    private EmailService emailService;

    // SecureRandom for generating cryptographically strong random codes
    private static final SecureRandom random = new SecureRandom();

    // Code expiration time in minutes (codes expire after 5 minutes for security)
    private static final int CODE_EXPIRATION_MINUTES = 5;

    /**
     * STEP 1: Start 2FA setup - Generate and send verification code to user's email
     * 
     * This method is called when the user clicks "Enable 2FA" in the settings page.
     * 
     * What it does:
     * 1. Fetch the user from database
     * 2. Generate a random 6-digit verification code
     * 3. Store the code and timestamp in the user's record
     * 4. Send the code to the user's email address
     * 5. Return success message
     * 
     * @param username - The username of the user enabling 2FA
     * @return Success message indicating the code was sent
     */
    public String startTwoFactorSetup(String username) throws MessagingException, UnsupportedEncodingException {
        // Step 1: Fetch the user from the database by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Generate a random 6-digit verification code
        // We use 100000 to 999999 to ensure it's always 6 digits
        String verificationCode = String.format("%06d", random.nextInt(900000) + 100000);

        // Step 3: Store the verification code in the user's record
        user.setTwoFactorSecret(verificationCode);

        // Step 4: Store the current timestamp to track when the code was generated
        // This is used to enforce code expiration (5 minutes)
        user.setTwoFactorCodeGeneratedAt(LocalDateTime.now());

        // Step 5: Save the updated user record to the database
        userRepository.save(user);

        // Step 6: Build the HTML email body with the verification code
        String htmlBody = buildTwoFactorEmailBody(user.getUsername(), verificationCode);

        // Step 7: Send the email with the verification code
        emailService.sendHtmlEmail(
                user.getEmail(),           // recipient's email address
                user.getUsername(),        // recipient's name for personalization
                "🔐 Your 2FA Verification Code",  // email subject line
                htmlBody                   // HTML email content with the code
        );

        // Step 8: Log to console for debugging
        System.out.println("✅ 2FA verification code sent to: " + user.getEmail());
        System.out.println("📧 Code: " + verificationCode + " (for testing purposes)");

        // Step 9: Return success message
        return "2FA Setup wizard started. Check your email!";
    }

    /**
     * STEP 2: Verify the 2FA code entered by the user
     * 
     * This method is called when the user enters the 6-digit code from the email.
     * 
     * What it does:
     * 1. Fetch the user from database
     * 2. Check if the code matches the stored code
     * 3. Check if the code has expired (older than 5 minutes)
     * 4. If valid, enable 2FA for the user
     * 5. If invalid, throw an error
     * 
     * @param username - The username of the user verifying the code
     * @param code - The 6-digit verification code entered by the user
     * @return Success message if code is valid
     */
    public String verifyTwoFactorCode(String username, String code) {
        // Step 1: Fetch the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Check if a verification code has been generated for this user
        if (user.getTwoFactorSecret() == null) {
            throw new RuntimeException("No verification code generated. Please start 2FA setup first.");
        }

        // Step 3: Check if the code matches the stored code
        if (!user.getTwoFactorSecret().equals(code)) {
            throw new RuntimeException("Invalid verification code. Please try again.");
        }

        // Step 4: Check if the code has expired (older than 5 minutes)
        LocalDateTime codeGeneratedAt = user.getTwoFactorCodeGeneratedAt();
        if (codeGeneratedAt == null) {
            throw new RuntimeException("Code generation timestamp missing. Please start 2FA setup again.");
        }

        LocalDateTime expirationTime = codeGeneratedAt.plusMinutes(CODE_EXPIRATION_MINUTES);
        if (LocalDateTime.now().isAfter(expirationTime)) {
            throw new RuntimeException("Verification code has expired. Please request a new code.");
        }

        // Step 5: Code is valid - Enable 2FA for the user
        user.setTwoFactorEnabled(true);

        // Step 6: Clear the verification code from the database for security
        user.setTwoFactorSecret(null);
        user.setTwoFactorCodeGeneratedAt(null);

        // Step 7: Save the updated user record
        userRepository.save(user);

        // Step 8: Log success
        System.out.println("✅ 2FA enabled for user: " + username);

        // Step 9: Return success message
        return "Two-Factor Authentication enabled successfully!";
    }

    /**
     * STEP 3: Disable 2FA for a user
     * 
     * This method is called when the user clicks "Disable 2FA" in settings.
     * 
     * What it does:
     * 1. Fetch the user from database
     * 2. Set twoFactorEnabled to false
     * 3. Clear any stored verification codes
     * 4. Save the updated user record
     * 
     * @param username - The username of the user disabling 2FA
     * @return Success message
     */
    public String disableTwoFactorAuth(String username) {
        // Step 1: Fetch the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Disable 2FA
        user.setTwoFactorEnabled(false);

        // Step 3: Clear any stored verification codes for security
        user.setTwoFactorSecret(null);
        user.setTwoFactorCodeGeneratedAt(null);

        // Step 4: Save the updated user record
        userRepository.save(user);

        // Step 5: Log success
        System.out.println("✅ 2FA disabled for user: " + username);

        // Step 6: Return success message
        return "Two-Factor Authentication disabled successfully.";
    }

    /**
     * STEP 4: Check if 2FA is enabled for a user
     * 
     * This method is called during login to check if the user needs to provide a 2FA code.
     * 
     * @param username - The username to check
     * @return true if 2FA is enabled, false otherwise
     */
    public boolean isTwoFactorEnabled(String username) {
        // Step 1: Fetch the user from database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Return the 2FA enabled status
        return user.getTwoFactorEnabled() != null && user.getTwoFactorEnabled();
    }

    /**
     * Helper method: Build HTML email body for 2FA verification code
     * 
     * This creates a beautiful HTML email with the verification code prominently displayed.
     * 
     * @param username - The user's username for personalization
     * @param code - The 6-digit verification code to display
     * @return Complete HTML email body as a string
     */
    private String buildTwoFactorEmailBody(String username, String code) {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <title>2FA Verification Code</title>" +
                "</head>" +
                "<body style='margin:0; padding:0; font-family: Arial, Helvetica, sans-serif; background-color:#f4f4f7;'>" +

                // Outer wrapper
                "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f7; padding: 40px 0;'>" +
                "<tr><td align='center'>" +

                // Email card container
                "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.08); max-width:600px; width:100%;'>" +

                // Header
                "<tr>" +
                "<td style='background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%); padding: 48px 40px; text-align:center;'>" +
                "  <p style='margin:0 0 8px 0; font-size:13px; letter-spacing:4px; text-transform:uppercase; color:#e94560; font-weight:700;'>✦ STYLESMART ✦</p>" +
                "  <h1 style='margin:0; font-size:32px; font-weight:800; color:#ffffff; line-height:1.2;'>Two-Factor</h1>" +
                "  <h1 style='margin:0; font-size:32px; font-weight:800; color:#e94560; line-height:1.2;'>Authentication</h1>" +
                "</td>" +
                "</tr>" +

                // Greeting
                "<tr>" +
                "<td style='padding: 40px 40px 0 40px;'>" +
                "  <p style='margin:0 0 8px 0; font-size:22px; font-weight:700; color:#1a1a2e;'>Hey, " + username + "! 👋</p>" +
                "  <p style='margin:0; font-size:15px; color:#718096; line-height:1.7;'>" +
                "    You requested to enable Two-Factor Authentication for your StyleSmart account. " +
                "    Use the verification code below to complete the setup." +
                "  </p>" +
                "</td>" +
                "</tr>" +

                // Verification code display
                "<tr>" +
                "<td style='padding: 32px 40px; text-align:center;'>" +
                "  <div style='background-color:#f7fafc; border: 2px dashed #e94560; border-radius:12px; padding: 24px; display:inline-block;'>" +
                "    <p style='margin:0 0 8px 0; font-size:12px; color:#718096; text-transform:uppercase; letter-spacing:1px;'>Your Verification Code</p>" +
                "    <p style='margin:0; font-size:36px; font-weight:800; color:#1a1a2e; letter-spacing:8px;'>" + code + "</p>" +
                "  </div>" +
                "</td>" +
                "</tr>" +

                // Expiration warning
                "<tr>" +
                "<td style='padding: 0 40px 32px 40px;'>" +
                "  <p style='margin:0; font-size:13px; color:#e94560; text-align:center;'>" +
                "    ⚠️ This code expires in 5 minutes for your security." +
                "  </p>" +
                "</td>" +
                "</tr>" +

                // Footer
                "<tr>" +
                "<td style='background-color:#f7fafc; padding:32px 40px; text-align:center; border-top:1px solid #e2e8f0;'>" +
                "  <p style='margin:0 0 8px 0; font-size:13px; font-weight:700; color:#1a1a2e;'>StyleSmart</p>" +
                "  <p style='margin:0; font-size:11px; color:#cbd5e0;'>If you didn't request this code, please ignore this email.<br>" +
                "  © 2026 StyleSmart. All rights reserved.</p>" +
                "</td>" +
                "</tr>" +

                "</table>" +
                "</td></tr>" +
                "</table>" +
                "</body>" +
                "</html>";
    }
}
