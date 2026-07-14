import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { SmartrecruteAiService } from '../core/smartrecruteAi.service';

@Component({
  selector: 'app-candidates',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],

  templateUrl: './candidates.component.html',
  styleUrls: ['./candidates.component.scss']
})
export class CandidatesComponent implements OnInit {
  candidates: any[] = [];
  loading = false;
  showForm = false;
  candidateForm: FormGroup;

  isAdmin(): boolean {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      return user.type === 'ADMINISTRATEUR';
    }
    return false;
  }

  isRecruteur(): boolean {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      return user.type === 'RECRUTEUR';
    }
    return false;
  }

  getUserId(): number {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      return user.id || 0;
    }
    return 0;
  }

  constructor(private fb: FormBuilder, private http: HttpClient, private ai: SmartrecruteAiService) {
    this.candidateForm = this.fb.group({
      nom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      motDePasse: ['', [Validators.required, Validators.minLength(6)]],
      role: ['CANDIDAT', Validators.required],
      telephone: [''],
      dateNaissance: [''],
      cvUrl: ['']
    });
  }

  ngOnInit() {
    this.loadCandidates();
  }

  loadCandidates() {
    this.loading = true;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    if (this.isRecruteur()) {
      const userId = this.getUserId();
      this.http.get<any[]>(`http://localhost:8000/api/candidatures/recruteur/${userId}`, { headers }).subscribe({
        next: (data) => {
          const seen = new Set();
          const unique: any[] = [];
          for (const app of data) {
            if (app.candidat && !seen.has(app.candidat.id)) {
              seen.add(app.candidat.id);
              unique.push({
                id: app.candidat.id,
                nom: app.candidat.nom,
                email: app.candidat.email,
                scoreMatching: app.scoreTotal || 0,
                offre: app.offre?.titre
              });
            }
          }
          this.candidates = unique;
          this.loading = false;
        },
        error: (err) => {
          console.error('Failed to load candidates', err);
          this.candidates = [];
          this.loading = false;
        }
      });
    } else {
      this.http.get('http://localhost:8000/api/candidats', { headers }).subscribe({
        next: (data: any) => {
          this.candidates = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Failed to load candidates', err);
          this.loading = false;
        }
      });
    }
  }

  toggleForm() {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.candidateForm.reset({ role: 'CANDIDAT' });
    }
  }

  onSubmit() {
    if (this.candidateForm.invalid) return;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.post('http://localhost:8000/api/candidats', this.candidateForm.value, { headers }).subscribe({
      next: () => {
        alert('Candidate added successfully!');
        this.loadCandidates();
        this.toggleForm();
      },
      error: (err) => {
        console.error('Failed to add candidate', err);
        alert('Failed to add candidate');
      }
    });
  }

  formatScore(score: number): string {
    if (score == null) return '0';
    return Math.round(score).toString();
  }

  deleteCandidate(id: number) {
    if (!confirm('Delete this candidate?')) return;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.delete(`http://localhost:8000/api/candidats/${id}`, { headers }).subscribe({
      next: () => {
        this.loadCandidates();
      },
      error: (err) => console.error('Delete failed', err)
    });
  }

  aiSearchQuery = '';
  aiSearchLoading = false;
  aiSearchResults: any[] = [];

  aiSearch() {
    if (!this.aiSearchQuery.trim()) return;
    this.aiSearchLoading = true;
    this.aiSearchResults = [];
    
    this.ai.searchCandidatesBackend(this.aiSearchQuery).subscribe({
      next: (res) => {
        this.aiSearchLoading = false;
        this.candidates = res || [];
      },
      error: () => {
        this.aiSearchLoading = false;
        this.loadCandidates();
      }
    });
  }
}
