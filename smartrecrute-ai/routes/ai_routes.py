from fastapi import APIRouter, Request
from pydantic import BaseModel
from typing import List, Optional
from services.grok_service import (
    generate_job_description,
    generate_interview_questions,
    explain_low_score,
    generate_offer_description
)

router = APIRouter()


class JobDescriptionRequest(BaseModel):
    title: str
    skills: List[str]
    experience: str = "Mid-level"


class InterviewQuestionsRequest(BaseModel):
    candidate_skills: List[str]
    missing_skills: List[str]
    job_title: str


class ScoreExplanationRequest(BaseModel):
    candidate_skills: Optional[List[str]] = None
    job_requirements: Optional[List[str]] = None
    score: int
    job_title: str


class GenerateOfferRequest(BaseModel):
    title: str


@router.post("/generate-job-description")
async def generate_job_desc(request: JobDescriptionRequest):
    """Generate a job description from title and skills."""
    try:
        result = generate_job_description(
            title=request.title,
            skills=request.skills,
            experience=request.experience
        )
        return {"success": True, "description": result}
    except Exception as e:
        return {"success": False, "error": str(e)}


@router.post("/generate-interview-questions")
async def generate_questions(request: InterviewQuestionsRequest):
    """Generate interview questions based on candidate profile."""
    try:
        questions = generate_interview_questions(
            candidate_skills=request.candidate_skills,
            missing_skills=request.missing_skills,
            job_title=request.job_title
        )
        return {"success": True, "questions": questions}
    except Exception as e:
        return {"success": False, "error": str(e)}


@router.post("/explain-score")
async def explain_score(request: Request):
    """Explain why a candidate scored low - bypasses Pydantic validation."""
    try:
        data = await request.json()
        
        cand_skills = data.get("candidate_skills") or []
        job_reqs = data.get("job_requirements") or []
        score = data.get("score", 0)
        job_title = data.get("job_title", "Position")
        
        explanation = explain_low_score(
            candidate_skills=cand_skills,
            job_requirements=job_reqs,
            score=score,
            job_title=job_title
        )
        return {"success": True, "explanation": explanation}
    except Exception as e:
        return {"success": False, "error": str(e)}


@router.post("/generate-offer")
async def generate_offer(request: GenerateOfferRequest):
    """Generate complete job offer from title only."""
    try:
        offer = generate_offer_description(title=request.title)
        return {"success": True, "offer": offer}
    except Exception as e:
        return {"success": False, "error": str(e)}


@router.get("/health")
async def health():
    """Health check endpoint."""
    return {"status": "healthy", "service": "smartrecrute-ai"}