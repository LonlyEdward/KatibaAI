CREATE TABLE legal_document_chunks (
   id UUID PRIMARY KEY,
   document_id UUID NOT NULL,
   chapter_number INTEGER,
   chapter_title VARCHAR(255),
   part_number INTEGER,
   part_title VARCHAR(255),
   section_number INTEGER,
   section_title VARCHAR(255),
   article_number VARCHAR(10),
   paragraph_number INTEGER,
   chunk_index INTEGER NOT NULL,
   content TEXT NOT NULL,
   embedding VECTOR(768),
   created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT fk_document_chunks_document
       FOREIGN KEY (document_id)
                                               REFERENCES legal_documents(id)
                                               ON DELETE CASCADE
);

CREATE INDEX idx_chunks_document ON legal_document_chunks(document_id);
CREATE INDEX idx_chunks_article ON legal_document_chunks(article_number);
CREATE INDEX idx_chunks_chapter ON legal_document_chunks(chapter_number);
CREATE INDEX idx_chunks_section ON legal_document_chunks(section_number);