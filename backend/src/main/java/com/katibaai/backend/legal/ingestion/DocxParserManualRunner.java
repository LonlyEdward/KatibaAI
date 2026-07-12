package com.katibaai.backend.legal.ingestion;

import com.katibaai.backend.legal.ingestion.DocxParagraph;
import com.katibaai.backend.legal.ingestion.ParsedChunk;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DocxParserManualRunner {

    public static void main(String[] args) throws Exception {
        DocumentReader reader = new DocumentReader();
        LegalStructureParser parser = new LegalStructureParser();

        File docx = new File("src/main/resources/documents/constitution.docx");

        List<DocxParagraph> paragraphs = reader.readDocx(docx);
        System.out.println("Total paragraphs read: " + paragraphs.size());

        List<ParsedChunk> chunks = parser.parse(paragraphs);
        System.out.println("Total articles parsed: " + chunks.size());

        StringBuilder output = new StringBuilder();
        for (ParsedChunk chunk : chunks) {
            output.append("=== Article ").append(chunk.getArticleNumber())
                    .append(" | Chapter ").append(chunk.getChapterNumber())
                    .append(" (").append(chunk.getChapterTitle()).append(")")
                    .append(" | Part ").append(chunk.getPartNumber())
                    .append(" (").append(chunk.getPartTitle()).append(")")
                    .append(" | Section ").append(chunk.getSectionNumber())
                    .append(" (").append(chunk.getSectionTitle()).append(")")
                    .append(" ===\n")
                    .append(chunk.getContent())
                    .append("\n\n");
        }

        Path outputPath = Path.of("parsed-articles.txt");
        Files.writeString(outputPath, output.toString());
        System.out.println("Full output written to: " + outputPath.toAbsolutePath());

        // Print first 5 articles to console for a quick look
        for (int i = 0; i < Math.min(5, chunks.size()); i++) {
            ParsedChunk c = chunks.get(i);
            System.out.println("\n--- Article " + c.getArticleNumber() + ": " + c.getContent().substring(0, Math.min(150, c.getContent().length())) + "...");
        }
    }
}