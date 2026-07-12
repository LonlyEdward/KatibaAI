package com.katibaai.backend.legal.ingestion;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Profile("ingest")
@RequiredArgsConstructor
public class ConstitutionIngestRunner implements CommandLineRunner {

    private final ConstitutionImporter importer;

    @Override
    public void run(String... args) {
        File docx = new File("src/main/resources/documents/constitution.docx");
        importer.importConstitution(docx);
    }
}