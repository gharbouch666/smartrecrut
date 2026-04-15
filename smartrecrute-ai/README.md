# SmartRecrute AI Service

AI-powered recruitment features using Google Gemini API.

## Setup

```bash
cd smartrecrute-ai
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
```

## Run

```bash
uvicorn main:app --host 0.0.0.0 --port 8001
```

## API Endpoints

| Endpoint | Description |
|----------|------------|
| `POST /api/ai/generate-job-description` | Generate job description from title + skills |
| `POST /api/ai/generate-interview-questions` | Generate 5 interview questions |
| `POST /api/ai/explain-score` | Explain why candidate scored low |
| `POST /api/ai/generate-offer` | Generate complete offer from title |
| `GET /api/ai/health` | Health check |

## Example Requests

### Generate Job Description
```bash
curl -X POST http://localhost:8001/api/ai/generate-job-description \
  -H "Content-Type: application/json" \
  -d '{"title":"Fullstack Developer","skills":["React","Node.js","Python"],"experience":"Mid-level"}'
```

### Generate Interview Questions
```bash
curl -X POST http://localhost:8001/api/ai/generate-interview-questions \
  -H "Content-Type: application/json" \
  -d '{"candidate_skills":["Java","Spring"],"missing_skills":["AWS"],"job_title":"Backend Developer"}'
```

### Explain Score
```bash
curl -X POST http://localhost:8001/api/ai/explain-score \
  -H "Content-Type: application/json" \
  -d '{"candidate_skills":["Python"],"job_requirements":["React","Node.js","AWS"],"score":45,"job_title":"Fullstack"}'
```

## Environment

API key is stored in `.env` - do NOT commit this file.

## Status

- Service: Running
- AI Model: gemini-pro
- Port: 8001