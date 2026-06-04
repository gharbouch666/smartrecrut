import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  forgotForm: FormGroup;
  message: string = '';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

    onSubmit() {
      if (this.forgotForm.invalid) return;

      this.http.post(`${environment.apiUrl}/auth/forgot-password`, 
        this.forgotForm.value).subscribe({
        next: () => {
          this.message = 'If your email exists in our system, you will receive a verification code shortly.';
          setTimeout(() => {
            this.router.navigate(['/auth/reset-password'], { queryParams: { email: this.forgotForm.value.email } });
          }, 2000);
        },
        error: (error) => {
          console.error('Forgot password error:', error);
          this.message = 'An error occurred. Please try again.';
        }
      });
    }

    goHome(event: MouseEvent) {
      event.preventDefault();
      this.router.navigate(['/']);
    }
}