import os
import google.generativeai as genai
from dotenv import load_dotenv
import json

load_dotenv()

API_KEY = os.getenv("GEMINI_API_KEY")
print(f"DEBUG: Loading API key: {API_KEY[:10]}...")

model = None

if API_KEY:
    try:
        genai.configure(api_key=API_KEY)
        model = genai.GenerativeModel("gemini-2.0-flash")
        print("DEBUG: Gemini model configured successfully")
    except Exception as e:
        print(f"ERROR configuring Gemini: {e}")
        model = None


FALLBACK_TEMPLATES = {
    "default": """About the Role:
We're looking for a talented professional to join our growing team.

Key Responsibilities:
- Design and implement scalable solutions
- Collaborate with cross-functional teams
- Write clean, maintainable code
- Participate in code reviews
- Mentor junior team members

Required Skills:
- 3+ years of relevant experience
- Strong problem-solving skills
- Good communication skills
- Team player attitude

Nice to Have:
- Experience with cloud platforms
- Agile methodology experience
- Open source contributions

What We Offer:
- Competitive salary
- Remote-friendly work environment
- Health insurance
- Learning and development budget
- Modern office space""",
    
    "developer": """About the Role:
We're seeking a skilled Developer to build amazing software.

Key Responsibilities:
- Design and develop high-quality applications
- Write clean, efficient code
- Test and debug applications
- Collaborate with product team
- Document your work

Required Skills:
- Proficiency in [programming language]
- Understanding of data structures
- Version control (Git)
- Problem-solving skills

Nice to Have:
- Framework experience
- Database knowledge
- API development

What We Offer:
- Competitive salary (€40-70k)
- Flexible working hours
- Remote options
- Team building events""",
    
    "backend": """About the Role:
Join our backend team to build robust APIs and services.

Key Responsibilities:
- Design and implement REST APIs
- Database design and optimization
- Write efficient queries
- Ensure data security
- Performance optimization

Required Skills:
- [Python/Java/Node.js] expertise
- SQL and NoSQL databases
- API design patterns
- Cloud basics

Nice to Have:
- AWS/GCP experience
- Docker/Kubernetes
- microservices

What We Offer:
- Salary: €50-80k
- Full remote option
- Latest tech equipment
- Conference attendance""",
    
    "frontend": """About the Role:
We're looking for a Frontend Developer to create beautiful UIs.

Key Responsibilities:
- Build responsive web interfaces
- Implement designs in code
- Optimize performance
- Ensure cross-browser compatibility
- Collaborate with designers

Required Skills:
- [React/Vue/Angular] experience
- HTML/CSS/JavaScript
- UI/UX basics
- Debugging skills

Nice to Have:
- TypeScript
- Testing frameworks
- Design tools

What We Offer:
- €45-75k salary
- Work from anywhere
- Latest MacBook
- Learning budget"""
}


def _get_fallback(title: str) -> str:
    """Get fallback template based on title keyword."""
    title_lower = title.lower()
    for keyword, template in FALLBACK_TEMPLATES.items():
        if keyword in title_lower:
            return template
    return FALLBACK_TEMPLATES["default"]


def _check_model():
    if model is None:
        return "AI service unavailable. The Gemini API key needs to be valid."
    return None


def generate_job_description(title: str, skills: list, experience: str) -> str:
    """Generate a professional job description from title and skills."""
    error = _check_model()
    if error:
        return f"{_get_fallback(title)}\n\n(AI quota exceeded - using template)"
    
    skills_str = ", ".join(skills) if skills else "relevant skills"
    prompt = f"""Write a professional job description for a {title} position.
Required skills: {skills_str}
Experience level: {experience}

Include: Intro, Responsibilities (5 bullet points), Qualifications (3 bullet points), Benefits.
200-300 words, professional tone."""
    
    try:
        response = model.generate_content(prompt)
        return response.text
    except Exception as e:
        error_msg = str(e)
        if "429" in error_msg or "quota" in error_msg.lower():
            return f"{_get_fallback(title)}\n\n(AI quota exceeded - using template)"
        return f"Error: {error_msg}"


def generate_interview_questions(candidate_skills: list, missing_skills: list, job_title: str) -> list:
    """Generate technical interview questions."""
    error = _check_model()
    if error:
        return [
            f"Tell me about your experience with {', '.join(candidate_skills[:2]) if candidate_skills else 'the role'}.",
            "Describe a challenging technical problem you solved.",
            "How do you stay updated with new technologies?",
            "Tell me about a project you're most proud of.",
            "How do you handle tight deadlines?"
        ]
    
    cand_skills_str = ", ".join(candidate_skills) if candidate_skills else "technical skills"
    gap_skills_str = ", ".join(missing_skills) if missing_skills else "gaps"
    
    prompt = f"""Generate 5 interview questions for {job_title}.
Candidate skills: {cand_skills_str}
Skills to test: {gap_skills_str}

Return JSON array: ["Q1?", "Q2?", "Q3?", "Q4?", "Q5?"]"""
    
    try:
        response = model.generate_content(prompt)
        text = response.text.strip()
        if text.startswith("```json"):
            text = text[7:]
        if text.endswith("```"):
            text = text[:-3]
        return json.loads(text.strip())
    except:
        return ["Error - quota may be exceeded"]


def explain_low_score(candidate_skills: list, job_requirements: list, score: int, job_title: str) -> str:
    """Explain why a candidate scored low."""
    error = _check_model()
    if error:
        return f"Your score of {score}% indicates room for improvement.\n\nTips to improve:\n1. Focus on required skills from the job posting\n2. Add more relevant technologies to your profile\n3. Update skill levels to match job requirements"
    
    cand_skills_str = ", ".join(candidate_skills) if candidate_skills else "No skills"
    req_skills_str = ", ".join(job_requirements) if job_requirements else "No requirements"
    
    prompt = f"""Why did this {job_title} candidate score {score}%?
Their skills: {cand_skills_str}
Required: {req_skills_str}

Explain briefly (3 sentences). Give 2 suggestions to improve."""
    
    try:
        response = model.generate_content(prompt)
        return response.text
    except:
        return f"Score: {score}%\n\nMissing skills: {', '.join(job_requirements)}\n\nTo improve: Add more required skills to your profile."


def search_candidates_natural(query: str, candidates_data: list) -> dict:
    """Parse natural language search query."""
    error = _check_model()
    if error:
        return {"skills": ["extract from query"], "filters": "basic search"}
    
    prompt = f"""Extract from "{query}":
Skills wanted and any filters.

Return JSON: {{"skills": ["skill1"], "filters": "filters"}}"""
    
    try:
        response = model.generate_content(prompt)
        text = response.text.strip()
        if text.startswith("```json"):
            text = text[7:]
        if text.endswith("```"):
            text = text[:-3]
        return json.loads(text.strip())
    except:
        return {"skills": [], "filters": ""}


def generate_offer_description(title: str) -> str:
    """Generate complete job offer from title."""
    error = _check_model()
    if error:
        return f"{_get_fallback(title)}\n\n(AI quota exceeded - using template)"
    
    prompt = f"""Write a complete job posting for {title}.

Include:
- About the Role (2 sentences)
- Key Responsibilities (5 points)
- Required Skills (4 points)
- Nice to Have (2 points)
- What We Offer (4 points)

300-400 words, professional."""
    
    try:
        response = model.generate_content(prompt)
        return response.text
    except Exception as e:
        error_msg = str(e)
        if "429" in error_msg or "quota" in error_msg.lower():
            return f"{_get_fallback(title)}\n\n(AI quota exceeded - using template)"
        return f"Title: {title}\n\nError: {error_msg}"