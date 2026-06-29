package com.stylesmart.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * EmailService is responsible for sending HTML emails to users.
 *
 * How it works:
 * - Spring's JavaMailSender connects to Gmail's SMTP server using the credentials in application.properties
 * - We build a MimeMessage (which supports HTML content, unlike a plain SimpleMailMessage)
 * - MimeMessageHelper is a Spring helper that makes it easy to set email fields (to, subject, body, etc.)
 * - We then call mailSender.send() to deliver the email
 */
@Service
public class EmailService {

    // JavaMailSender is automatically configured by Spring Boot
    // using the spring.mail.* properties we set in application.properties
    @Autowired
    private JavaMailSender mailSender;

    // The "from" email address is injected from spring.mail.username in application.properties
    // This is the Gmail address that will appear as the sender
    @Value("${spring.mail.username}")
    private String fromEmail;

    // The "from" display name is injected from app.mail.from-name in application.properties
    // This is the friendly name that appears in the recipient's inbox e.g. "StyleSmart Team"
    @Value("${app.mail.from-name}")
    private String fromName;

    /**
     * Sends a single HTML email to a recipient.
     *
     * @param toEmail   - The recipient's email address (e.g. user@example.com)
     * @param toName    - The recipient's name, used for personalizing the email
     * @param subject   - The subject line of the email
     * @param htmlBody  - The full HTML content of the email body
     * @throws MessagingException if the email fails to send (e.g. network error, invalid address)
     */
    public void sendHtmlEmail(String toEmail, String toName, String subject, String htmlBody) throws MessagingException, UnsupportedEncodingException {
        // Step 1: Create a new MimeMessage - this is the actual email object
        MimeMessage message = mailSender.createMimeMessage();

        // Step 2: Wrap it in MimeMessageHelper for easier field setting
        // The second argument 'true' means we want to enable multipart mode (required for HTML emails)
        // The third argument sets the character encoding to UTF-8 (supports all characters, emojis, etc.)
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Step 3: Set the "From" field — this appears as the sender in the recipient's inbox
        // Format: "StyleSmart Team <your-gmail@gmail.com>"
        helper.setFrom(fromEmail, fromName);

        // Step 4: Set the "To" field — the recipient's email address
        helper.setTo(toEmail);

        // Step 5: Set the email subject line
        helper.setSubject(subject);

        // Step 6: Set the email body as HTML content
        // The second argument 'true' tells the helper this is HTML (not plain text)
        helper.setText(htmlBody, true);

        // Step 7: Send the email via Gmail's SMTP server
        mailSender.send(message);
    }

    /**
     * Builds a beautiful HTML email body for the Weekly Style Digest notification.
     * This generates a fully self-contained HTML email template (with inline CSS for compatibility).
     *
     * Email clients like Gmail, Outlook strip out <style> tags, so we use inline styles.
     *
     * @param username - The user's name, used to personalize the greeting
     * @return A string containing the complete HTML email body
     */
    public String buildStyleDigestEmailBody(String username) {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <title>StyleSmart Weekly Digest</title>" +
                "</head>" +
                "<body style='margin:0; padding:0; font-family: Arial, Helvetica, sans-serif; background-color:#f4f4f7;'>" +

                // ---- Outer wrapper ----
                "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f7; padding: 40px 0;'>" +
                "<tr><td align='center'>" +

                // ---- Email card container ----
                "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.08); max-width:600px; width:100%;'>" +

                // ---- Header / Banner ----
                "<tr>" +
                "<td style='background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%); padding: 48px 40px; text-align:center;'>" +
                "  <p style='margin:0 0 8px 0; font-size:13px; letter-spacing:4px; text-transform:uppercase; color:#e94560; font-weight:700;'>✦ STYLESMART ✦</p>" +
                "  <h1 style='margin:0; font-size:32px; font-weight:800; color:#ffffff; line-height:1.2;'>Your Weekly</h1>" +
                "  <h1 style='margin:0; font-size:32px; font-weight:800; color:#e94560; line-height:1.2;'>Style Digest</h1>" +
                "  <p style='margin:16px 0 0 0; font-size:15px; color:#a0aec0;'>Your personal fashion intelligence update</p>" +
                "</td>" +
                "</tr>" +

                // ---- Greeting section ----
                "<tr>" +
                "<td style='padding: 40px 40px 0 40px;'>" +
                "  <p style='margin:0 0 8px 0; font-size:22px; font-weight:700; color:#1a1a2e;'>Hey, " + username + "! 👋</p>" +
                "  <p style='margin:0; font-size:15px; color:#718096; line-height:1.7;'>" +
                "    Great news — you're now subscribed to <strong style='color:#e94560;'>StyleSmart Weekly Style Digests</strong>. " +
                "    Every week, we'll deliver your personal fashion intelligence straight to your inbox." +
                "  </p>" +
                "</td>" +
                "</tr>" +

                // ---- Divider ----
                "<tr><td style='padding: 32px 40px;'>" +
                "<hr style='border:none; border-top: 1px solid #e2e8f0; margin:0;'>" +
                "</td></tr>" +

                // ---- What you'll receive section ----
                "<tr>" +
                "<td style='padding: 0 40px;'>" +
                "  <p style='margin:0 0 20px 0; font-size:16px; font-weight:700; color:#1a1a2e; text-transform:uppercase; letter-spacing:1px;'>What's in your weekly digest 📦</p>" +

                // Feature card 1
                "  <table width='100%' cellpadding='0' cellspacing='0' style='margin-bottom:16px;'>" +
                "  <tr>" +
                "    <td style='width:52px; vertical-align:top;'>" +
                "      <div style='width:44px; height:44px; border-radius:12px; background-color:#fff0f3; display:flex; align-items:center; justify-content:center; font-size:22px; text-align:center; line-height:44px;'>👗</div>" +
                "    </td>" +
                "    <td style='padding-left:16px; vertical-align:top;'>" +
                "      <p style='margin:0 0 4px 0; font-size:15px; font-weight:700; color:#1a1a2e;'>Trending Styles This Week</p>" +
                "      <p style='margin:0; font-size:13px; color:#718096; line-height:1.6;'>Curated outfits and looks trending across the fashion world, tailored to your preferences.</p>" +
                "    </td>" +
                "  </tr>" +
                "  </table>" +

                // Feature card 2
                "  <table width='100%' cellpadding='0' cellspacing='0' style='margin-bottom:16px;'>" +
                "  <tr>" +
                "    <td style='width:52px; vertical-align:top;'>" +
                "      <div style='width:44px; height:44px; border-radius:12px; background-color:#f0fff4; display:flex; align-items:center; justify-content:center; font-size:22px; text-align:center; line-height:44px;'>💡</div>" +
                "    </td>" +
                "    <td style='padding-left:16px; vertical-align:top;'>" +
                "      <p style='margin:0 0 4px 0; font-size:15px; font-weight:700; color:#1a1a2e;'>AI Stylist Tips</p>" +
                "      <p style='margin:0; font-size:13px; color:#718096; line-height:1.6;'>Smart outfit combination suggestions and wardrobe optimization tips from your AI stylist.</p>" +
                "    </td>" +
                "  </tr>" +
                "  </table>" +

                // Feature card 3
                "  <table width='100%' cellpadding='0' cellspacing='0' style='margin-bottom:32px;'>" +
                "  <tr>" +
                "    <td style='width:52px; vertical-align:top;'>" +
                "      <div style='width:44px; height:44px; border-radius:12px; background-color:#fffff0; display:flex; align-items:center; justify-content:center; font-size:22px; text-align:center; line-height:44px;'>⭐</div>" +
                "    </td>" +
                "    <td style='padding-left:16px; vertical-align:top;'>" +
                "      <p style='margin:0 0 4px 0; font-size:15px; font-weight:700; color:#1a1a2e;'>Your Saved Gems</p>" +
                "      <p style='margin:0; font-size:13px; color:#718096; line-height:1.6;'>A recap of your saved outfits and new recommendations based on your wardrobe.</p>" +
                "    </td>" +
                "  </tr>" +
                "  </table>" +

                "</td>" +
                "</tr>" +

                // ---- Call to action button ----
                "<tr>" +
                "<td style='padding: 0 40px 40px 40px; text-align:center;'>" +
                "  <a href='#' style='display:inline-block; padding:16px 48px; background: linear-gradient(135deg, #e94560, #c73652); color:#ffffff; text-decoration:none; border-radius:50px; font-size:16px; font-weight:700; letter-spacing:0.5px;'>Explore StyleSmart →</a>" +
                "</td>" +
                "</tr>" +

                // ---- Footer ----
                "<tr>" +
                "<td style='background-color:#f7fafc; padding:32px 40px; text-align:center; border-top:1px solid #e2e8f0;'>" +
                "  <p style='margin:0 0 8px 0; font-size:13px; font-weight:700; color:#1a1a2e;'>StyleSmart</p>" +
                "  <p style='margin:0 0 16px 0; font-size:12px; color:#a0aec0;'>Your personal AI fashion assistant</p>" +
                "  <p style='margin:0; font-size:11px; color:#cbd5e0;'>You're receiving this because you signed up for StyleSmart weekly digest notifications.<br>" +
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
