package com.katibaai.backend.legal.ingestion;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ParsedChunk {
    Integer chapterNumber;
    String chapterTitle;
    Integer partNumber;
    String partTitle;
    Integer sectionNumber;
    String sectionTitle;
    String articleNumber;
    Integer paragraphNumber;
    Integer chunkIndex;
    String content;
}