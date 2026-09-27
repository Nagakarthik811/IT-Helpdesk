import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { TicketService } from '../../core/services/ticket.service';

@Component({
  selector: 'app-employee-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './employee-dashboard.component.html',
  styleUrl: './employee-dashboard.component.css'
})
export class EmployeeDashboardComponent implements OnInit {
  user = this.authService.getCurrentUser();
  tickets: any[] = [];
  recentTickets: any[] = [];

  constructor(
    private authService: AuthService,
    private ticketService: TicketService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.ticketService.getDashboard('EMPLOYEE').subscribe({
      next: (data) => {
        this.tickets = data.myTickets ?? [];
        this.recentTickets = data.recentTickets ?? [];
      },
      error: () => this.router.navigate(['/login'])
    });
  }

  priorityClass(ticket: any): string {
    return (ticket?.priority ?? 'normal').toLowerCase();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
