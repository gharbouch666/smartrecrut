import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss'],
})
export class SettingsComponent {
  profileForm: FormGroup;
  passwordForm: FormGroup;
  oldPassword = '';
  newPassword = '';
  confirmPassword = '';
  message = '';

  constructor(private http: HttpClient, private fb: FormBuilder) {
    this.profileForm = this.fb.group({
      nom: [''],
      email: [''],
    });
    this.passwordForm = this.fb.group({
      oldPassword: [''],
      newPassword: [''],
      confirmPassword: ['']
    });
  }

  saveProfile() {
    this.message = '';
    const nom = this.profileForm.value.nom;
    const email = this.profileForm.value.email;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.put(`http://localhost:8000/api/utilisateurs/profile`, { nom, email }, { headers }).subscribe({
      next: () => {
        this.message = 'Profile updated successfully';
        const userStr = localStorage.getItem('user');
        if (userStr) {
          const user = JSON.parse(userStr);
          localStorage.setItem('user', JSON.stringify({ ...user, nom, email }));
        }
      },
      error: (err) => {
        this.message = err.error || 'Failed to update profile';
      }
    });
  }

  changePassword() {
    if (this.passwordForm.invalid) return;
    const { oldPassword, newPassword, confirmPassword } = this.passwordForm.value;
    if (newPassword !== confirmPassword) {
      this.message = 'New passwords do not match';
      return;
    }
    const token = localStorage.getItem('accessToken');
    const userStr = localStorage.getItem('user');
    const user = userStr ? JSON.parse(userStr) : {};
    const email = user.email || '';
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const payload = { oldPassword, newPassword, confirmPassword };
    this.http.post(`http://localhost:8000/api/auth/change-password?email=${email}`, payload, {
      headers,
      responseType: 'text'
    }).subscribe({
      next: (response) => {
        this.message = response || 'Password changed successfully';
        this.passwordForm.reset();
      },
      error: (err) => {
        this.message = 'Password change failed';
      }
    });
  }
}