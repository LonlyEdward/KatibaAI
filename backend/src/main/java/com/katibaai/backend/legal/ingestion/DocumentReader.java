package com.katibaai.backend.legal.ingestion;

import com.katibaai.backend.legal.ingestion.DocxParagraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentReader {

    public List<DocxParagraph> readDocx(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            List<DocxParagraph> result = new ArrayList<>();

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text == null || text.isBlank()) continue;

                String styleName = resolveStyleName(paragraph, document);
                Integer numId = extractNumId(paragraph);
                Integer ilvl = extractIlvl(paragraph);

                result.add(DocxParagraph.builder()
                        .styleName(styleName)
                        .text(text.trim())
                        .numId(numId)
                        .ilvl(ilvl)
                        .build());
            }

            return result;
        } catch (IOException e) {
            throw new DocumentReadException("Failed to read DOCX: " + file.getName(), e);
        }
    }

    private String resolveStyleName(XWPFParagraph paragraph, XWPFDocument document) {
        String styleId = paragraph.getStyleID();
        if (styleId == null) return "Normal";
        var style = document.getStyles().getStyle(styleId);
        return (style != null && style.getName() != null) ? style.getName() : styleId;
    }

    private Integer extractNumId(XWPFParagraph paragraph) {
        BigInteger numId = paragraph.getNumID();
        return numId != null ? numId.intValue() : null;
    }

    private Integer extractIlvl(XWPFParagraph paragraph) {
        BigInteger ilvl = paragraph.getNumIlvl();
        return ilvl != null ? ilvl.intValue() : null;
    }
}