import requests
import json


def _call_huggingface(prompt: str) -> str:
    """Call HuggingFace Inference API."""
    try:
        # Using a free model that works for text generation
        response = requests.post(
            "https://api-inference.huggingface.co/models/meta-llama/Llama-3.2-1B-Instruct",
            headers={"Content-Type": "application/json"},
            json={
                "inputs": prompt,
                "parameters": {
                    "max_new_tokens": 500,
                    "temperature": 0.7
                }
            },
            timeout=30
        )
        
        if response.status_code == 200:
            data = response.json()
            if isinstance(data, list) and len(data) > 0:
                return data[0].get("generated_text", str(data))
            return str(data)
        else:
            return f"Error: {response.status_code} - {response.text[:200]}"
            
    except Exception as e:
        return f"Error: {str(e)}"


def generate_job_description(title: str, skills: list, experience: str) -> str:
    """Generate a professional job description from title and skills."""
    skills_str = ", ".join(skills) if skills else "relevant skills"
    
    prompt = f"""Write a professional job description for a {title} position.
Required skills: {skills_str}
Experience level: {experience}

Write 200-300 words with: Introduction, Key Responsibilities (5 bullet points), Qualifications (3 bullet points), Benefits."""
    
    result = _call_huggingface(prompt)
    
    # Clean up the response
    if prompt in result:
        result = result.replace(prompt, "").strip()
    
    return result if result else "Could not generate description. Please try again."


def generate_interview_questions(candidate_skills: list, missing_skills: list, job_title: str) -> list:
    """Generate technical interview questions."""
    cand_skills_str = ", ".join(candidate_skills) if candidate_skills else "technical skills"
    gap_skills_str = ", ".join(missing_skills) if missing_skills else "gaps"
    
    prompt = f"""Generate 5 interview questions for {job_title} position.
Candidate skills: {cand_skills_str}
Skills to test: {gap_skills_str}

Return ONLY a JSON array with 5 questions like: ["Q1?", "Q2?", "Q3?", "Q4?", "Q5?"]"""
    
    result = _call_huggingface(prompt)
    
    try:
        # Try to extract JSON
        if "[" in result:
            start = result.find("[")
            end = result.rfind("]") + 1
            questions = json.loads(result[start:end])
            return questions[:5]
    except:
        pass
    
    # Fallback questions
    return [
        "Tell me about your experience with the required skills.",
        "Describe a challenging project you worked on.",
        "How do you stay updated with new technologies?",
        "What are your career goals?",
        "Why do you want to join our team?"
    ]


def explain_low_score(candidate_skills: list, job_requirements: list, score: int, job_title: str) -> str:
    """Explain why a candidate scored low."""
    cand_skills_str = ", ".join(candidate_skills) if candidate_skills else "No skills"
    req_skills_str = ", ".join(job_requirements) if job_requirements else "No requirements"
    
    prompt = f"""Why did this {job_title} candidate score {score}%?
Their skills: {cand_skills_str}
Required: {req_skills_str}

Explain briefly (3 sentences) and give 2 suggestions to improve."""
    
    result = _call_huggingface(prompt)
    
    if prompt in result:
        result = result.replace(prompt, "").strip()
    
    return result if result else f"Your score is {score}%. Consider adding more required skills."


def generate_offer_description(title: str) -> str:
    """Generate complete job offer from title."""
    prompt = f"""Write a complete job posting for {title} position.

Include:
- About the Role (2 sentences)
- Key Responsibilities (5 points)
- Required Skills (4 points)
- Nice to Have (2 points)
- What We Offer (4 points)

Write 300-400 words professionally."""
    
    result = _call_huggingface(prompt)
    
    if prompt in result:
        result = result.replace(prompt, "").strip()
    
    return result if result else f"Job posting for {title}. Add your company details."