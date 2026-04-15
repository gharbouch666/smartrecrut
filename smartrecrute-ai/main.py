"""
SmartRecrute AI Service
FastAPI service for AI-powered recruitment features using Google Gemini API

Run: uvicorn main:app --host 0.0.0.0 --port 8001
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from routes.ai_routes import router as ai_router

app = FastAPI(
    title="SmartRecrute AI API",
    description="AI-powered recruitment features using Google Gemini",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:4200", "http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(ai_router, prefix="/api/ai", tags=["AI Features"])


@app.get("/")
async def root():
    return {
        "service": "SmartRecrute AI",
        "version": "1.0.0",
        "status": "running",
        "endpoints": {
            "generate_job_description": "POST /api/ai/generate-job-description",
            "generate_interview_questions": "POST /api/ai/generate-interview-questions", 
            "explain_score": "POST /api/ai/explain-score",
            "generate_offer": "POST /api/ai/generate-offer"
        }
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)