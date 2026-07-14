# SmartRecrute AI Service

AI-powered recruitment features using local Ollama.

## Setup

```bash
cd ai-service
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
```

## Run

```bash
uvicorn app:app --host 0.0.0.0 --port 8001
```

## AI Model

- **Provider:** Ollama (local)
- **Model:** llama3.2

## Folder Structure

```
ai-service/
├── app.py                      # FastAPI app entry point
├── requirements.txt             # Python dependencies
├── README.md                    # This file
├── routes/
│   └── ai.py                   # API route definitions
└── services/
    └── ollama_service.py       # Ollama LLM integration
```

## API Endpoints

| Endpoint | Description |
|----------|------------|
| `POST /api/ai/generate-job-description` | Generate job description from title + skills |
| `POST /api/ai/generate-interview-questions` | Generate 5 interview questions |
| `POST /api/ai/explain-score` | Explain why candidate scored low |
| `POST /api/ai/generate-offer` | Generate complete offer from title |
| `GET /api/ai/health` | Health check |

## Environment

No API key required. Make sure Ollama is running locally (`ollama serve`) with the `llama3.2` model pulled (`ollama pull llama3.2`).
