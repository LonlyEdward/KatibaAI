package com.katibaai.backend.legal.ingestion;

public class DocumentReadException extends RuntimeException {
    public DocumentReadException(String message, Throwable cause) {
        super(message, cause);
    }
}