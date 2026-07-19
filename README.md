# KatibaAI

AI powered legal assistant that uses Retrieval Augmented Generation (RAG) to answer questions about the Constitution of Tanzania, providing context-aware responses supported by relevant constitutional articles and references.

---

## Description

The Constitution of Tanzania is a long formally structured document with many Articles making it difficult to quickly find the specific provisions needed to answer real questions. KatibaAI solves this using Retrieval-Augmented Generation (RAG). It chunks the Constitution into structured sections, generates vector embeddings for semantic search, retrieves the most relevant constitutional provisions for each user query and provides them as context to a Large Language Model (LLM). The LLM then generates an answer grounded in the retrieved constitutional text while citing the relevant Article numbers.

---

## Features

- Ask questions in natural language and get answers grounded in the actual Constitution text
- Automatic source citation where every answer names the specific Article(s) it's based on
- Direct article lookup, asking "what does Article 13 say?" reliably returns Article 13, not a loosely related semantic match
- Conversation memory within a session where follow-up questions like "does it have any limits?" are understood in context
- Persistent chat history meaning logged-in users can maintain multiple separate conversations and revisit them later
- Secure authentication using JWT-based login with short-lived access tokens and rotating refresh tokens
- GraphQL API as a single, typed endpoint for all chat functionality

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend language / framework | Java 21, Spring Boot 3.4.5 |
| Frontend | React (Vite) |
| API | GraphQL (chat features), REST (authentication) |
| Database | PostgreSQL 16 with the pgvector extension |
| Database migrations | Flyway |
| Authentication | JWT + Spring Security |
| Chat model | Qwen2.5 (via Ollama, local inference) |
| Embedding model | nomic-embed-text (via Ollama, 768 dimensions) |
| Document parsing | Apache POI |
| Database Migrations | Flyway |
| ORM | Spring Data JPA / Hibernate |
| Local infrastructure | Docker Compose (PostgreSQL + Ollama) |

---
# Architecture

```text
                User
                  │
                  ▼
          React Frontend
                  │
         GraphQL / REST APIs
                  │
                  ▼
        Spring Boot Backend
                  │
      ┌───────────┴────────────┐
      │                        │
      ▼                        ▼
 PostgreSQL + pgvector     Ollama (LLM)
      │                        │
      └───────────┬────────────┘
                  ▼
      Retrieval-Augmented Generation
                  ▼
          Grounded AI Response
```

## How It Works

**Preparation (done once):** The Constitution, as a structured Word document, is parsed into individual Articles, each broken into appropriately sized chunks and converted into a vector embedding. These are stored in PostgreSQL alongside the original text.

**At question time:**
1. The user's question is checked for an explicit Article reference (e.g. "Article 18"), if found, that Article is fetched directly.
2. The question is also converted into a vector embedding and compared against every stored chunk using cosine similarity, surfacing the most conceptually relevant Articles.
3. Both result sets are combined (duplicates removed) and handed to the language model, along with any recent conversation history for context.
4. The model is instructed to answer only using the provided excerpts and to cite the Article number(s) it used. If the excerpts don't contain enough information, it says so rather than guessing.
5. The question and answer are saved to the user's chat session for future reference.

---

## Installation

### Prerequisites

- Java 21
- Node.js (for the frontend)
- Docker + Docker Compose

### Steps

1. Clone the repository:
   ```bash
   git clone git@github.com:LonlyEdward/KatibaAI.git
   cd KatibaAI
   ```

2. Create a `.env` file in the project root:
   ```env
   POSTGRES_DB=katibaai
   POSTGRES_USER=katibauser
   POSTGRES_PASSWORD=yourpass

   JWT_SECRET=              # generate with: openssl rand -base64 32
   JWT_EXPIRY_MINUTES=15
   JWT_REFRESH_DAYS=7

   OLLAMA_MODEL=qwen2.5:3b
   CORS_ALLOWED_ORIGINS=http://localhost:5173
   ```

3. Start PostgreSQL and Ollama

   ```bash
   docker compose up -d
   ```

4. Pull the required models into the Ollama container

   ```bash
   docker exec -it katiba-ollama ollama pull nomic-embed-text
   docker exec -it katiba-ollama ollama pull qwen2.5:3b
   ```

5. Run the backend

   ```bash
   set -a
   source ../.env
   set +a
   ./mvnw spring-boot:run
   ```

  This applies all database migrations automatically on startup.


6. Ingest the Constitution (one-time)

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=ingest
   ```

7. Run the frontend

   ```bash
   cd ../frontend
   npm install
   npm run dev
   ```

---


## Usage

### Register and log in


### Ask a question


### Continue the conversation


### View past conversations

---

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

---

## Disclaimer

KatibaAI is an independent, AI-powered informational tool. It is not a substitute for professional legal advice and its answers should not be relied upon for legal decisions of any kind. While the system is designed to ground its responses in the actual text of the Constitution and to cite its sources, language models can still misinterpret text, omit relevant context or make mistakes. Always consult a qualified legal professional or refer directly to the official Constitution for any matter of legal significance.

This project is not affiliated with, endorsed by or officially connected to the Government of the United Republic of Tanzania.