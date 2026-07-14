CREATE TABLE chat_sessions (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_chat_sessions_user
                                   FOREIGN KEY (user_id) REFERENCES users(id)
                                       ON DELETE CASCADE
);

CREATE TABLE messages (
                          id UUID PRIMARY KEY,
                          session_id UUID NOT NULL,
                          role VARCHAR(20) NOT NULL,
                          content TEXT NOT NULL,
                          sources TEXT,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_messages_session
                              FOREIGN KEY (session_id) REFERENCES chat_sessions(id)
                                  ON DELETE CASCADE
);

CREATE INDEX idx_chat_sessions_user ON chat_sessions(user_id);
CREATE INDEX idx_messages_session ON messages(session_id);