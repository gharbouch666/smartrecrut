import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CdkDragDrop, DragDropModule, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { Router } from '@angular/router';
import { AiService } from '../core/ai.service';

interface Candidature {
  id: number;
  candidat: { id: number; nom: string; email: string };
  offre: { id: number; titre: string };
  scoreTotal: number;
  statut: string;
  datePostulation: string;
}

@Component({
  selector: 'app-applications',
  standalone: true,
  imports: [CommonModule, FormsModule, DragDropModule],
  styleUrls: ['./applications.component.scss'],
  templateUrl: './applications.component.html',
})
export class ApplicationsComponent implements OnInit {
  allApplications: Candidature[] = [];
  aTrier: Candidature[] = [];
  entretien: Candidature[] = [];
  retenu: Candidature[] = [];
  refuse: Candidature[] = [];
  loading = false;
  userRole: string = '';
  currentUserId: number = 0;
  myApplications: Candidature[] = [];
  minScoreFilter: number = 0;
  breakdownModal = false;
  breakdown: any = null;
  breakdownLoading = false;
  selectedApp: Candidature | null = null;

  constructor(private http: HttpClient, private router: Router, private ai: AiService) {}

  get isCandidate(): boolean {
    return this.userRole === 'CANDIDAT';
  }

  isRecruteur(): boolean {
    return this.userRole === 'RECRUTEUR';
  }

  formatScore(score: number): string {
    if (!score && score !== 0) return '0';
    return Math.round(score).toString();
  }

  applyFilters() {
    const minScore = this.minScoreFilter;
    this.aTrier = this.allApplications.filter(a => a.statut === 'A_TRIER' && this.passesFilter(a, minScore));
    this.entretien = this.allApplications.filter(a => a.statut === 'ENTRETIEN' && this.passesFilter(a, minScore));
    this.retenu = this.allApplications.filter(a => a.statut === 'RETENU' && this.passesFilter(a, minScore));
    this.refuse = this.allApplications.filter(a => a.statut === 'REFUSE' && this.passesFilter(a, minScore));
  }

  passesFilter(app: Candidature, minScore: number): boolean {
    return minScore === 0 || (app.scoreTotal || 0) >= minScore;
  }

  showBreakdown(app: Candidature) {
    if (!this.isRecruteur()) return;
    this.selectedApp = app;
    this.breakdownModal = true;
    this.breakdownLoading = true;
    this.breakdown = null;
    
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    this.http.get<any>(`http://localhost:8000/api/candidatures/score-breakdown/${app.candidat.id}/${app.offre.id}`, { headers }).subscribe({
      next: (data) => {
        this.breakdown = data;
        this.breakdownLoading = false;
      },
      error: () => {
        this.breakdownLoading = false;
      }
    });
  }

  openChat(candidatId: number, candidatNom: string) {
    const otherUserId = candidatId;
    const otherUserNom = candidatNom;
    localStorage.setItem('chatWithId', String(otherUserId));
    localStorage.setItem('chatWithNom', otherUserNom);
    this.router.navigate(['/chat']);
  }

  ngOnInit() {
    const userStr = localStorage.getItem('user');
    const userRoleStored = localStorage.getItem('userRole');
    console.log('[DEBUG] localStorage user:', userStr);
    console.log('[DEBUG] localStorage userRole:', userRoleStored);
    if (userStr) {
      const user = JSON.parse(userStr);
      console.log('[DEBUG] Parsed user object:', user);
      this.userRole = user.type || userRoleStored || '';
      this.currentUserId = user.id || 0;
      console.log('[DEBUG] User role:', this.userRole, 'User ID:', this.currentUserId);
      this.userRole = this.userRole.trim().toUpperCase();
      console.log('[DEBUG] Normalized role:', this.userRole);
    }
    console.log('[DEBUG] Will call loadApplications with role:', this.userRole);
    this.loadApplications();
  }

  loadApplications() {
    this.loading = true;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    console.log('[DEBUG] loadApplications: role=', this.userRole, 'userId=', this.currentUserId);
    
    if (this.userRole === 'CANDIDAT') {
      this.http.get<Candidature[]>(`http://localhost:8000/api/candidatures/candidat/${this.currentUserId}`, { headers }).subscribe({
        next: (data) => {
          console.log('[DEBUG] Candidate API response:', data);
          this.myApplications = (data || []).filter(a => this.passesFilter(a, this.minScoreFilter));
          this.loading = false;
        },
        error: (err) => {
          console.error('[DEBUG] Candidate API error:', err);
          this.myApplications = [];
          this.loading = false;
        }
      });
    } else if (this.userRole === 'RECRUTEUR') {
      console.log('[DEBUG] Calling recruiter endpoint with ID:', this.currentUserId);
      this.http.get<Candidature[]>(`http://localhost:8000/api/candidatures/recruteur/${this.currentUserId}`, { headers }).subscribe({
        next: (data) => {
          console.log('[DEBUG] Recruiter API response:', data);
          console.log('[DEBUG] Response length:', data ? data.length : 0);
          if (data && data.length > 0) {
            console.log('[DEBUG] First item statut:', data[0].statut);
          }
          this.allApplications = data || [];
          this.applyFilters();
          this.loading = false;
        },
        error: (err) => {
          console.error('[DEBUG] Recruiter API error:', err);
          this.loading = false;
        }
      });
    } else if (this.userRole === 'ADMINISTRATEUR') {
      this.http.get<Candidature[]>(`http://localhost:8000/api/candidatures`, { headers }).subscribe({
        next: (data) => {
          console.log('[DEBUG] Admin API response:', data);
          this.allApplications = data || [];
          this.applyFilters();
          this.loading = false;
        },
        error: (err) => {
          console.error('[DEBUG] Admin API error:', err);
          this.loading = false;
        }
      });
    }
  }

  drop(event: CdkDragDrop<Candidature[]>, newStatus: string) {
    console.log('[DEBUG] drop called: newStatus=', newStatus);
    const isSameContainer = event.previousContainer === event.container;
    console.log('[DEBUG] isSameContainer:', isSameContainer);
    console.log('[DEBUG] currentIndex:', event.currentIndex);
    
    // Get the item - handle both same and different container cases
    let item = null;
    if (isSameContainer) {
      item = event.container.data ? event.container.data[event.currentIndex] : null;
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      // When moving between different containers, get item from previous container
      item = event.previousContainer.data ? event.previousContainer.data[event.previousIndex] : null;
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    }
    
    console.log('[DEBUG] item to update:', item);
    
    // Always call updateStatus to persist
    if (item && item.id) {
      console.log('[DEBUG] calling updateStatus for id:', item.id, 'status:', newStatus);
      this.updateStatus(item.id, newStatus);
    } else {
      console.log('[DEBUG] WARNING: no item found, cannot update status');
    }
  }

  updateStatus(id: number, newStatus: string) {
    console.log('[DEBUG] updateStatus called: id=', id, 'newStatus=', newStatus);
    const token = localStorage.getItem('accessToken');
    console.log('[DEBUG] Token:', token ? 'present' : 'missing');
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${token}`)
      .set('Content-Type', 'application/json');

    this.http.patch<Candidature>(`http://localhost:8000/api/candidatures/${id}/statut`, { statut: newStatus }, { headers }).subscribe({
      next: (res) => {
        console.log('[DEBUG] Status updated successfully:', res);
        const app = this.allApplications.find(a => a.id === id);
        if (app) app.statut = newStatus;
        window.dispatchEvent(new Event('applicationStatusUpdated'));
      },
      error: (err) => console.error('[DEBUG] Failed to update status:', err)
    });
  }

  interviewQuestions: string[] = [];
  interviewLoading = false;
  showInterviewModal = false;

  explainScoreExplanation: string = '';
  explainScoreLoading = false;
  showExplainScoreModal = false;

  explainScore(app: Candidature) {
    this.explainScoreLoading = true;
    this.showExplainScoreModal = true;
    this.explainScoreExplanation = '';
    
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    // First fetch skills from backend
    this.http.get<any>(`http://localhost:8000/api/candidatures/${app.id}/with-skills`, { headers }).subscribe({
      next: (skillsData) => {
        const candidateSkills = skillsData.candidatSkills || [];
        const jobRequirements = skillsData.jobTags || [];
        const score = app.scoreTotal || 0;
        const jobTitle = app.offre?.titre || 'Developer';
        
        this.ai.explainScore(candidateSkills, jobRequirements, score, jobTitle).subscribe({
          next: (res) => {
            this.explainScoreLoading = false;
            if (res.success && res.explanation) {
              this.explainScoreExplanation = res.explanation;
            } else {
              this.explainScoreExplanation = res.error || 'Unable to generate explanation';
            }
          },
          error: () => {
            this.explainScoreLoading = false;
          }
        });
      },
      error: () => {
        this.explainScoreLoading = false;
      }
    });
  }

  generateInterviewQuestions(app: Candidature) {
    this.interviewLoading = true;
    this.interviewQuestions = [];
    this.showInterviewModal = true;
    
    // Extract candidate skills and job title from the application
    const candidateSkills: string[] = [];
    const jobTitle = app.offre?.titre || 'Developer';
    
    this.ai.generateInterviewQuestions(candidateSkills, [], jobTitle).subscribe({
      next: (res) => {
        this.interviewLoading = false;
        if (res.success && res.questions) {
          this.interviewQuestions = res.questions;
        } else {
          this.interviewQuestions = [
            'Tell me about your experience with the required skills.',
            'Describe a challenging project you worked on.',
            'How do you stay updated with new technologies?',
            'What are your career goals?',
            'Why do you want to join our team?'
          ];
        }
      },
      error: () => {
        this.interviewLoading = false;
        this.interviewQuestions = [
          'Tell me about your experience with the required skills.',
          'Describe a challenging project you worked on.',
          'How do you stay updated with new technologies?',
          'What are your career goals?',
          'Why do you want to join our team?'
        ];
      }
    });
  }
}
