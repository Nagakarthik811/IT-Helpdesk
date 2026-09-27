import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TicketService } from '../../core/services/ticket.service';

@Component({
  selector: 'app-new-ticket',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './new-ticket.component.html',
  styleUrl: './new-ticket.component.css'
})
export class NewTicketComponent {
  form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(5)]],
    description: ['', [Validators.required, Validators.minLength(10)]],
    category: ['HARDWARE', Validators.required],
    priority: ['MEDIUM', Validators.required]
  });

  submitting = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private ticketService: TicketService,
    private router: Router
  ) {}

  submit(): void {
    if (this.form.invalid) {
      this.errorMessage = 'Please complete all required fields.';
      return;
    }

    this.submitting = true;
    this.ticketService.createTicket(this.form.value).subscribe({
      next: () => this.router.navigate(['/employee']),
      error: () => {
        this.submitting = false;
        this.errorMessage = 'Unable to create ticket. Please try again.';
      }
    });
  }
}
