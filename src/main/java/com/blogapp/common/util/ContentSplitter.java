package com.blogapp.common.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Splits HTML content into two roughly equal parts by word count,
 * preserving paragraph boundaries.
 *
 * Returns a String[2]: [0] = part1 HTML, [1] = part2 HTML.
 * If content is too short to split, part2 will be empty.
 */
public final class ContentSplitter {

    private ContentSplitter() {
    }

    public static String[] split(String html) {
        if (html == null || html.isBlank()) {
            return new String[] { "", "" };
        }

        Document doc = Jsoup.parseBodyFragment(html);
        Elements children = doc.body().children();

        if (children.isEmpty()) {
            return new String[] { html, "" };
        }

        // Count total words
        int totalWords = countWords(doc.body().text());
        if (totalWords < 20) {
            // Too short to split meaningfully
            return new String[] { html, "" };
        }

        int halfWords = totalWords / 2;
        int runningWords = 0;
        int splitIndex = 0;

        for (int i = 0; i < children.size(); i++) {
            runningWords += countWords(children.get(i).text());
            if (runningWords >= halfWords) {
                // Include this paragraph in part 1
                splitIndex = i + 1;
                break;
            }
        }

        // Edge case: if splitIndex is 0, put at least one element in part1
        if (splitIndex == 0)
            splitIndex = 1;
        // Edge case: don't put everything in part1
        if (splitIndex >= children.size())
            splitIndex = children.size() - 1;

        StringBuilder part1 = new StringBuilder();
        StringBuilder part2 = new StringBuilder();

        for (int i = 0; i < children.size(); i++) {
            if (i < splitIndex) {
                part1.append(children.get(i).outerHtml());
            } else {
                part2.append(children.get(i).outerHtml());
            }
        }

        return new String[] { part1.toString(), part2.toString() };
    }

    private static int countWords(String text) {
        if (text == null || text.isBlank())
            return 0;
        return text.trim().split("\\s+").length;
    }
}
