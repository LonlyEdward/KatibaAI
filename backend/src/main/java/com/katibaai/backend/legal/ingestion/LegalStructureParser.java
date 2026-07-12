package com.katibaai.backend.legal.ingestion;

import com.katibaai.backend.legal.ingestion.DocxParagraph;
import com.katibaai.backend.legal.ingestion.ParsedChunk;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LegalStructureParser {

    private static final Pattern CHAPTER_PATTERN =
            Pattern.compile("^CHAPTER\\s+(\\d+)\\.\\s*(.*)$", Pattern.CASE_INSENSITIVE);

    private static final Pattern PART_PATTERN =
            Pattern.compile("^PART\\s+([IVXLCDM]+)\\.\\s*(.*)$", Pattern.CASE_INSENSITIVE);

    private static final Pattern SECTION_PATTERN =
            Pattern.compile("^Section\\s+(\\d+)\\.\\s*(.*)$", Pattern.CASE_INSENSITIVE);

    private static final Pattern EXPLICIT_ARTICLE_PATTERN =
            Pattern.compile("^(\\d+[A-Z])\\.\\s*(.*)$");

    public List<ParsedChunk> parse(List<DocxParagraph> paragraphs) {
        List<ParsedChunk> chunks = new ArrayList<>();

        Integer currentChapterNumber = null;
        String currentChapterTitle = null;
        Integer currentPartNumber = null;
        String currentPartTitle = null;
        Integer currentSectionNumber = null;
        String currentSectionTitle = null;

        String currentArticleNumber = null;
        String currentArticleTitle = null;
        StringBuilder currentContent = new StringBuilder();
        int articleCounter = 0;
        int subCounter = 0;
        int chunkIndex = 0;

        boolean startedBody = false;

        for (DocxParagraph p : paragraphs) {
            String style = p.getStyleName();
            String text = p.getText();

            if ("Normal".equalsIgnoreCase(style)) {
                continue;
            }

            if (isChapterHeading(style, text)) {
                startedBody = true;
                flushArticle(chunks, currentChapterNumber, currentChapterTitle,
                        currentPartNumber, currentPartTitle, currentSectionNumber, currentSectionTitle,
                        currentArticleNumber, currentArticleTitle, currentContent, chunkIndex);
                currentContent = new StringBuilder();
                currentArticleNumber = null;
                currentArticleTitle = null;

                Matcher m = CHAPTER_PATTERN.matcher(text);
                if (m.matches()) {
                    currentChapterNumber = Integer.parseInt(m.group(1));
                    currentChapterTitle = m.group(2).trim();
                }
                currentPartNumber = null;
                currentPartTitle = null;
                currentSectionNumber = null;
                currentSectionTitle = null;
                continue;
            }

            if (!startedBody) continue;

            if (isPartHeading(style, text)) {
                flushArticle(chunks, currentChapterNumber, currentChapterTitle,
                        currentPartNumber, currentPartTitle, currentSectionNumber, currentSectionTitle,
                        currentArticleNumber, currentArticleTitle, currentContent, chunkIndex);
                currentContent = new StringBuilder();
                currentArticleNumber = null;
                currentArticleTitle = null;

                Matcher m = PART_PATTERN.matcher(text);
                if (m.matches()) {
                    currentPartNumber = romanToInt(m.group(1));
                    currentPartTitle = m.group(2).trim();
                }
                currentSectionNumber = null;
                currentSectionTitle = null;
                continue;
            }

            if (isSectionHeading(style, text)) {
                flushArticle(chunks, currentChapterNumber, currentChapterTitle,
                        currentPartNumber, currentPartTitle, currentSectionNumber, currentSectionTitle,
                        currentArticleNumber, currentArticleTitle, currentContent, chunkIndex);
                currentContent = new StringBuilder();
                currentArticleNumber = null;
                currentArticleTitle = null;

                Matcher m = SECTION_PATTERN.matcher(text);
                if (m.matches()) {
                    currentSectionNumber = Integer.parseInt(m.group(1));
                    currentSectionTitle = m.group(2).trim();
                }
                continue;
            }

            boolean isMainArticle = p.getNumId() != null && p.getNumId() == 2
                    && p.getIlvl() != null && p.getIlvl() == 0;

            boolean isExplicitLettered = p.getNumId() == null
                    && "Heading 4".equalsIgnoreCase(style)
                    && Pattern.compile("^\\d+[A-Z]\\.").matcher(text).find();

            boolean isArticleTitle = isMainArticle || isExplicitLettered;

            if (isArticleTitle) {
                flushArticle(chunks, currentChapterNumber, currentChapterTitle,
                        currentPartNumber, currentPartTitle, currentSectionNumber, currentSectionTitle,
                        currentArticleNumber, currentArticleTitle, currentContent, chunkIndex);
                if (!chunks.isEmpty()) chunkIndex++;
                currentContent = new StringBuilder();
                subCounter = 0;

                if (isExplicitLettered) {
                    Matcher explicit = EXPLICIT_ARTICLE_PATTERN.matcher(text);
                    if (explicit.matches()) {
                        currentArticleNumber = explicit.group(1);
                        currentArticleTitle = explicit.group(2).trim();
                    }
                } else {
                    articleCounter++;
                    currentArticleNumber = String.valueOf(articleCounter);
                    currentArticleTitle = text;
                }
                continue;
            }

            if (p.getIlvl() != null && p.getIlvl() == 1) {
                subCounter++;
                currentContent.append(subCounter).append(". ").append(text).append(" ");
            } else if (p.getIlvl() != null && p.getIlvl() == 2) {
                currentContent.append(text).append(" ");
            } else {
                currentContent.append(text).append(" ");
            }
        }

        flushArticle(chunks, currentChapterNumber, currentChapterTitle,
                currentPartNumber, currentPartTitle, currentSectionNumber, currentSectionTitle,
                currentArticleNumber, currentArticleTitle, currentContent, chunkIndex);

        return chunks;
    }

    private void flushArticle(List<ParsedChunk> chunks,
                              Integer chapterNumber, String chapterTitle,
                              Integer partNumber, String partTitle,
                              Integer sectionNumber, String sectionTitle,
                              String articleNumber, String articleTitle,
                              StringBuilder content, int chunkIndex) {
        if (articleNumber == null) return;

        String bodyText = content.toString().trim();
        String fullContent = bodyText.isEmpty()
                ? articleTitle
                : articleTitle + ". " + bodyText;

        chunks.add(ParsedChunk.builder()
                .chapterNumber(chapterNumber)
                .chapterTitle(chapterTitle)
                .partNumber(partNumber)
                .partTitle(partTitle)
                .sectionNumber(sectionNumber)
                .sectionTitle(sectionTitle)
                .articleNumber(articleNumber)
                .paragraphNumber(null)
                .chunkIndex(chunkIndex)
                .content(fullContent)
                .build());
    }

    private boolean isChapterHeading(String style, String text) {
        return "Heading 1".equalsIgnoreCase(style) && CHAPTER_PATTERN.matcher(text).matches();
    }

    private boolean isPartHeading(String style, String text) {
        return "Heading 2".equalsIgnoreCase(style) && PART_PATTERN.matcher(text).matches();
    }

    private boolean isSectionHeading(String style, String text) {
        return SECTION_PATTERN.matcher(text).matches();
    }

    private int romanToInt(String roman) {
        int[] values = new int[roman.length()];
        for (int i = 0; i < roman.length(); i++) {
            switch (roman.charAt(i)) {
                case 'I' -> values[i] = 1;
                case 'V' -> values[i] = 5;
                case 'X' -> values[i] = 10;
                case 'L' -> values[i] = 50;
                case 'C' -> values[i] = 100;
                case 'D' -> values[i] = 500;
                case 'M' -> values[i] = 1000;
            }
        }
        int result = 0;
        for (int i = 0; i < values.length; i++) {
            if (i + 1 < values.length && values[i] < values[i + 1]) {
                result -= values[i];
            } else {
                result += values[i];
            }
        }
        return result;
    }
}