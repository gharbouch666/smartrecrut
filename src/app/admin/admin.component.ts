import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

interface DashboardStats {
  totalCandidates: number;
  totalRecruiters: number;
  totalOffres: number;
  totalCandidatures: number;
  totalTags: number;
}

interface User {
  id: number;
  nom: string;
  email: string;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, FormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {
  stats: DashboardStats = {
    totalCandidates: 0,
    totalRecruiters: 0,
    totalOffres: 0,
    totalCandidatures: 0,
    totalTags: 0
  };

  users: { candidats: User[]; recruteurs: User[]; administrateurs: User[] } = {
    candidats: [],
    recruteurs: [],
    administrateurs: []
  };

  showAddUser = false;
  newUser = { nom: '', email: '', password: '', role: 'RECRUTEUR' };
  savingUser = false;

  statusCounts: any = {};
  skillCategories: any[] = [];
  maxCount = 1;
  maxSkillCount = 1;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadStats();
    this.loadUsers();
    this.loadCharts();
  }

  private get authHeaders(): HttpHeaders {
    return new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
  }

  loadCharts() {
    this.http.get<any[]>('http://localhost:8000/api/candidatures', { headers: this.authHeaders }).subscribe({
      next: (data) => {
        const counts = { aTrier: 0, entretien: 0, retenu: 0, refuse: 0 };
        data.forEach((c: any) => {
          if      (c.statut === 'A_TRIER')   counts.aTrier++;
          else if (c.statut === 'ENTRETIEN') counts.entretien++;
          else if (c.statut === 'RETENU')    counts.retenu++;
          else if (c.statut === 'REFUSE')    counts.refuse++;
        });
        this.statusCounts = counts;
        this.maxCount = Math.max(counts.aTrier, counts.entretien, counts.retenu, counts.refuse, 1);
      }
    });

    this.http.get<any[]>('http://localhost:8000/api/tags', { headers: this.authHeaders }).subscribe({
      next: (tags) => {
        const categories: { [key: string]: number } = {};
        tags.forEach((t: any) => {
          categories[t.categorie] = (categories[t.categorie] || 0) + 1;
        });
        const colors: { [key: string]: string } = {
          'TECH':     '#5c6dff',
          'LANGUAGE': '#b6f542',
          'SOFT':     '#10b981',
          'TOOL':     '#f59e0b'
        };
        this.skillCategories = Object.entries(categories).map(([name, count]) => ({
          name,
          count,
          color: colors[name] || '#6b7280'
        }));
        this.maxSkillCount = Math.max(...this.skillCategories.map(c => c.count), 1);
      }
    });
  }

  loadStats() {
    this.http.get<DashboardStats>('http://localhost:8000/api/stats/dashboard', { headers: this.authHeaders }).subscribe({
      next: (data) => (this.stats = data),
      error: () => {}
    });
  }

  loadUsers() {
    this.http.get<any>('http://localhost:8000/api/stats/users', { headers: this.authHeaders }).subscribe({
      next: (data) => (this.users = data),
      error: () => {}
    });
  }

  deleteUser(id: number, type: string) {
    if (!confirm(`Delete this ${type}? This cannot be undone.`)) return;
    this.http
      .get(`http://localhost:8000/api/stats/users/delete/${id}/${type}`, { headers: this.authHeaders })
      .subscribe({
        next: () => {
          this.loadUsers();
        },
        error: (err) => {
          if (err.status === 500) {
            alert(`Cannot delete this ${type} — they may have associated data. Please remove their data first.`);
          } else {
            alert(`Failed to delete ${type}: ${err.error?.message || err.status}`);
          }
        }
      });
  }

  toggleAddUser() {
    this.showAddUser = !this.showAddUser;
  }

  addNewUser() {
    if (!this.newUser.nom || !this.newUser.email || !this.newUser.password) {
      alert('Please fill all fields.');
      return;
    }
    this.savingUser = true;
    const headers = this.authHeaders.set('Content-Type', 'application/json');
    this.http
      .post('http://localhost:8000/api/auth/register', {
        nom: this.newUser.nom,
        email: this.newUser.email,
        motDePasse: this.newUser.password,
        role: this.newUser.role
      }, { headers })
      .subscribe({
        next: () => {
          this.savingUser = false;
          this.showAddUser = false;
          this.newUser = { nom: '', email: '', password: '', role: 'RECRUTEUR' };
          this.loadUsers();
        },
        error: (err) => {
          alert(`Failed to create user: ${err.error?.message || err.status}`);
          this.savingUser = false;
        }
      });
  }
}