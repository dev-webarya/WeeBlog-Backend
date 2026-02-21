package com.blogapp.payment.controller;

import com.blogapp.payment.config.PricingConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing", description = "Public pricing information for content access plans")
public class PricingController {

    private final PricingConfig pricingConfig;

    @GetMapping
    @Operation(summary = "Get pricing plans", description = "Returns all available pricing plans with base prices and duration discounts")
    public ResponseEntity<Map<String, Object>> getPricing() {
        Map<String, Object> pricing = new LinkedHashMap<>();

        // Per-blog
        pricing.put("perBlog", Map.of(
                "label", "Single Blog Access",
                "pricePaise", pricingConfig.getPerBlogPaise(),
                "priceFormatted", formatPrice(pricingConfig.getPerBlogPaise())));

        // Subsection subscription
        pricing.put("subsection", buildSubscriptionPlan(
                "Subsection Subscription", pricingConfig.getSubsectionMonthlyPaise()));

        // Section subscription
        pricing.put("section", buildSubscriptionPlan(
                "Section Subscription", pricingConfig.getSectionMonthlyPaise()));

        // All-access subscription
        pricing.put("allAccess", buildSubscriptionPlan(
                "All-Access Subscription", pricingConfig.getAllAccessMonthlyPaise()));

        pricing.put("durationDiscounts", pricingConfig.getDurationDiscounts());

        return ResponseEntity.ok(pricing);
    }

    private Map<String, Object> buildSubscriptionPlan(String label, long monthlyPaise) {
        Map<String, Object> plan = new LinkedHashMap<>();
        plan.put("label", label);
        plan.put("monthlyPricePaise", monthlyPaise);
        plan.put("monthlyPriceFormatted", formatPrice(monthlyPaise));

        // Show effective price for each duration
        Map<String, Object> durations = new LinkedHashMap<>();
        for (var entry : pricingConfig.getDurationDiscounts().entrySet()) {
            String dur = entry.getKey();
            int discount = entry.getValue();
            int months = switch (dur) {
                case "1M" -> 1;
                case "3M" -> 3;
                case "6M" -> 6;
                case "12M" -> 12;
                default -> 1;
            };
            long total = monthlyPaise * months;
            long discounted = total - (total * discount / 100);
            durations.put(dur, Map.of(
                    "months", months,
                    "discountPercent", discount,
                    "totalPaise", discounted,
                    "totalFormatted", formatPrice(discounted),
                    "effectiveMonthlyPaise", discounted / months,
                    "effectiveMonthlyFormatted", formatPrice(discounted / months)));
        }
        plan.put("durations", durations);
        return plan;
    }

    private String formatPrice(long paise) {
        return "₹" + String.format("%.2f", paise / 100.0);
    }
}
