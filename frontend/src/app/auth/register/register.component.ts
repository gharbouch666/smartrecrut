import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerForm: FormGroup;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      nom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      motDePasse: ['', [Validators.required, Validators.minLength(6)]],
      role: ['CANDIDAT', Validators.required]
    });
  }

  onSubmit() {
    if (this.registerForm.invalid) return;
    const formValue = this.registerForm.value;
    console.log('Register payload:', formValue);
    this.http.post('http://localhost:8000/api/auth/register', formValue).subscribe({
      next: (res) => {
        console.log('Register success:', res);
        alert('Registration successful. Please sign in.');
        this.router.navigate(['/auth/login']);
      },
      error: (err) => {
        console.error('Register error:', err);
        alert('Registration failed: ' + (err.error?.message || err.statusText || 'Unknown error'));
      }
    });
  }
}
