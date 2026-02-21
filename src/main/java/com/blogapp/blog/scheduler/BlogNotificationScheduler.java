package com.blogapp.blog.scheduler;

import com.blogapp.blog.entity.BlogPost;
import com.blogapp.blog.enums.BlogStatus;
import com.blogapp.blog.repository.BlogPostRepository;
import com.blogapp.otp.service.EmailService;
import com.blogapp.subscriber.entity.Subscriber;
import com.blogapp.subscriber.enums.SubscriberStatus;
import com.blogapp.subscriber.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlogNotificationScheduler {

    private final BlogPostRepository blogPostRepository;
    private final SubscriberRepository subscriberRepository;
    private final EmailService emailService;

    @Value("${blog.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${blog.notification.enabled:false}")
    private boolean notificationEnabled;

    /**
     * Runs every hour — finds PUBLISHED blogs where emailSent = false,
     * sends a notification email to all active & verified subscribers,
     * then marks emailSent = true on those blogs.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour at minute 0
    public void notifySubscribersAboutNewBlogs() {
        if (!notificationEnabled) {
            log.debug("Blog notification scheduler is DISABLED. Set blog.notification.enabled=true to enable.");
            return;
        }

        log.info("⏰ Blog notification scheduler started...");

        // 1. Find published blogs that haven't been notified yet
        List<BlogPost> unnotifiedBlogs = blogPostRepository
                .findByStatusAndEmailSent(BlogStatus.PUBLISHED, false);

        if (unnotifiedBlogs.isEmpty()) {
            log.info("No new un-notified blogs found. Skipping.");
            return;
        }

        log.info("Found {} un-notified published blog(s). Preparing notifications...", unnotifiedBlogs.size());

        // 2. Get all active & verified subscribers
        List<Subscriber> subscribers = subscriberRepository
                .findAllByStatusAndVerified(SubscriberStatus.ACTIVE, true);

        if (subscribers.isEmpty()) {
            log.info("No active verified subscribers found. Marking blogs as notified anyway.");
            markBlogsAsEmailSent(unnotifiedBlogs);
            return;
        }

        log.info("Sending notifications to {} subscriber(s) about {} blog(s).",
                subscribers.size(), unnotifiedBlogs.size());

        // 3. Build blog info list
        List<EmailService.BlogInfo> blogInfoList = unnotifiedBlogs.stream()
                .map(blog -> new EmailService.BlogInfo(
                        blog.getTitle(),
                        blog.getSlug(),
                        blog.getExcerpt()))
                .toList();

        // 4. Send email to each subscriber
        int successCount = 0;
        int failCount = 0;

        for (Subscriber subscriber : subscribers) {
            try {
                emailService.sendNewBlogNotification(
                        subscriber.getEmail(),
                        subscriber.getName(),
                        blogInfoList,
                        frontendUrl);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("Failed to send notification to {}: {}", subscriber.getEmail(), e.getMessage());
            }
        }

        log.info("Notification emails sent: {} success, {} failed.", successCount, failCount);

        // 5. Mark all blogs as email sent
        markBlogsAsEmailSent(unnotifiedBlogs);

        log.info("✅ Blog notification scheduler completed. {} blog(s) marked as notified.", unnotifiedBlogs.size());
    }

    private void markBlogsAsEmailSent(List<BlogPost> blogs) {
        for (BlogPost blog : blogs) {
            blog.setEmailSent(true);
            blogPostRepository.save(blog);
        }
    }
}
