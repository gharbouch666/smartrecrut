import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="p-8 max-w-7xl mx-auto min-h-[calc(100vh-2rem)]">
      <div class="flex justify-between items-center mb-8">
        <div class="space-y-1">
          <h1 class="text-3xl font-bold text-[var(--text)]">Settings</h1>
          <p class="text-[var(--text-muted)]">Manage your account preferences</p>
        </div>
      </div>

      <div class="bg-[var(--card)] rounded-2xl shadow-lg border border-[var(--border)] p-6 mb-6">
        <h2 class="text-xl font-semibold text-[var(--text)] mb-6">Change Password</h2>
        <form (ngSubmit)="changePassword()" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-[var(--text)] mb-2">Current Password</label>
            <input type="password" [(ngModel)]="oldPassword" name="oldPassword" required class="w-full px-4 py-3 bg-[var(--surface)] border border-[var(--border)] rounded-xl focus:border-[var(--accent)] focus:outline-none transition-all text-[var(--text)]">
          </div>
          <div>
            <label class="block text-sm font-medium text-[var(--text)] mb-2">New Password</label>
            <input type="password" [(ngModel)]="newPassword" name="newPassword" required class="w-full px-4 py-3 bg-[var(--surface)] border border-[var(--border)] rounded-xl focus:border-[var(--accent)] focus:outline-none transition-all text-[var(--text)]">
          </div>
          <div>
            <label class="block text-sm font-medium text-[var(--text)] mb-2">Confirm New Password</label>
            <input type="password" [(ngModel)]="confirmPassword" name="confirmPassword" required class="w-full px-4 py-3 bg-[var(--surface)] border border-[var(--border)] rounded-xl focus:border-[var(--accent)] focus:outline-none transition-all text-[var(--text)]">
          </div>
          <button type="submit" class="px-6 py-3 bg-[var(--accent)] text-white rounded-xl font-medium hover:opacity-90 transition-all shadow-lg">Update Password</button>
        </form>
        <p *ngIf="message" class="mt-4 text-sm" [class.text-green-500]="message.includes('success')" [class.text-red-500]="message.includes('failed')">{{message}}</p>
      </div>
    </div>
  `
})
export class SettingsComponent {
  oldPassword = '';
  newPassword = '';
  confirmPassword = '';
  message = '';

  constructor(private http: HttpClient) {}

  changePassword() {
    if (this.newPassword !== this.confirmPassword) {
      this.message = 'New passwords do not match';
      return;
    }
    const token = localStorage.getItem('accessToken');
    const userStr = localStorage.getItem('user');
    const user = userStr ? JSON.parse(userStr) : {};
    const email = user.email || '';
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const payload = { oldPassword: this.oldPassword, newPassword: this.newPassword, confirmPassword: this.confirmPassword };
    this.http.post(`http://localhost:8000/api/auth/change-password?email=${email}`, payload, { 
      headers,
      responseType: 'text'
    }).subscribe({
      next: (response) => { 
        this.message = response || 'Password changed successfully'; 
        this.oldPassword = ''; 
        this.newPassword = ''; 
        this.confirmPassword = ''; 
      },
      error: (err) => { 
        console.error('[DEBUG] Password change error:', err);
        const errMsg = err.error;
        if (typeof errMsg === 'string') {
          this.message = errMsg;
        } else if (errMsg && typeof errMsg.message === 'string') {
          this.message = errMsg.message;
        } else if (err.statusText) {
          this.message = err.statusText;
        } else {
          this.message = 'Password change failed';
        }
      }
    });
  }
}