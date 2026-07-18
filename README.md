# KatibaAI

AI powered legal assistant that uses Retrieval Augmented Generation (RAG) to answer questions about the Constitution of Tanzania, providing context aware responses supported by relevant constitutional articles and references.

---

## Description

KatibaAI streamlines access to the Tanzanian Constitution's Articles by employing a retrieval-augmented generation (RAG) pipeline. The system pre-processes and chunks the constitutional text into segments while preserving hierarchical metadata which are then converted into vector embeddings. When a user submits a natural language query, a semantic similarity search identifies and retrieves the most relevant text segments. These contexts are passed to a Large Language Model (LLM) constrained by strict system prompts, forcing the model to synthesize answers exclusively from the retrieved text and generate precise, verifiable Article citations while preventing hallucinations.

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
| Language / Framework | Java 21, Spring Boot 3.4.5 |
| Frontend | React |
| API | GraphQL (chat features), REST (authentication) |
| Database | PostgreSQL 16 with the pgvector extension |
| Database migrations | Flyway |
| Authentication | JWT + Spring Security |
| Chat model | Qwen2.5 (via Ollama, local inference) |
| Embedding model | nomic-embed-text (via Ollama, 768 dimensions) |
| Document parsing | Apache POI |
| Tokenization | jtokkit |
| ORM | Spring Data JPA / Hibernate |

---

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
- PostgreSQL 16+ with the `pgvector` extension enabled
- Ollama with the required models pulled:
  ```bash
  ollama pull nomic-embed-text
  ollama pull qwen2.5:3b
  ```

### Steps

1. Clone the repository:
   ```bash
   git clone git@github.com:LonlyEdward/KatibaAI.git
   cd KatibaAI/backend
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

3. Load the environment variables and run the app:
   ```bash
   set -a
   source .env
   set +a
   ./mvnw spring-boot:run
   ```

   This applies all database migrations automatically on startup.

4. Ingest the Constitution (one time, populates the database with searchable content):
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=ingest
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