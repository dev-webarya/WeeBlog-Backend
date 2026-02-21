package com.blogapp.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SlugUtil {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-{2,}");

    private SlugUtil() {
        // Utility class — prevent instantiation
    }

    /**
     * Generate a URL-friendly slug from the given input text.
     * Example: "How to Prepare for IGCSE Physics!" →
     * "how-to-prepare-for-igcse-physics"
     */
    public static String generateSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = WHITESPACE.matcher(normalized).replaceAll("-");
        slug = NON_LATIN.matcher(slug).replaceAll("");
        slug = MULTIPLE_DASHES.matcher(slug).replaceAll("-");
        slug = slug.toLowerCase(Locale.ENGLISH);
        slug = slug.replaceAll("^-|-$", ""); // trim leading/trailing dashes
        return slug;
    }

    /**
     * Generate a unique slug by appending a timestamp suffix.
     */
    public static String generateUniqueSlug(String input) {
        String base = generateSlug(input);
        return base + "-" + System.currentTimeMillis();
    }
}
