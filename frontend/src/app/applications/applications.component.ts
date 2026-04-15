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
  template: `
    <div class="p-8 max-w-7xl mx-auto min-h-[calc(100vh-2rem)]">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6">
        <div class="space-y-1">
          <h1 class="text-3xl font-bold text-[var(--text)]">{{isCandidate ? 'My Applications' : 'Applications'}}</h1>
          <p class="text-[var(--text-muted)]">{{isCandidate ? 'Track your job applications' : 'Track candidates through the recruitment pipeline'}}</p>
        </div>
        <!-- Filter by score (Recruiter only) -->
        <div *ngIf="!isCandidate" class="flex items-center gap-2">
          <label class="text-sm text-[var(--text-muted)]">Min Score:</label>
          <select [(ngModel)]="minScoreFilter" (change)="applyFilters()" class="form-select" style="padding: 0.5rem; font-size: 0.875rem;">
            <option [value]="0">All</option>
            <option [value]="70">>= 70%</option>
            <option [value]="50">>= 50%</option>
            <option [value]="30">>= 30%</option>
          </select>
        </div>
      </div>

      <!-- Candidate View: Simple List -->
      <div *ngIf="isCandidate" class="space-y-4">
        <div *ngFor="let app of myApplications" 
          class="bg-[var(--card)] rounded-xl border border-[var(--border)] p-5 hover:border-[var(--accent)]/50 transition-all">
          <div class="flex justify-between items-start mb-2">
            <h3 class="text-lg font-semibold text-[var(--text)]">{{app.offre.titre}}</h3>
            <span class="px-3 py-1 rounded-full text-xs font-medium"
              [class.bg-yellow-100]="app.statut === 'A_TRIER'"
              [class.text-yellow-700]="app.statut === 'A_TRIER'"
              [class.bg-blue-100]="app.statut === 'ENTRETIEN'"
              [class.text-blue-700]="app.statut === 'ENTRETIEN'"
              [class.bg-green-100]="app.statut === 'RETENU'"
              [class.text-green-700]="app.statut === 'RETENU'"
              [class.bg-red-100]="app.statut === 'REFUSE'"
              [class.text-red-700]="app.statut === 'REFUSE'">
              {{app.statut === 'A_TRIER' ? 'To Review' : app.statut === 'ENTRETIEN' ? 'Interview' : app.statut === 'RETENU' ? 'Accepted' : 'Rejected'}}
            </span>
          </div>
          <div class="flex items-center gap-4 text-sm text-[var(--text-muted)]">
            <span>Score: </span>
            <span class="font-bold" 
              [class.text-green-600]="(app.scoreTotal || 0) >= 80" 
              [class.text-yellow-600]="(app.scoreTotal || 0) >= 50 && (app.scoreTotal || 0) < 80" 
              [class.text-red-600]="(app.scoreTotal || 0) < 50">
              {{formatScore(app.scoreTotal)}}%
            </span>
            <span class="ml-4">Applied: {{app.datePostulation | date:'mediumDate'}}</span>
          </div>
        </div>
        
        <!-- Empty State for Candidates -->
        <div *ngIf="myApplications.length === 0" class="text-center py-16">
          <div class="w-16 h-16 mx-auto mb-4 rounded-2xl bg-[var(--surface)] flex items-center justify-center">
            <svg class="w-8 h-8 text-[var(--text-muted)]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
            </svg>
          </div>
          <h3 class="text-lg font-medium text-[var(--text)] mb-1">No applications yet</h3>
          <p class="text-[var(--text-muted)]">Browse jobs and apply to get started</p>
          <a href="/jobs" class="inline-block mt-4 px-5 py-2.5 bg-[var(--accent)] text-white rounded-xl font-medium hover:opacity-90">Browse Jobs</a>
        </div>
      </div>

      <!-- Score Breakdown Modal -->
      <div *ngIf="breakdownModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" (click)="breakdownModal = false">
        <div class="bg-[var(--card)] rounded-xl border border-[var(--border)] p-6 max-w-md w-full mx-4" (click)="$event.stopPropagation()">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold text-[var(--text)]">Score Breakdown</h3>
            <button (click)="breakdownModal = false" class="text-[var(--text-muted)] hover:text-[var(--text)]">&times;</button>
          </div>
          <div *ngIf="breakdownLoading" class="text-center py-8 text-[var(--text-muted)]">Loading...</div>
          <div *ngIf="!breakdownLoading && breakdown">
            <div class="mb-4 text-center">
              <span class="text-4xl font-bold" 
                [class.text-green-600]="(breakdown.score || 0) >= 80" 
                [class.text-yellow-600]="(breakdown.score || 0) >= 50 && (breakdown.score || 0) < 80" 
                [class.text-red-600]="(breakdown.score || 0) < 50">
                {{formatScore(breakdown.score)}}%
              </span>
              <p class="text-sm text-[var(--text-muted)]">Overall Score</p>
            </div>
            <div class="space-y-3">
              <div *ngIf="breakdown.matchedSkills?.length">
                <p class="text-sm font-medium text-[var(--text)] mb-2">Matched Skills</p>
                <div class="flex flex-wrap gap-1">
                  <span *ngFor="let skill of breakdown.matchedSkills" class="px-2 py-1 bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 rounded text-xs">
                    {{skill}}
                  </span>
                </div>
              </div>
              <div *ngIf="breakdown.missingRequiredSkills?.length">
                <p class="text-sm font-medium text-[var(--text)] mb-2">Missing Required</p>
                <div class="flex flex-wrap gap-1">
                  <span *ngFor="let skill of breakdown.missingRequiredSkills" class="px-2 py-1 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 rounded text-xs">
                    {{skill}}
                  </span>
                </div>
              </div>
              <div *ngIf="breakdown.extraSkills?.length">
                <p class="text-sm font-medium text-[var(--text)] mb-2">Extra Skills</p>
                <div class="flex flex-wrap gap-1">
                  <span *ngFor="let skill of breakdown.extraSkills" class="px-2 py-1 bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-400 rounded text-xs">
                    {{skill}}
                  </span>
                </div>
              </div>
              <div *ngIf="!breakdown.matchedSkills?.length && !breakdown.missingRequiredSkills?.length && !breakdown.extraSkills?.length" class="text-center py-4 text-[var(--text-muted)]">
                No skill data available
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Interview Questions Modal -->
      <div *ngIf="showInterviewModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" (click)="showInterviewModal = false">
        <div class="bg-[var(--card)] rounded-xl border border-[var(--border)] p-6 max-w-lg w-full mx-4" (click)="$event.stopPropagation()">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold text-[var(--text)]">🤖 Interview Questions</h3>
            <button (click)="showInterviewModal = false" class="text-[var(--text-muted)] hover:text-[var(--text)]">&times;</button>
          </div>
          <div *ngIf="interviewLoading" class="text-center py-8 text-[var(--text-muted)]">Generating questions...</div>
          <div *ngIf="!interviewLoading && interviewQuestions.length" class="space-y-3">
            <p class="text-sm text-[var(--text-muted)] mb-4">Suggested questions for this candidate:</p>
            <div *ngFor="let q of interviewQuestions; let i = index" class="p-3 bg-[var(--surface)] rounded-lg">
              <span class="font-medium text-[var(--accent)]">{{i + 1 }}.</span> {{q}}
            </div>
          </div>
        </div>
      </div>

      <!-- Explain Score Modal -->
      <div *ngIf="showExplainScoreModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" (click)="showExplainScoreModal = false">
        <div class="bg-[var(--card)] rounded-xl border border-[var(--border)] p-6 max-w-lg w-full mx-4" (click)="$event.stopPropagation()">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold text-[var(--text)]">🤖 Why this score?</h3>
            <button (click)="showExplainScoreModal = false" class="text-[var(--text-muted)] hover:text-[var(--text)]">&times;</button>
          </div>
          <div *ngIf="explainScoreLoading" class="text-center py-8 text-[var(--text-muted)]">Analyzing...</div>
          <div *ngIf="!explainScoreLoading && explainScoreExplanation" class="text-[var(--text)]">
            <p class="text-sm text-[var(--text-muted)] mb-4">AI Analysis:</p>
            <p class="whitespace-pre-wrap">{{explainScoreExplanation}}</p>
          </div>
        </div>
      </div>

      <!-- Recruiter View: Kanban Board -->
      <div *ngIf="!isCandidate" cdkDropListGroup class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <!-- To Review -->
        <div class="bg-[var(--card)] rounded-xl border border-[var(--border)] overflow-hidden">
          <div class="px-4 py-3 border-b border-[var(--border)] bg-yellow-50 dark:bg-yellow-900/20 flex items-center justify-between">
            <h2 class="text-sm font-semibold text-[var(--text)] flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-yellow-500"></span>
              To Review
            </h2>
            <span class="text-xs font-medium text-[var(--text-muted)] bg-[var(--surface)] px-2 py-0.5 rounded-full">{{aTrier.length}}</span>
          </div>
          <div cdkDropList [cdkDropListData]="aTrier" (cdkDropListDropped)="drop($event, 'A_TRIER')" class="p-3 min-h-48 space-y-2">
            <div *ngFor="let app of aTrier" cdkDrag class="bg-[var(--surface)] rounded-lg border border-[var(--border)] p-3 cursor-move hover:border-[var(--accent)]/50 transition-all cursor-pointer" (click)="showBreakdown(app)">
              <div class="flex justify-between items-start mb-1.5">
                <span class="text-sm font-medium text-[var(--text)]">{{app.candidat.nom}}</span>
                <span class="text-xs font-bold" 
                  [class.text-green-600]="(app.scoreTotal || 0) >= 80" 
                  [class.text-yellow-600]="(app.scoreTotal || 0) >= 50 && (app.scoreTotal || 0) < 80" 
                  [class.text-red-600]="(app.scoreTotal || 0) < 50">
                  {{formatScore(app.scoreTotal)}}%
                </span>
              </div>
              <p class="text-xs text-[var(--text-muted)]">{{app.offre.titre}}</p>
              <div class="flex gap-2 mt-2" *ngIf="isRecruteur()">
                <button (click)="openChat(app.candidat.id, app.candidat.nom); $event.stopPropagation()" class="text-xs text-[var(--accent)] hover:underline">
                  Chat
                </button>
                <button (click)="showBreakdown(app); $event.stopPropagation()" class="text-xs text-blue-600 hover:underline">
                  Details
                </button>
                <button (click)="explainScore(app); $event.stopPropagation()" class="text-xs text-orange-600 hover:underline">
                  Why low?
                </button>
                <button (click)="generateInterviewQuestions(app); $event.stopPropagation()" class="text-xs text-purple-600 hover:underline">
                  Questions
                </button>
              </div>
            </div>
            <div *ngIf="aTrier.length === 0" class="text-center py-8 text-sm text-[var(--text-muted)]">
              No applications
            </div>
          </div>
        </div>

        <!-- Interview -->
        <div class="bg-[var(--card)] rounded-xl border border-[var(--border)] overflow-hidden">
          <div class="px-4 py-3 border-b border-[var(--border)] bg-blue-50 dark:bg-blue-900/20 flex items-center justify-between">
            <h2 class="text-sm font-semibold text-[var(--text)] flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-blue-500"></span>
              Interview
            </h2>
            <span class="text-xs font-medium text-[var(--text-muted)] bg-[var(--surface)] px-2 py-0.5 rounded-full">{{entretien.length}}</span>
          </div>
          <div cdkDropList [cdkDropListData]="entretien" (cdkDropListDropped)="drop($event, 'ENTRETIEN')" class="p-3 min-h-48 space-y-2">
            <div *ngFor="let app of entretien" cdkDrag class="bg-[var(--surface)] rounded-lg border border-[var(--border)] p-3 cursor-move hover:border-[var(--accent)]/50 transition-all">
              <div class="flex justify-between items-start mb-1.5">
                <span class="text-sm font-medium text-[var(--text)]">{{app.candidat.nom}}</span>
                <span class="text-xs font-bold" 
                  [class.text-green-600]="(app.scoreTotal || 0) >= 80" 
                  [class.text-yellow-600]="(app.scoreTotal || 0) >= 50 && (app.scoreTotal || 0) < 80" 
                  [class.text-red-600]="(app.scoreTotal || 0) < 50">
                  {{formatScore(app.scoreTotal)}}%
                </span>
              </div>
              <p class="text-xs text-[var(--text-muted)]">{{app.offre.titre}}</p>
              <button *ngIf="isRecruteur()" (click)="openChat(app.candidat.id, app.candidat.nom)" class="mt-2 text-xs text-[var(--accent)] hover:underline">
                Chat
              </button>
            </div>
            <div *ngIf="entretien.length === 0" class="text-center py-8 text-sm text-[var(--text-muted)]">
              No applications
            </div>
          </div>
        </div>

        <!-- Hired -->
        <div class="bg-[var(--card)] rounded-xl border border-[var(--border)] overflow-hidden">
          <div class="px-4 py-3 border-b border-[var(--border)] bg-green-50 dark:bg-green-900/20 flex items-center justify-between">
            <h2 class="text-sm font-semibold text-[var(--text)] flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-green-500"></span>
              Hired
            </h2>
            <span class="text-xs font-medium text-[var(--text-muted)] bg-[var(--surface)] px-2 py-0.5 rounded-full">{{retenu.length}}</span>
          </div>
          <div cdkDropList [cdkDropListData]="retenu" (cdkDropListDropped)="drop($event, 'RETENU')" class="p-3 min-h-48 space-y-2">
            <div *ngFor="let app of retenu" cdkDrag class="bg-[var(--surface)] rounded-lg border border-[var(--border)] p-3 cursor-move hover:border-[var(--accent)]/50 transition-all">
              <div class="flex justify-between items-start mb-1.5">
                <span class="text-sm font-medium text-[var(--text)]">{{app.candidat.nom}}</span>
                <span class="text-xs font-bold" 
                  [class.text-green-600]="(app.scoreTotal || 0) >= 80" 
                  [class.text-yellow-600]="(app.scoreTotal || 0) >= 50 && (app.scoreTotal || 0) < 80" 
                  [class.text-red-600]="(app.scoreTotal || 0) < 50">
                  {{formatScore(app.scoreTotal)}}%
                </span>
              </div>
              <p class="text-xs text-[var(--text-muted)]">{{app.offre.titre}}</p>
              <button *ngIf="isRecruteur()" (click)="openChat(app.candidat.id, app.candidat.nom)" class="mt-2 text-xs text-[var(--accent)] hover:underline">
                Chat
              </button>
            </div>
            <div *ngIf="retenu.length === 0" class="text-center py-8 text-sm text-[var(--text-muted)]">
              No applications
            </div>
          </div>
        </div>

        <!-- Rejected -->
        <div class="bg-[var(--card)] rounded-xl border border-[var(--border)] overflow-hidden">
          <div class="px-4 py-3 border-b border-[var(--border)] bg-red-50 dark:bg-red-900/20 flex items-center justify-between">
            <h2 class="text-sm font-semibold text-[var(--text)] flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-red-500"></span>
              Rejected
            </h2>
            <span class="text-xs font-medium text-[var(--text-muted)] bg-[var(--surface)] px-2 py-0.5 rounded-full">{{refuse.length}}</span>
          </div>
          <div cdkDropList [cdkDropListData]="refuse" (cdkDropListDropped)="drop($event, 'REFUSE')" class="p-3 min-h-48 space-y-2">
            <div *ngFor="let app of refuse" cdkDrag class="bg-[var(--surface)] rounded-lg border border-[var(--border)] p-3 cursor-move hover:border-[var(--accent)]/50 transition-all">
              <div class="flex justify-between items-start mb-1.5">
                <span class="text-sm font-medium text-[var(--text)]">{{app.candidat.nom}}</span>
                <span class="text-xs font-bold" 
                  [class.text-green-600]="(app.scoreTotal || 0) >= 80" 
                  [class.text-yellow-600]="(app.scoreTotal || 0) >= 50 && (app.scoreTotal || 0) < 80" 
                  [class.text-red-600]="(app.scoreTotal || 0) < 50">
                  {{formatScore(app.scoreTotal)}}%
                </span>
              </div>
              <p class="text-xs text-[var(--text-muted)]">{{app.offre.titre}}</p>
              <button *ngIf="isRecruteur()" (click)="openChat(app.candidat.id, app.candidat.nom)" class="mt-2 text-xs text-[var(--accent)] hover:underline">
                Chat
              </button>
            </div>
            <div *ngIf="refuse.length === 0" class="text-center py-8 text-sm text-[var(--text-muted)]">
              No applications
            </div>
          </div>
        </div>
      </div>
    </div>
  `
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
