package com.katibaai.backend.legal.ingestion;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DocxParagraph {
    String styleName;
    String text;
    Integer numId;
    Integer ilvl;
}