package com.blogapp.admin.controller;

import com.blogapp.payment.config.PricingConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/pricing")
@RequiredArgsConstructor
@Tag(name = "Admin Pricing", description = "Manage pricing configuration")
public class AdminPricingController {

    private final PricingConfig pricingConfig;

    @GetMapping
    @Operation(summary = "Get current pricing config")
    public ResponseEntity<Map<String, Object>> getPricing() {
        return ResponseEntity.ok(getPricingMap());
    }

    @PutMapping
    @Operation(summary = "Update pricing config", description = "Update base prices and discount percentages")
    public ResponseEntity<Map<String, Object>> updatePricing(@RequestBody Map<String, Object> body) {
        if (body.containsKey("perBlogPaise")) {
            pricingConfig.setPerBlogPaise(((Number) body.get("perBlogPaise")).longValue());
        }
        if (body.containsKey("subsectionMonthlyPaise")) {
            pricingConfig.setSubsectionMonthlyPaise(((Number) body.get("subsectionMonthlyPaise")).longValue());
        }
        if (body.containsKey("sectionMonthlyPaise")) {
            pricingConfig.setSectionMonthlyPaise(((Number) body.get("sectionMonthlyPaise")).longValue());
        }
        if (body.containsKey("allAccessMonthlyPaise")) {
            pricingConfig.setAllAccessMonthlyPaise(((Number) body.get("allAccessMonthlyPaise")).longValue());
        }
        if (body.containsKey("durationDiscounts")) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> discounts = (Map<String, Integer>) body.get("durationDiscounts");
            pricingConfig.setDurationDiscounts(discounts);
        }
        return ResponseEntity.ok(getPricingMap());
    }

    private Map<String, Object> getPricingMap() {
        return Map.of(
                "perBlogPaise", pricingConfig.getPerBlogPaise(),
                "subsectionMonthlyPaise", pricingConfig.getSubsectionMonthlyPaise(),
                "sectionMonthlyPaise", pricingConfig.getSectionMonthlyPaise(),
                "allAccessMonthlyPaise", pricingConfig.getAllAccessMonthlyPaise(),
                "durationDiscounts", pricingConfig.getDurationDiscounts());
    }
}
