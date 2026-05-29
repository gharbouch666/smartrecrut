import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

interface Admin {
  id: number;
  nom: string;
  email: string;
  role: string;
  actif: boolean;
  dateCreation: string;
  dateModification: string;
}

@Component({
  selector: 'app-admin-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  styleUrls: ['./admin-profile.component.scss'],
  template: `
    <div style="max-width: 1200px; margin: 0 auto;">
      <!-- Header -->
      <div class="page-header" style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h1 class="page-title">My Profile</h1>
          <p class="page-subtitle">Manage your information and security</p>
        </div>
        <button (click)="configureMode = !configureMode" class="btn-primary">
          {{configureMode ? 'View Profile' : 'Configure Profile'}}
        </button>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="card" style="text-align: center; padding: 3rem;">
        <p class="mono" style="color: var(--text-muted);">Loading...</p>
      </div>

<!-- Basic Profile View -->
        <div *ngIf="!configureMode && !loading" class="grid" style="grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 1.5rem;">
          <!-- Profile Card -->
          <div class="card">
            <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1.5rem;">
              <div class="avatar" style="width: 64px; height: 64px; font-size: 1.5rem;">
                {{admin?.nom?.charAt(0)?.toUpperCase() || 'U'}}
              </div>
              <div>
                <h2 class="page-title" style="font-size: 1.5rem;">{{admin?.nom || 'Your Name'}}</h2>
                <p class="mono" style="font-size: 0.875rem; color: var(--text-muted);">{{admin?.email}}</p>
              </div>
            </div>
            
            <div style="display: flex; flex-direction: column; gap: 0.75rem;">
              <div style="padding: 0.75rem; background: var(--surface); border-radius: 8px;">
                <p class="mono" style="font-size: 0.6875rem; color: var(--text-muted); text-transform: uppercase;">Role</p>
                <p style="font-weight: 500; color: var(--text);">{{admin?.role || 'Admin'}}</p>
              </div>
              <div style="padding: 0.75rem; background: var(--surface); border-radius: 8px;">
                <p class="mono" style="font-size: 0.6875rem; color: var(--text-muted); text-transform: uppercase;">Status</p>
                <p style="font-weight: 500; color: var(--text);">{{admin?.actif ? 'Active' : 'Inactive'}}</p>
              </div>
              <div style="padding: 0.75rem; background: var(--surface); border-radius: 8px;">
                <p class="mono" style="font-size: 0.6875rem; color: var(--text-muted); text-transform: uppercase;">Member Since</p>
                <p style="font-weight: 500; color: var(--text);">{{admin?.dateCreation | date:'mediumDate'}}</p>
              </div>
            </div>
          </div>

          <!-- Security Card -->
          <div class="card">
            <h2 class="page-title" style="font-size: 1.25rem; margin-bottom: 1rem;">Security</h2>
            <div style="display: flex; flex-direction: column; gap: 1rem;">
              <div style="padding: 1rem; background: var(--surface); border-radius: 8px;">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <div>
                    <p style="font-weight: 600; color: var(--text);">Password</p>
                    <p class="mono" style="font-size: 0.75rem; color: var(--text-muted);">Last changed: Never</p>
                  </div>
                  <button (click)="configureMode = true; activeTab = 'security'" class="btn-primary" style="padding: 0.5rem 1rem;">Change</button>
                </div>
              </div>
            </div>
          </div>
        </div>

      <!-- Configure Mode -->
      <div *ngIf="configureMode && !loading">
        <div style="display: flex; justify-content: flex-end; margin-bottom: 1.5rem; gap: 1rem;">
          <button (click)="saveProfile()" [disabled]="saving" class="btn-primary">
            {{saving ? 'Saving...' : 'Save Changes'}}
          </button>
          <span *ngIf="saved" class="pill pill-green">Saved!</span>
        </div>

        <!-- Tabs -->
        <div style="display: flex; gap: 0.25rem; margin-bottom: 1.5rem; background: var(--card); border: 1px solid var(--border); border-radius: 8px; padding: 0.25rem;">
          <button (click)="activeTab = 'info'" 
            style="flex: 1; padding: 0.75rem 1rem; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;"
            [style.background]="activeTab === 'info' ? 'var(--accent)' : 'transparent'"
            [style.color]="activeTab === 'info' ? 'white' : 'var(--text-muted)'">
            Info
          </button>
          <button (click)="activeTab = 'security'" 
            style="flex: 1; padding: 0.75rem 1rem; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;"
            [style.background]="activeTab === 'security' ? 'var(--accent)' : 'transparent'"
            [style.color]="activeTab === 'security' ? 'white' : 'var(--text-muted)'">
            Security
          </button>
        </div>

        <!-- Info Tab -->
        <div *ngIf="activeTab === 'info'">
          <div class="card">
            <h2 class="page-title" style="font-size: 1rem; margin-bottom: 1.5rem;">Personal Information</h2>
            <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1rem;">
              <div>
                <label class="form-label">Full Name</label>
                <input type="text" [(ngModel)]="admin!.nom" class="field"/>
              </div>
              <div>
                <label class="form-label">Email</label>
                <input type="email" [(ngModel)]="admin!.email" class="field"/>
              </div>
            </div>
          </div>
        </div>

        <!-- Security Tab -->
        <div *ngIf="activeTab === 'security'">
          <div class="card">
            <h2 class="page-title" style="font-size: 1rem; margin-bottom: 1.5rem;">Password Change</h2>
            <div style="display: grid; gap: 1rem;">
              <div>
                <label class="form-label">Current Password</label>
                <input type="password" [(ngModel)]="currentPassword" class="field" placeholder="Enter current password"/>
              </div>
              <div>
                <label class="form-label">New Password</label>
                <input type="password" [(ngModel)]="newPassword" class="field" placeholder="Enter new password"/>
              </div>
              <div>
                <label class="form-label">Confirm New Password</label>
                <input type="password" [(ngModel)]="confirmPassword" class="field" placeholder="Confirm new password"/>
              </div>
              <div>
                <button (click)="changePassword()" [disabled]="changingPassword" class="btn-primary" style="width: 100%;">
                  {{changingPassword ? 'Changing...' : 'Change Password'}}
                </button>
                <span *ngIf="passwordChanged" class="pill pill-green">Password changed!</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class AdminProfileComponent implements OnInit {
  loading = true;
  saving = false;
  saved = false;
  changingPassword = false;
  passwordChanged = false;
  activeTab: 'info' | 'security' = 'info';
  configureMode = false;
  
  admin: Admin | null = null;
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadAdmin();
  }

  loadAdmin() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      alert('Please login first');
      return;
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get<any>('http://localhost:8000/api/auth/me', { headers }).subscribe({
      next: (user) => {
        this.admin = {
          id: user.id,
          nom: user.nom,
          email: user.email,
          role: user.role,
          actif: user.actif,
          dateCreation: user.dateCreation,
          dateModification: user.dateModification
        };
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        alert('Failed to load profile');
      }
    });
  }

  saveProfile() {
    if (!this.admin) return;
    this.saving = true;
    this.saved = false;
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`)
      .set('Content-Type', 'application/json');
    
    const updateData = {
      nom: this.admin.nom,
      email: this.admin.email
    };
    
    this.http.put(`http://localhost:8000/api/administrateurs/${this.admin.id}`, updateData, { headers }).subscribe({
      next: () => {
        this.saving = false;
        this.saved = true;
        // Reload to get updated data
        this.loadAdmin();
        setTimeout(() => this.saved = false, 3000);
      },
      error: (err) => {
        this.saving = false;
        console.error('Save error:', err);
        alert('Failed to save profile');
      }
    });
  }

  changePassword() {
    if (!this.admin) return;
    if (this.newPassword !== this.confirmPassword) {
      alert('New passwords do not match');
      return;
    }
    this.changingPassword = true;
    this.passwordChanged = false;
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`)
      .set('Content-Type', 'application/json');
    
    const passwordData = {
      currentPassword: this.currentPassword,
      newPassword: this.newPassword
    };
    
    this.http.post(`http://localhost:8000/api/auth/change-password`, passwordData, { headers }).subscribe({
      next: () => {
        this.changingPassword = false;
        this.passwordChanged = true;
        // Clear the password fields
        this.currentPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
        setTimeout(() => this.passwordChanged = false, 3000);
      },
      error: (err) => {
        this.changingPassword = false;
        console.error('Password change error:', err);
        alert('Failed to change password: ' + (err.error?.message || 'Invalid current password'));
      }
    });
  }
}