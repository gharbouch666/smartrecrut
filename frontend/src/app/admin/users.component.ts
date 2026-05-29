import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

interface User {
  id: number;
  nom: string;
  email: string;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  styleUrls: ['./users.component.scss'],
  template: `
    <div style="max-width: 1400px; margin: 0 auto;">
      <div class="page-header">
        <h1 class="page-title">Users Management</h1>
        <p class="page-subtitle">Add, edit, or remove system users</p>
      </div>

      <!-- Add User Form -->
      <div *ngIf="showAddUser" class="card" style="margin-bottom: 2rem;">
        <h2 class="page-title" style="font-size: 1.25rem; margin-bottom: 1.5rem;">Add New User</h2>
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
          <div>
            <label class="form-label">Name</label>
            <input [(ngModel)]="newUser.nom" placeholder="Full name" class="field">
          </div>
          <div>
            <label class="form-label">Email</label>
            <input [(ngModel)]="newUser.email" placeholder="email@example.com" class="field" type="email">
          </div>
          <div>
            <label class="form-label">Password</label>
            <input [(ngModel)]="newUser.password" placeholder="Password" class="field" type="password">
          </div>
          <div>
            <label class="form-label">Role</label>
            <select [(ngModel)]="newUser.role" class="field">
              <option value="RECRUTEUR">Recruteur</option>
              <option value="ADMINISTRATEUR">Administrateur</option>
              <option value="CANDIDAT">Candidat</option>
            </select>
          </div>
        </div>
        <div style="display: flex; gap: 0.75rem; margin-top: 1rem;">
          <button (click)="addNewUser()" [disabled]="savingUser" class="btn-primary">{{savingUser ? 'Creating...' : 'Create User'}}</button>
          <button (click)="showAddUser = false" class="btn-ghost">Cancel</button>
        </div>
      </div>

      <!-- Users Table -->
      <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
          <h2 class="page-title" style="font-size: 1.25rem;">All Users</h2>
          <button (click)="showAddUser = true" class="btn-primary">+ Add User</button>
        </div>

        <!-- Candidates -->
        <div style="margin-bottom: 2rem;">
           <h3 class="pill pill-green" style="margin-bottom: 1rem;">Candidates ({{users.candidats.length || 0}})</h3>
          <div class="data-table-wrap">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th style="text-align: right;">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let user of users.candidats">
                  <td class="mono">{{user.id}}</td>
                  <td>{{user.nom}}</td>
                  <td>{{user.email}}</td>
                  <td style="text-align: right;">
                    <button (click)="editUser(user)" class="btn-ghost" style="padding: 0.375rem 0.75rem; font-size: 0.75rem;">Edit</button>
                    <button (click)="deleteUser(user.id, 'candidat')" class="btn-danger" style="padding: 0.375rem 0.75rem; font-size: 0.75rem; margin-left: 0.5rem;">Delete</button>
                  </td>
                </tr>
                <tr *ngIf="!users.candidats.length">
                  <td colspan="4" style="text-align: center; color: var(--text-muted); padding: 2rem;">No candidates</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Recruiters -->
        <div style="margin-bottom: 2rem;">
           <h3 class="pill pill-blue" style="margin-bottom: 1rem;">Recruiters ({{users.recruteurs.length || 0}})</h3>
          <div class="data-table-wrap">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th style="text-align: right;">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let user of users.recruteurs">
                  <td class="mono">{{user.id}}</td>
                  <td>{{user.nom}}</td>
                  <td>{{user.email}}</td>
                  <td style="text-align: right;">
                    <button (click)="editUser(user)" class="btn-ghost" style="padding: 0.375rem 0.75rem; font-size: 0.75rem;">Edit</button>
                    <button (click)="deleteUser(user.id, 'recruteur')" class="btn-danger" style="padding: 0.375rem 0.75rem; font-size: 0.75rem; margin-left: 0.5rem;">Delete</button>
                  </td>
                </tr>
                <tr *ngIf="!users.recruteurs?.length">
                  <td colspan="4" style="text-align: center; color: var(--text-muted); padding: 2rem;">No recruiters</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Admins -->
        <div>
          <h3 class="pill pill-amber" style="margin-bottom: 1rem;">Administrateurs ({{users.administrateurs.length || 0}})</h3>
          <div class="data-table-wrap">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th style="text-align: right;">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let user of users.administrateurs">
                  <td class="mono">{{user.id}}</td>
                  <td>{{user.nom}}</td>
                  <td>{{user.email}}</td>
                  <td style="text-align: right;">
                    <button (click)="editUser(user)" class="btn-ghost" style="padding: 0.375rem 0.75rem; font-size: 0.75rem;">Edit</button>
                    <button (click)="deleteUser(user.id, 'administrateur')" class="btn-danger" style="padding: 0.375rem 0.75rem; font-size: 0.75rem; margin-left: 0.5rem;">Delete</button>
                  </td>
                </tr>
                <tr *ngIf="!users.administrateurs?.length">
                  <td colspan="4" style="text-align: center; color: var(--text-muted); padding: 2rem;">No administrators</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Edit User Modal -->
      <div *ngIf="editingUser" style="position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100;">
        <div class="card" style="max-width: 400px; width: 90%;">
          <h2 class="page-title" style="font-size: 1.25rem; margin-bottom: 1.5rem;">Edit User</h2>
          <div style="display: flex; flex-direction: column; gap: 1rem;">
            <div>
              <label class="form-label">Name</label>
              <input [(ngModel)]="editUserData.nom" class="field">
            </div>
            <div>
              <label class="form-label">Email</label>
              <input [(ngModel)]="editUserData.email" class="field" type="email">
            </div>
            <div>
              <label class="form-label">New Password (leave blank to keep current)</label>
              <input [(ngModel)]="editUserData.password" class="field" type="password" placeholder="New password">
            </div>
          </div>
          <div style="display: flex; gap: 0.75rem; margin-top: 1.5rem;">
            <button (click)="saveEdit()" [disabled]="savingEdit" class="btn-primary">{{savingEdit ? 'Saving...' : 'Save Changes'}}</button>
            <button (click)="editingUser = false" class="btn-ghost">Cancel</button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class UsersComponent implements OnInit {
  users: { candidats: User[], recruteurs: User[], administrateurs: User[] } = { candidats: [], recruteurs: [], administrateurs: [] };
  
  showAddUser = false;
  newUser = { nom: '', email: '', password: '', role: 'RECRUTEUR' };
  savingUser = false;

  editingUser = false;
  editUserData: any = {};
  editingId: number | null = null;
  editingType: string = '';
  savingEdit = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    this.http.get<any>('http://localhost:8000/api/stats/users', { headers }).subscribe({
      next: (data) => this.users = data,
      error: () => {}
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

  editUser(user: User) {
    this.editingId = user.id;
    this.editUserData = { nom: user.nom, email: user.email, password: '' };
    // Figure out which type
     if (this.users.candidats.find(u => u.id === user.id)) {
      this.editingType = 'candidat';
     } else if (this.users.recruteurs.find(u => u.id === user.id)) {
      this.editingType = 'recruteur';
    } else {
      this.editingType = 'administrateur';
    }
    this.editingUser = true;
  }

  saveEdit() {
    if (!this.editUserData.nom || !this.editUserData.email) {
      alert('Name and email required');
      return;
    }
    this.savingEdit = true;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`)
      .set('Content-Type', 'application/json');
    
    // Use stats endpoint to update
    const updateData: any = {
      nom: this.editUserData.nom,
      email: this.editUserData.email
    };
    if (this.editUserData.password) {
      updateData.motDePasse = this.editUserData.password;
    }

    this.http.put(`http://localhost:8000/api/stats/users/update/${this.editingId}/${this.editingType}`, updateData, { headers }).subscribe({
      next: () => {
        alert('User updated!');
        this.savingEdit = false;
        this.editingUser = false;
        this.loadUsers();
      },
      error: (err) => {
        alert('Failed to update: ' + (err.error?.message || err.status));
        this.savingEdit = false;
      }
    });
  }

  deleteUser(id: number, type: string) {
    if (!confirm('Delete this ' + type + '? This cannot be undone!')) return;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    
    this.http.get('http://localhost:8000/api/stats/users/delete/' + id + '/' + type, { headers }).subscribe({
      next: () => {
        alert(type + ' deleted!');
        this.loadUsers();
      },
      error: (err) => {
        if (err.status === 500) {
          alert('Cannot delete this ' + type + ' - they may have associated data (jobs, applications, etc.).\n\nPlease remove their data first.');
        } else {
          alert('Failed to delete: ' + (err.error?.message || err.status));
        }
      }
    });
  }
}