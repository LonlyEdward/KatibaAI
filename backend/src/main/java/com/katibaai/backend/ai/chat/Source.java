package com.katibaai.backend.ai.chat;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Source {
    String articleNumber;
    String chapterTitle;
    double distance;
}