package com.katibaai.backend.ai.retrieval;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RetrievedChunk {
    Integer chapterNumber;
    String chapterTitle;
    Integer partNumber;
    String partTitle;
    Integer sectionNumber;
    String sectionTitle;
    String articleNumber;
    String content;
    double distance;
}