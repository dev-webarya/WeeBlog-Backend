package com.blogapp.common.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class HtmlSanitizer {

    private HtmlSanitizer() {
        // Utility class — prevent instantiation
    }

    /**
     * Sanitize HTML content to prevent XSS while allowing safe blog formatting
     * tags.
     * Allows: headings, bold/italic, lists, links, quotes, images, code blocks.
     */
    public static String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }

        Safelist safelist = Safelist.relaxed()
                .addTags("h1", "h2", "h3", "h4", "h5", "h6", "pre", "code", "blockquote", "hr")
                .addAttributes("a", "href", "title", "target", "rel")
                .addAttributes("img", "src", "alt", "title", "width", "height")
                .addAttributes("code", "class")
                .addAttributes("pre", "class")
                .addAttributes(":all", "style")
                .addProtocols("a", "href", "http", "https", "mailto")
                .addProtocols("img", "src", "http", "https");

        return Jsoup.clean(html, safelist);
    }

    /**
     * Strip all HTML tags — returns plain text only.
     */
    public static String stripAll(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        return Jsoup.clean(html, Safelist.none());
    }
}
