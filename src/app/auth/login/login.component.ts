import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) return;
    const loginData = {
      username: this.loginForm.get('email')?.value,
      password: this.loginForm.get('password')?.value
    };
    this.http.post('http://localhost:8000/api/auth/login', loginData).subscribe({
      next: (res: any) => {
        localStorage.setItem('accessToken', res.accessToken);
        localStorage.setItem('refreshToken', res.refreshToken);
        
        // Get user info and redirect based on role
        const headers = new HttpHeaders().set('Authorization', `Bearer ${res.accessToken}`);
        this.http.get<any>('http://localhost:8000/api/auth/me', { headers }).subscribe({
          next: (user: any) => {
            // Store user object as JSON for components that expect it
            const userData = {
              id: user.id,
              type: user.role,
              nom: user.nom,
              email: user.email
            };
            localStorage.setItem('user', JSON.stringify(userData));
            // Also store individual items for backward compatibility
            localStorage.setItem('userRole', user.role);
            localStorage.setItem('userId', String(user.id));
            localStorage.setItem('userNom', user.nom);
            
            // Route based on role - go to dashboard first
            if (user.role === 'RECRUTEUR') {
              this.router.navigate(['/dashboard']);
            } else if (user.role === 'CANDIDAT') {
              this.router.navigate(['/dashboard']);
            } else if (user.role === 'ADMINISTRATEUR') {
              this.router.navigate(['/admin']);
            } else {
              this.router.navigate(['/dashboard']);
            }
          },
          error: () => this.router.navigate(['/dashboard'])
        });
      },
      error: () => alert('Email ou mot de passe incorrect')
    });
  }
}

