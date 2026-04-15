import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
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
  template: `
    <div class="p-8 max-w-7xl mx-auto">
      <h1 class="page-title mb-8">Admin Dashboard</h1>
      
      <div class="grid grid-cols-4 gap-6 mb-12">
        <div class="card">
          <p class="text-sm" style="color: var(--text-muted)">Total Candidates</p>
          <p class="text-4xl font-bold" style="color: var(--text)">{{stats.totalCandidates || 0}}</p>
        </div>
        <div class="card">
          <p class="text-sm" style="color: var(--text-muted)">Total Recruiters</p>
          <p class="text-4xl font-bold" style="color: var(--text)">{{stats.totalRecruiters || 0}}</p>
        </div>
        <div class="card">
          <p class="text-sm" style="color: var(--text-muted)">Job Offers</p>
          <p class="text-4xl font-bold" style="color: var(--text)">{{stats.totalOffres || 0}}</p>
        </div>
        <div class="card">
          <p class="text-sm" style="color: var(--text-muted)">Applications</p>
          <p class="text-4xl font-bold" style="color: var(--text)">{{stats.totalCandidatures || 0}}</p>
        </div>
      </div>

      <!-- Charts Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-12">
        <!-- Applications by Status -->
        <div class="card">
          <h3 class="text-lg font-semibold mb-6" style="color: var(--text)">Applications Pipeline</h3>
          <div class="space-y-4">
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                <span class="text-sm" style="color: var(--text-muted)">To Review</span>
                <span class="text-sm font-medium" style="color: var(--text)">{{statusCounts.aTrier || 0}}</span>
              </div>
              <div style="height: 8px; background: var(--surface); border-radius: 4px; overflow: hidden;">
                <div [style.width.%]="(statusCounts.aTrier || 0) / maxCount * 100" style="height: 100%; background: #eab308;"></div>
              </div>
            </div>
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                <span class="text-sm" style="color: var(--text-muted)">Interview</span>
                <span class="text-sm font-medium" style="color: var(--text)">{{statusCounts.entretien || 0}}</span>
              </div>
              <div style="height: 8px; background: var(--surface); border-radius: 4px; overflow: hidden;">
                <div [style.width.%]="(statusCounts.entretien || 0) / maxCount * 100" style="height: 100%; background: #3b82f6;"></div>
              </div>
            </div>
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                <span class="text-sm" style="color: var(--text-muted)">Hired</span>
                <span class="text-sm font-medium" style="color: var(--text)">{{statusCounts.retenu || 0}}</span>
              </div>
              <div style="height: 8px; background: var(--surface); border-radius: 4px; overflow: hidden;">
                <div [style.width.%]="(statusCounts.retenu || 0) / maxCount * 100" style="height: 100%; background: #22c55e;"></div>
              </div>
            </div>
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                <span class="text-sm" style="color: var(--text-muted)">Rejected</span>
                <span class="text-sm font-medium" style="color: var(--text)">{{statusCounts.refuse || 0}}</span>
              </div>
              <div style="height: 8px; background: var(--surface); border-radius: 4px; overflow: hidden;">
                <div [style.width.%]="(statusCounts.refuse || 0) / maxCount * 100" style="height: 100%; background: #ef4444;"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Skills by Category -->
        <div class="card">
          <h3 class="text-lg font-semibold mb-6" style="color: var(--text)">Skills by Category</h3>
          <div class="space-y-3">
            <div *ngFor="let cat of skillCategories" style="display: flex; align-items: center; gap: 1rem;">
              <span class="text-sm" style="color: var(--text-muted); width: 100px;">{{cat.name}}</span>
              <div style="flex: 1; height: 8px; background: var(--surface); border-radius: 4px; overflow: hidden;">
                <div [style.width.%]="cat.count / maxSkillCount * 100" [style.background]="cat.color" style="height: 100%;"></div>
              </div>
              <span class="text-sm font-medium" style="color: var(--text); width: 30px;">{{cat.count}}</span>
            </div>
            <div *ngIf="skillCategories.length === 0" class="text-center py-8 text-[var(--text-muted)]">
              No skills data available
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-8 mb-12">
        <div class="card hover:border-[var(--accent)] transition-all cursor-pointer group" routerLink="/admin/tags">
          <div class="w-20 h-20 rounded-2xl flex items-center justify-center mb-6" style="background: var(--surface)">
            <svg class="w-10 h-10" style="color: var(--accent)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"/>
            </svg>
          </div>
          <h2 class="text-2xl font-bold mb-3" style="color: var(--text)">Skills Library</h2>
          <p class="text-lg leading-relaxed" style="color: var(--text-muted)">Manage technical competencies and tags for matching algorithm</p>
        </div>
      </div>

      <!-- User Management Section -->
      <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
          <h2 class="text-2xl font-bold" style="color: var(--text)">User Management</h2>
          <button (click)="toggleAddUser()" class="btn-primary">+ Add User</button>
        </div>
        
        <div *ngIf="showAddUser" class="card" style="background: var(--surface); margin-bottom: 1.5rem; padding: 1.5rem;">
          <h3 class="text-lg font-semibold mb-4" style="color: var(--text)">Add New User</h3>
          <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1rem;">
            <div>
              <label style="display: block; font-size: 0.8125rem; font-weight: 500; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.07em; margin-bottom: 0.375rem;">Name</label>
              <input [(ngModel)]="newUser.nom" placeholder="Full name" class="field">
            </div>
            <div>
              <label style="display: block; font-size: 0.8125rem; font-weight: 500; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.07em; margin-bottom: 0.375rem;">Email</label>
              <input [(ngModel)]="newUser.email" placeholder="email@example.com" class="field" type="email">
            </div>
            <div>
              <label style="display: block; font-size: 0.8125rem; font-weight: 500; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.07em; margin-bottom: 0.375rem;">Password</label>
              <input [(ngModel)]="newUser.password" placeholder="Password" class="field" type="password">
            </div>
            <div>
              <label style="display: block; font-size: 0.8125rem; font-weight: 500; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.07em; margin-bottom: 0.375rem;">Role</label>
              <select [(ngModel)]="newUser.role" class="field">
                <option value="RECRUTEUR">Recruteur</option>
                <option value="ADMINISTRATEUR">Administrateur</option>
              </select>
            </div>
          </div>
          <div style="display: flex; gap: 0.75rem; margin-top: 1rem;">
            <button (click)="addNewUser()" [disabled]="savingUser" class="btn-primary">{{savingUser ? 'Creating...' : 'Create User'}}</button>
            <button (click)="showAddUser = false" class="btn-ghost">Cancel</button>
          </div>
        </div>
        
        <!-- Candidates -->
        <div class="mb-8">
          <h3 class="text-lg font-semibold mb-4" style="color: var(--lime)">Candidates ({{users.candidats?.length || 0}})</h3>
          <div class="overflow-x-auto">
            <table style="width: 100%; border-collapse: collapse">
              <thead>
                <tr style="border-bottom: 1px solid var(--border)">
                  <th style="text-align: left; padding: 0.75rem; color: var(--text-muted)">ID</th>
                  <th style="text-align: left; padding: 0.75rem; color: var(--text-muted)">Name</th>
                  <th style="text-align: left; padding: 0.75rem; color: var(--text-muted)">Email</th>
                  <th style="text-align: right; padding: 0.75rem; color: var(--text-muted)">Action</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let user of users.candidats" style="border-bottom: 1px solid var(--border)">
                  <td style="padding: 0.75rem; color: var(--text)">{{user.id}}</td>
                  <td style="padding: 0.75rem; color: var(--text)">{{user.nom}}</td>
                  <td style="padding: 0.75rem; color: var(--text)">{{user.email}}</td>
                  <td style="padding: 0.75rem; text-align: right">
                    <button (click)="deleteUser(user.id, 'candidat')" class="text-red-500 hover:text-red-700">Delete</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Recruiters -->
        <div class="mb-8">
          <h3 class="text-lg font-semibold mb-4" style="color: var(--accent)">Recruiters ({{users.recruteurs?.length || 0}})</h3>
          <div class="overflow-x-auto">
            <table style="width: 100%; border-collapse: collapse">
              <thead>
                <tr style="border-bottom: 1px solid var(--border)">
                  <th style="text-align: left; padding: 0.75rem; color: var(--text-muted)">ID</th>
                  <th style="text-align: left; padding: 0.75rem; color: var(--text-muted)">Name</th>
                  <th style="text-align: left; padding: 0.75rem; color: var(--text-muted)">Email</th>
                  <th style="text-align: right; padding: 0.75rem; color: var(--text-muted)">Action</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let user of users.recruteurs" style="border-bottom: 1px solid var(--border)">
                  <td style="padding: 0.75rem; color: var(--text)">{{user.id}}</td>
                  <td style="padding: 0.75rem; color: var(--text)">{{user.nom}}</td>
                  <td style="padding: 0.75rem; color: var(--text)">{{user.email}}</td>
                  <td style="padding: 0.75rem; text-align: right">
                    <button (click)="deleteUser(user.id, 'recruteur')" class="text-red-500 hover:text-red-700">Delete</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Admins -->
        <div>
          <h3 class="text-lg font-semibold mb-4" style="color: var(--gold)">Administrators ({{users.administrateurs?.length || 0}})</h3>
          <div class="overflow-x-auto">
            <table style="width: 100%; border-collapse: collapse">
              <thead>
                <tr style="border-bottom: 1px solid var(--border)">
                  <th style="text-align: left; padding: 0.75rem; color: var(--text-muted)">ID</th>
                  <th style="text-align: left; padding: 0.75rem; color: var(--text-muted)">Name</th>
                  <th style="text-align: left; padding: 0.75rem; color: var(--text-muted)">Email</th>
                  <th style="text-align: right; padding: 0.75rem; color: var(--text-muted)">Action</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let user of users.administrateurs" style="border-bottom: 1px solid var(--border)">
                  <td style="padding: 0.75rem; color: var(--text)">{{user.id}}</td>
                  <td style="padding: 0.75rem; color: var(--text)">{{user.nom}}</td>
                  <td style="padding: 0.75rem; color: var(--text)">{{user.email}}</td>
                  <td style="padding: 0.75rem; text-align: right">
                    <button (click)="deleteUser(user.id, 'administrateur')" class="text-red-500 hover:text-red-700">Delete</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <router-outlet></router-outlet>
    </div>
  `,
  styles: []
})
export class AdminComponent implements OnInit {
  stats: DashboardStats = { totalCandidates: 0, totalRecruiters: 0, totalOffres: 0, totalCandidatures: 0, totalTags: 0 };
  users: { candidats: User[], recruteurs: User[], administrateurs: User[] } = { candidats: [], recruteurs: [], administrateurs: [] };
  
  // Add user form
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

  loadCharts() {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    
    this.http.get<any[]>('http://localhost:8000/api/candidatures', { headers }).subscribe({
      next: (data) => {
        const counts = { aTrier: 0, entretien: 0, retenu: 0, refuse: 0 };
        data.forEach((c: any) => {
          if (c.statut === 'A_TRIER') counts.aTrier++;
          else if (c.statut === 'ENTRETIEN') counts.entretien++;
          else if (c.statut === 'RETENU') counts.retenu++;
          else if (c.statut === 'REFUSE') counts.refuse++;
        });
        this.statusCounts = counts;
        this.maxCount = Math.max(counts.aTrier, counts.entretien, counts.retenu, counts.refuse, 1);
      }
    });

    this.http.get<any[]>('http://localhost:8000/api/tags', { headers }).subscribe({
      next: (tags) => {
        const categories: { [key: string]: number } = {};
        tags.forEach((t: any) => {
          categories[t.categorie] = (categories[t.categorie] || 0) + 1;
        });
        const colors: { [key: string]: string } = {
          'TECH': '#3b82f6',
          'LANGUAGE': '#8b5cf6', 
          'SOFT': '#10b981',
          'TOOL': '#f59e0b'
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
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    this.http.get<DashboardStats>('http://localhost:8000/api/stats/dashboard', { headers }).subscribe({
      next: (data) => this.stats = data,
      error: () => {}
    });
  }

  loadUsers() {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    this.http.get<any>('http://localhost:8000/api/stats/users', { headers }).subscribe({
      next: (data) => this.users = data,
      error: () => {}
    });
  }

  deleteUser(id: number, type: string) {
    if (!confirm('Delete this ' + type + '? This cannot be undone!')) return;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    
    // Use GET endpoint for simplicity
    this.http.get('http://localhost:8000/api/stats/users/delete/' + id + '/' + type, { headers }).subscribe({
      next: () => {
        alert(type + ' deleted!');
        this.loadUsers();
      },
      error: (err) => {
        if (err.status === 500) {
          alert('Cannot delete this ' + type + ' - they may have associated data (jobs, applications, etc.).\n\nPlease remove their data first.');
        } else {
          alert('Failed to delete ' + type + ': ' + (err.error?.message || err.status));
        }
      }
    });
  }

  toggleAddUser() {
    this.showAddUser = !this.showAddUser;
  }

  addNewUser() {
    if (!this.newUser.nom || !this.newUser.email || !this.newUser.password) {
      alert('Please fill all fields');
      return;
    }
    this.savingUser = true;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`)
      .set('Content-Type', 'application/json');
    
    this.http.post('http://localhost:8000/api/auth/register', {
      nom: this.newUser.nom,
      email: this.newUser.email,
      motDePasse: this.newUser.password,
      role: this.newUser.role
    }, { headers }).subscribe({
      next: () => {
        alert('User created successfully!');
        this.savingUser = false;
        this.showAddUser = false;
        this.newUser = { nom: '', email: '', password: '', role: 'RECRUTEUR' };
        this.loadUsers();
      },
      error: (err) => {
        alert('Failed to create user: ' + (err.error?.message || err.status));
        this.savingUser = false;
      }
    });
  }
}