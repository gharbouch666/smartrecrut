import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class SmartrecruteAiService {
  private baseUrl = 'http://localhost:8001/api/ai';

  constructor(private http: HttpClient) {}

  generateOffer(title: string) {
    return this.http.post<any>(`${this.baseUrl}/generate-offer`, { title });
  }

  generateJobDescription(title: string, skills: string[], experience: string) {
    return this.http.post<any>(`${this.baseUrl}/generate-job-description`, {
      title,
      skills,
      experience
    });
  }

  generateInterviewQuestions(candidateSkills: string[], missingSkills: string[], jobTitle: string) {
    return this.http.post<any>(`${this.baseUrl}/generate-interview-questions`, {
      candidate_skills: candidateSkills,
      missing_skills: missingSkills,
      job_title: jobTitle
    });
  }

  explainScore(candidateSkills: string[], jobRequirements: string[], score: number, jobTitle: string) {
    return this.http.post<any>(`${this.baseUrl}/explain-score`, {
      candidate_skills: candidateSkills,
      job_requirements: jobRequirements,
      score,
      job_title: jobTitle
    });
  }

  searchCandidatesBackend(query: string) {
    return this.http.post<any>('http://localhost:8000/api/candidats/search', { query });
  }
}