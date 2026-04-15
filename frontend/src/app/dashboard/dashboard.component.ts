import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  stats = {
    totalOffres: 0,
    totalCandidats: 0,
    totalCandidatures: 0
  };
  userRole: string = '';
  currentUserId: number = 0;
  myApplications: any[] = [];
  myJobs: any[] = [];

  constructor(private http: HttpClient) {}

  get isAdmin(): boolean {
    return this.userRole === 'ADMIN' || this.userRole === 'ADMINISTRATEUR';
  }

  get isRecruiter(): boolean {
    return this.userRole === 'RECRUTEUR';
  }

  get isCandidate(): boolean {
    return this.userRole === 'CANDIDAT';
  }

  ngOnInit() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      this.userRole = user.type || '';
      this.currentUserId = user.id || 0;
    }
    this.loadStats();
    if (this.isCandidate) {
      this.loadMyApplications();
    }
    if (this.isRecruiter) {
      this.loadMyJobs();
    }
  }

  loadStats() {
    const token = localStorage.getItem('accessToken');
    if (!token) return;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get('http://localhost:8000/api/stats/dashboard', { headers }).subscribe({
      next: (data: any) => {
        this.stats = data;
      },
      error: (err) => {
        console.error('Stats error', err);
      }
    });
  }

  loadMyApplications() {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get<any[]>(`http://localhost:8000/api/candidatures/candidat/${this.currentUserId}`, { headers }).subscribe({
      next: (data) => {
        this.myApplications = data || [];
      },
      error: () => {}
    });
  }

  loadMyJobs() {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get<any[]>('http://localhost:8000/api/offres', { headers }).subscribe({
      next: (data) => {
        this.myJobs = data || [];
      },
      error: () => {}
    });
  }

  get openJobsCount(): number {
    return this.myJobs.filter(j => j.statut === 'OUVERTE').length;
  }

  get inConsiderationCount(): number {
    return this.myApplications.filter(a => a.statut === 'RETENU' || a.statut === 'ENTRETIEN').length;
  }

  get pendingCount(): number {
    return this.myApplications.filter(a => a.statut === 'A_TRIER').length;
  }
}
