package com.blogapp.otp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Send an HTML email.
     */
    public void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            helper.setFrom("sales.webarya@gmail.com");

            mailSender.send(message);
            log.info("Email sent to {} — subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            // Don't throw — log and continue (OTP is still saved)
            log.warn("Email delivery failed but OTP has been stored. For development, check logs for OTP.");
        }
    }

    /**
     * Send blog approval notification email.
     */
    public void sendApprovalEmail(String to, String blogTitle, String blogLink) {
        String subject = "Your blog is now live!";
        String body = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>🎉 Congratulations!</h2>
                    <p>Your blog <strong>"%s"</strong> has been approved and published.</p>
                    <p><a href="%s" style="color: #4A90D9;">View your blog</a></p>
                    <p>Thank you for your contribution!</p>
                </body>
                </html>
                """.formatted(blogTitle, blogLink);
        sendEmail(to, subject, body);
    }

    /**
     * Send blog rejection notification email.
     */
    public void sendRejectionEmail(String to, String blogTitle, String reason) {
        String subject = "Update on your blog submission";
        String body = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>Blog Submission Update</h2>
                    <p>We've reviewed your blog <strong>"%s"</strong>.</p>
                    <p>Unfortunately, it was not approved for the following reason:</p>
                    <blockquote style="border-left: 4px solid #E74C3C; padding: 10px; color: #555;">%s</blockquote>
                    <p>You are welcome to update and resubmit your blog.</p>
                </body>
                </html>
                """.formatted(blogTitle, reason);
        sendEmail(to, subject, body);
    }

    /**
     * Send submission received confirmation email.
     */
    public void sendSubmissionReceivedEmail(String to, String blogTitle, String referenceId) {
        String subject = "We received your blog submission";
        String body = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>Blog Submitted!</h2>
                    <p>Thanks! Your blog <strong>"%s"</strong> has been submitted and is awaiting admin approval.</p>
                    <p>Reference ID: <strong>%s</strong></p>
                    <p>You will receive an email once it has been reviewed.</p>
                </body>
                </html>
                """.formatted(blogTitle, referenceId);
        sendEmail(to, subject, body);
    }

    /**
     * Send new blog notification to subscribers.
     */
    public void sendNewBlogNotification(String to, String subscriberName, java.util.List<BlogInfo> newBlogs,
            String frontendUrl) {
        String subject = "🆕 New blogs published on BlogPost!";

        StringBuilder blogListHtml = new StringBuilder();
        for (BlogInfo blog : newBlogs) {
            String blogLink = frontendUrl + "/blogs/" + blog.slug();
            blogListHtml
                    .append("""
                            <tr>
                                <td style="padding: 16px 20px; border-bottom: 1px solid #f0f0f0;">
                                    <h3 style="margin: 0 0 6px 0; font-size: 16px; color: #1a1a2e;">%s</h3>
                                    <p style="margin: 0 0 10px 0; font-size: 13px; color: #666; line-height: 1.4;">%s</p>
                                    <a href="%s" style="display: inline-block; padding: 8px 20px; background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; text-decoration: none; border-radius: 6px; font-size: 13px; font-weight: 600;">Read Now →</a>
                                </td>
                            </tr>
                            """
                            .formatted(blog.title(), blog.excerpt() != null ? blog.excerpt() : "", blogLink));
        }

        String greeting = (subscriberName != null && !subscriberName.isBlank())
                ? subscriberName
                : "there";

        String body = """
                <html>
                <body style="margin: 0; padding: 0; background-color: #f5f5f5; font-family: 'Segoe UI', Arial, sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="max-width: 600px; margin: 30px auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 12px rgba(0,0,0,0.08);">
                        <tr>
                            <td style="background: linear-gradient(135deg, #667eea, #764ba2); padding: 30px 24px; text-align: center;">
                                <h1 style="margin: 0; color: #ffffff; font-size: 22px;">📝 New on BlogPost</h1>
                                <p style="margin: 8px 0 0; color: rgba(255,255,255,0.85); font-size: 14px;">Fresh content just for you</p>
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 24px 20px 8px;">
                                <p style="margin: 0; font-size: 15px; color: #333;">Hey %s 👋</p>
                                <p style="margin: 8px 0 16px; font-size: 14px; color: #555;">We have %d new blog(s) published recently:</p>
                            </td>
                        </tr>
                        %s
                        <tr>
                            <td style="padding: 20px 24px; text-align: center; color: #999; font-size: 12px; border-top: 1px solid #eee;">
                                <p style="margin: 0;">You're receiving this because you subscribed to BlogPost.</p>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(greeting, newBlogs.size(), blogListHtml.toString());

        sendEmail(to, subject, body);
    }

    /**
     * Blog info record for notification emails.
     */
    public record BlogInfo(String title, String slug, String excerpt) {
    }
}
