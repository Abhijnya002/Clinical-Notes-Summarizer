# Clinical Notes Summarizer

A full-stack demo that turns a free-text clinical note into a structured
summary (chief complaint, HPI, assessment, plan, medications, follow-up)
using a locally-run, open-source LLM. Built as a portfolio project to show
prompt engineering, structured-output validation, and HIPAA-aware design
decisions end to end -- from the React UI down to the model call.

> **This is a demo, not a certified clinical or HIPAA-compliant system.**
> Do not enter real patient information. See [HIPAA & Privacy Notes](#hipaa--privacy-notes)
> below for what a real deployment would need on top of this.

## Why an open-source, local LLM?

The backend talks to [Ollama](https://ollama.com), which runs open-source
models (default: Meta's **Llama 3.2**) entirely on your own machine:

- No API keys, no per-request cost, no third-party data sharing.
- Clinical text never leaves your machine -- important for anything
  PHI-adjacent, even in a demo.
- Ollama exposes a single, simple HTTP endpoint (`/api/generate`), which
  keeps the integration code small and easy to follow.

Swapping in a different model is a one-line config change (`OLLAMA_MODEL`),
and swapping the whole provider (e.g. for AWS Bedrock in production) only
touches `OllamaClientService`.

## Architecture

```
┌─────────────────────┐      HTTP/JSON      ┌───────────────────────┐      HTTP/JSON      ┌─────────────┐
│  React + TypeScript │ ───────────────────▶ │   Spring Boot backend │ ───────────────────▶ │   Ollama    │
│  (Vite dev server)  │ ◀─────────────────── │  (validation, usage   │ ◀─────────────────── │ (Llama 3.2) │
└─────────────────────┘                      │   monitoring)         │                      └─────────────┘
                                              └───────────────────────┘
```

1. The UI sends the raw note text to `POST /api/summarize`.
2. The backend builds a prompt (`PromptBuilder`) instructing the model to
   return **only** a JSON object with a fixed schema, using Ollama's
   `format: "json"` mode.
3. The model's JSON is parsed into a `ClinicalSummary` record and run through
   `SummaryValidator`, which checks that clinically required fields
   (chief complaint, assessment, plan) are actually present and non-blank --
   the model's output is never trusted blindly.
4. `UsageMonitor` records aggregate, non-PHI counters (request count,
   success/failure, average latency) for basic operational visibility.
5. The frontend renders the structured summary, surfacing a warning banner
   if validation failed.

## Tech stack

| Layer      | Technology                                      |
|------------|--------------------------------------------------|
| Frontend   | React 18, TypeScript, Vite                        |
| Backend    | Java 17, Spring Boot 3 (Web, Validation, Actuator) |
| LLM        | Ollama running Llama 3.2 (open source, local)      |
| Packaging  | Docker / docker-compose, GitHub Actions CI          |

## Project structure

```
.
├── backend/            Spring Boot API (Maven)
│   └── src/main/java/com/clinicalnotes/summarizer/
│       ├── controller/  REST endpoints
│       ├── service/     Prompt building, Ollama client, validation, usage
│       ├── dto/         Request/response records
│       ├── config/      CORS, LLM properties, RestClient config
│       └── exception/   Error handling
├── frontend/            React + TypeScript UI (Vite)
│   └── src/
│       ├── components/  NoteInput, SummaryCard, UsageBadge
│       └── api/         Backend client
├── docker-compose.yml
└── .github/workflows/ci.yml
```

## Prerequisites

- [Ollama](https://ollama.com/download) installed and running
- Node.js 20+ and npm
- Java 17+ (or just use the included Maven wrapper, `./mvnw`)

## Running locally

**1. Start Ollama and pull a model:**

```bash
ollama serve            # if it isn't already running as a background service
ollama pull llama3.2
```

**2. Start the backend** (default port `8080`):

```bash
cd backend
./mvnw spring-boot:run
```

**3. Start the frontend** (default port `5173`):

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173, click **"Load sample note"** (a fully synthetic
example -- no real patient data), and click **Summarize note**.

### Configuration

Backend config lives in `backend/src/main/resources/application.yml` and can
be overridden with environment variables:

| Variable           | Default                  | Purpose                          |
|--------------------|---------------------------|-----------------------------------|
| `OLLAMA_BASE_URL`  | `http://localhost:11434`  | Where Ollama is listening         |
| `OLLAMA_MODEL`     | `llama3.2`                | Which pulled model to use         |

Frontend config lives in `frontend/.env` (copy from `.env.example`):

| Variable              | Default                 | Purpose                  |
|-----------------------|---------------------------|---------------------------|
| `VITE_API_BASE_URL`   | `http://localhost:8080`  | Backend API base URL      |

### Running with Docker

```bash
docker compose up --build
```

This builds and runs the backend and frontend containers. Ollama is expected
to run on the **host** machine (not in a container) so model weights don't
need to be re-downloaded on every image rebuild; the backend container
reaches it via `host.docker.internal`.

## API

### `POST /api/summarize`

Request:
```json
{ "clinicalNote": "Patient presents with..." }
```

Response:
```json
{
  "summary": {
    "chiefComplaint": "...",
    "historyOfPresentIllness": "...",
    "assessment": "...",
    "plan": "...",
    "medications": ["..."],
    "followUp": "..."
  },
  "validated": true,
  "validationWarnings": [],
  "rawModelOutput": null
}
```

Errors: `400` for a blank/oversized note, `503` if Ollama isn't reachable,
`502` if the model's output couldn't be parsed as valid structured JSON.

### `GET /api/usage`

Returns aggregate, non-PHI usage counters:
```json
{
  "totalRequests": 12,
  "successfulRequests": 11,
  "failedRequests": 1,
  "averageLatencyMs": 842.3,
  "lastRequestEpochMillis": 1753185600000
}
```

## HIPAA & Privacy Notes

This project is built with HIPAA-*aware* habits, but is **not** a
HIPAA-compliant product out of the box. What it does today:

- Clinical text is processed in memory only -- nothing is written to a
  database, file, or third-party API.
- Application logs record only note length, timing, and validation
  status -- never note content or raw model output.
- The LLM runs locally via Ollama, so no clinical text is sent to an
  external vendor.
- The UI includes an explicit banner reminding users not to paste real
  patient data, and the sample note is entirely synthetic.
- The model is explicitly prompted not to fabricate facts and not to
  reproduce direct identifiers (names, DOBs) even if present in the input.

What a real HIPAA-compliant deployment would additionally need:

- A signed Business Associate Agreement (BAA) with every vendor in the
  data path (cloud host, LLM provider if not self-hosted, etc.).
- Encryption in transit (TLS) and at rest for any persisted data.
- Authentication, authorization, and audit logging for every access to
  patient data.
- A formal de-identification/redaction step before text reaches any LLM,
  rather than relying on prompt instructions alone.
- Signed data retention and breach-notification policies.

## Cloud Deployment (AWS)

The app is packaged as two independent Docker images, which maps cleanly
onto a straightforward AWS layout:

- **Frontend**: static build (`npm run build`) served from **S3 +
  CloudFront**.
- **Backend**: the `backend/Dockerfile` image pushed to **ECR** and run on
  **ECS Fargate** behind an **ALB**, with config (`OLLAMA_BASE_URL`, etc.)
  supplied via environment variables / **Secrets Manager**.
- **LLM in production**: swap `OllamaClientService`'s target for **Amazon
  Bedrock** (or a GPU-backed ECS service running Ollama) when a
  local-machine LLM isn't an option -- the rest of the pipeline (prompt
  building, validation, usage monitoring) is unchanged.
- **Monitoring**: replace the in-memory `UsageMonitor` with Micrometer's
  CloudWatch registry so usage metrics survive across instances and
  deploys.

None of this is provisioned by this repo -- it's documented here as the
intended target architecture, not deployed infrastructure.

## Testing

```bash
cd backend && ./mvnw test
cd frontend && npm run build   # type-checks and builds
```

## License

MIT -- see [LICENSE](LICENSE).
