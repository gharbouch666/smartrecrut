import requests
import json

OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "llama3.2"

print(f"DEBUG: Using Ollama with model {MODEL}")


def _call_ollama(prompt: str, system_prompt: str = None) -> str:
    """Call local Ollama model."""
    full_prompt = system_prompt + "\n\n" + prompt if system_prompt else prompt
    
    try:
        response = requests.post(
            OLLAMA_URL,
            json={
                "model": MODEL,
                "prompt": full_prompt,
                "stream": False
            },
            timeout=120
        )
        
        if response.status_code == 200:
            data = response.json()
            return data.get("response", str(data))
        return f"Error: {response.status_code} - {response.text[:200]}"
    except Exception as e:
        return f"Ollama error: {str(e)}"


def generate_job_description(title: str, skills: list, experience: str) -> str:
    """Generate a professional job description from title and skills."""
    skills_str = ", ".join(skills) if skills else "relevant skills"
    
    prompt = f"""Job posting for {title} ({experience}):

Required: {skills_str}
Max 200 words.
Sections: Role overview, Responsibilities (4 points), Requirements (3 points), What we offer (3 points).
Direct. No filler."""
    
    return _call_ollama(prompt)


def generate_interview_questions(candidate_skills: list, missing_skills: list, job_title: str) -> list:
    """Generate technical interview questions."""
    cand_skills_str = ", ".join(candidate_skills) if candidate_skills else "technical skills"
    gap_skills_str = ", ".join(missing_skills) if missing_skills else "gaps"
    
    prompt = f"""Generate exactly 5 interview questions for {job_title} position.
Candidate skills: {cand_skills_str}
Skills to test: {gap_skills_str}

Return ONLY a JSON array with 5 questions. Format: ["Question 1?", "Question 2?", "Question 3?", "Question 4?", "Question 5?"]"""
    
    result = _call_ollama(prompt, "You are a helpful HR assistant. Always respond with valid JSON.")
    
    try:
        if "[" in result:
            start = result.find("[")
            end = result.rfind("]") + 1
            questions = json.loads(result[start:end])
            return questions[:5]
    except:
        pass
    
    return [
        "Tell me about your experience with the required skills.",
        "Describe a challenging project you worked on.",
        "How do you stay updated with new technologies?",
        "What are your career goals?",
        "Why do you want to join our team?"
    ]


def explain_low_score(candidate_skills: list, job_requirements: list, score: int, job_title: str) -> str:
    """Explain why a candidate scored low."""
    cand_skills_str = ", ".join(candidate_skills) if candidate_skills else "No skills provided"
    req_skills_str = ", ".join(job_requirements) if job_requirements else "No requirements provided"
    
    prompt = f"""Score analysis for {job_title} position: {score}%.

Candidate skills: {cand_skills_str}
Job requirements: {req_skills_str}

Short professional notes:
- Match level and原因
- Missing/skills gaps
- 2 hiring recommendations max
Max 150 words. No filler."""
    
    return _call_ollama(prompt, "HR analyst. Ultra-concise. No fluff.")


def generate_offer_description(title: str) -> str:
    """Generate complete job offer from title."""
    prompt = f"""Write a complete job posting for {title} position.

Include:
- About the Role (2 sentences)
- Key Responsibilities (5 points)
- Required Skills (4 points)
- Nice to Have (2 points)
- What We Offer (4 points)

300-400 words, professional tone. Start directly with the content."""
    
    return _call_ollama(prompt)