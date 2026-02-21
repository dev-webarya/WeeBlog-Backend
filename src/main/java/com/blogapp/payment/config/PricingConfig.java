package com.blogapp.payment.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "pricing")
@Schema(description = "Pricing configuration for content access plans")
public class PricingConfig {

    // Base prices in paise (₹1 = 100 paise)
    private long perBlogPaise = 4900; // ₹49
    private long subsectionMonthlyPaise = 19900; // ₹199/month
    private long sectionMonthlyPaise = 49900; // ₹499/month
    private long allAccessMonthlyPaise = 99900; // ₹999/month

    // Duration discount percentages
    private Map<String, Integer> durationDiscounts = Map.of(
            "1M", 0,
            "3M", 20,
            "6M", 35,
            "12M", 50);

    /**
     * Calculate the final price in paise for a given plan.
     */
    public long calculatePrice(String planType, String duration) {
        long baseMonthly = switch (planType) {
            case "PER_BLOG" -> perBlogPaise;
            case "SUBSCRIPTION_SUBSECTION" -> subsectionMonthlyPaise;
            case "SUBSCRIPTION_SECTION" -> sectionMonthlyPaise;
            case "SUBSCRIPTION_ALL" -> allAccessMonthlyPaise;
            default -> throw new IllegalArgumentException("Unknown plan: " + planType);
        };

        if ("PER_BLOG".equals(planType)) {
            return baseMonthly; // No duration discount for single blog purchases
        }

        int months = parseMonths(duration);
        int discountPercent = durationDiscounts.getOrDefault(duration, 0);
        long totalBeforeDiscount = baseMonthly * months;
        return totalBeforeDiscount - (totalBeforeDiscount * discountPercent / 100);
    }

    private int parseMonths(String duration) {
        if (duration == null)
            return 1;
        return switch (duration) {
            case "1M" -> 1;
            case "3M" -> 3;
            case "6M" -> 6;
            case "12M" -> 12;
            default -> 1;
        };
    }
}
