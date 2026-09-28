import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RegisterRequest, Role } from '../../models/auth.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  form = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    username: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    employeeId: [''],
    department: [''],
    role: ['EMPLOYEE', [Validators.required]]
  });

  submitting = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  submit(): void {
    if (this.form.invalid) {
      this.errorMessage = 'Please fill in all required fields correctly.';
      return;
    }

    const payload: RegisterRequest = {
      firstName: this.form.value.firstName?.trim() ?? '',
      lastName: this.form.value.lastName?.trim() ?? '',
      username: this.form.value.username?.trim() ?? '',
      email: this.form.value.email?.trim() ?? '',
      password: this.form.value.password ?? '',
      employeeId: this.form.value.employeeId?.trim() ?? '',
      department: this.form.value.department?.trim() ?? '',
      role: (this.form.value.role as Role) ?? 'EMPLOYEE'
    };

    this.submitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register(payload).subscribe({
      next: () => {
        this.successMessage = 'Account created successfully. Redirecting to login...';
        setTimeout(() => this.router.navigate(['/login']), 1200);
      },
      error: () => {
        this.submitting = false;
        this.errorMessage = 'Unable to create account. Username or email may already exist.';
      }
    });
  }
}
