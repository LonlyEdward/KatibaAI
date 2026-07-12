CREATE TABLE legal_documents (
                                 id UUID PRIMARY KEY,
                                 title VARCHAR(255) NOT NULL,
                                 document_type VARCHAR(50) NOT NULL,
                                 country VARCHAR(100) NOT NULL,
                                 language VARCHAR(10) NOT NULL,
                                 version VARCHAR(50),
                                 published_date DATE,
                                 description TEXT,
                                 active BOOLEAN NOT NULL DEFAULT TRUE,
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);