package com.stylesmart.service;

import com.stylesmart.entity.User;
import com.stylesmart.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * NotificationService handles the business logic for sending
 * email notifications to users.
 *
 * It works together with:
 * - UserRepository: to fetch all registered users from the database
 * - EmailService: to actually send the HTML email via Gmail SMTP
 */
@Service
public class NotificationService {

    // Inject UserRepository to query all users from the database
    @Autowired
    private UserRepository userRepository;

    // Inject EmailService which handles the actual email delivery
    @Autowired
    private EmailService emailService;

    /**
     * Sends the "Weekly Style Digest" email notification to every user in the database.
     *
     * How it works:
     * 1. Fetch all registered users from the database using JpaRepository.findAll()
     * 2. Loop through each user one by one
     * 3. Build a personalized HTML email body for that user (using their username)
     * 4. Send the email to that user's email address
     * 5. Track how many succeeded and how many failed
     * 6. Return a summary result back to the controller
     *
     * @return A summary string e.g. "Digest sent to 25 users. 0 failed."
     */
    public String sendWeeklyStyleDigestToAllUsers() {
        // Step 1: Fetch every user from the database
        // findAll() is a built-in JpaRepository method that returns a List of all User entities
        List<User> allUsers = userRepository.findAll();

        // Step 2: Track success and failure counts
        int successCount = 0;
        int failCount = 0;

        // Step 3: The email subject line that will appear in each user's inbox
        String subject = "✨ Your Weekly Style Digest is Here!";

        // Step 4: Loop through every user and send them a personalized email
        for (User user : allUsers) {
            try {
                // Step 4a: Build a personalized HTML email body for this specific user
                // We pass the username so the email says "Hey, [username]!"
                String htmlBody = emailService.buildStyleDigestEmailBody(user.getUsername());

                // Step 4b: Send the HTML email to this user's email address
                // This calls JavaMailSender which connects to Gmail SMTP and delivers the email
                emailService.sendHtmlEmail(
                        user.getEmail(),    // recipient email (e.g. john@gmail.com)
                        user.getUsername(), // recipient name (used in logs and helper)
                        subject,            // email subject line
                        htmlBody            // the full HTML email content
                );

                // Step 4c: Count this as a success
                successCount++;

                // Log to the console so you can see progress in IntelliJ
                System.out.println("✅ Style digest sent to: " + user.getEmail());

            } catch (MessagingException e) {
                // Step 4d: If sending to this user fails (e.g. invalid email, network error),
                // we log the error and continue to the next user (we don't stop the whole loop)
                failCount++;
                System.err.println("❌ Failed to send to: " + user.getEmail() + " | Reason: " + e.getMessage());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        // Step 5: Build and return a human-readable summary
        return String.format(
                "Weekly style digest complete. ✅ Sent: %d | ❌ Failed: %d | Total users: %d",
                successCount, failCount, allUsers.size()
        );
    }

    /**
     * Sends a "Welcome — Weekly Style Digest Subscribed" email to a single user.
     * This can be called right after a user registers or enables email notifications.
     *
     * @param userEmail - The email address of the user to notify
     * @param username  - The username of the user (for personalizing the email greeting)
     * @return A result message indicating success or failure
     */
    public String sendWelcomeDigestNotification(String userEmail, String username) {
        try {
            // Step 1: Define the subject line for this one-time welcome email
            String subject = "🎉 You're subscribed to StyleSmart Weekly Digest!";

            // Step 2: Build the personalized HTML email body for this user
            String htmlBody = emailService.buildStyleDigestEmailBody(username);

            // Step 3: Send the email via Gmail SMTP
            emailService.sendHtmlEmail(userEmail, username, subject, htmlBody);

            // Step 4: Log success
            System.out.println("✅ Welcome digest notification sent to: " + userEmail);

            return "Email notification sent successfully to " + userEmail;

        } catch (MessagingException e) {
            // If sending fails, log and return an error message
            System.err.println("❌ Failed to send welcome notification to: " + userEmail + " | " + e.getMessage());
            return "Failed to send notification: " + e.getMessage();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
