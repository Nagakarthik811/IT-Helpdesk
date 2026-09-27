import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  form = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  submitting = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  submit(): void {
    if (this.form.invalid) {
      this.errorMessage = 'Please enter username and password.';
      return;
    }

    this.submitting = true;
    this.errorMessage = '';

    this.authService.login({
      username: this.form.value.username ?? '',
      password: this.form.value.password ?? ''
    }).subscribe({
      next: () => {
        const role = this.authService.getCurrentUser()?.role;
        if (role === 'ADMIN') this.router.navigate(['/admin']);
        else if (role === 'IT_SUPPORT') this.router.navigate(['/support']);
        else this.router.navigate(['/employee']);
      },
      error: () => {
        this.submitting = false;
        this.errorMessage = 'Invalid username or password.';
      }
    });
  }
}
