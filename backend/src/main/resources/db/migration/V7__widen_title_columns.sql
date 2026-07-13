ALTER TABLE legal_document_chunks
    ALTER COLUMN chapter_title TYPE VARCHAR(500),
    ALTER COLUMN part_title TYPE VARCHAR(500),
    ALTER COLUMN section_title TYPE VARCHAR(500);