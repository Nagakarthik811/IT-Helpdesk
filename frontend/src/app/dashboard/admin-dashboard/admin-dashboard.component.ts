import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { TicketService } from '../../core/services/ticket.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  user = this.authService.getCurrentUser();
  totalTickets: any[] = [];
  recentTickets: any[] = [];
  supportUsers: any[] = [];
  assignmentSelections: Record<number, number> = {};

  constructor(
    private authService: AuthService,
    private ticketService: TicketService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
    this.loadSupportUsers();
  }

  loadDashboard(): void {
    this.ticketService.getDashboard('ADMIN').subscribe({
      next: (data) => {
        this.totalTickets = data.totalTickets ?? [];
        this.recentTickets = data.recentTickets ?? [];
      },
      error: () => this.router.navigate(['/login'])
    });
  }

  loadSupportUsers(): void {
    this.ticketService.getAllUsers().subscribe({
      next: (users) => {
        this.supportUsers = users.filter((user) => user.role === 'IT_SUPPORT');
        this.totalTickets.forEach((ticket) => {
          if (ticket.assignedToUsername) {
            const selectedUser = this.supportUsers.find((u) => u.username === ticket.assignedToUsername);
            if (selectedUser) {
              this.assignmentSelections[ticket.id] = selectedUser.id;
            }
          }
        });
      },
      error: () => this.supportUsers = []
    });
  }

  openTicketsCount(): number {
    return this.totalTickets.filter((ticket) => ticket.status !== 'CLOSED').length;
  }

  recentTicketPreview(): any[] {
    return this.recentTickets.slice(0, 8);
  }

  priorityClass(ticket: any): string {
    return (ticket?.priority ?? 'normal').toLowerCase();
  }

  assignToSupport(ticket: any): void {
    const supportUserId = this.assignmentSelections[ticket.id];
    if (!supportUserId) {
      return;
    }

    this.ticketService.assignTicket(ticket.id, supportUserId).subscribe({
      next: () => this.loadDashboard(),
      error: () => this.loadDashboard()
    });
  }

  updateTicketStatus(ticket: any, status: 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED'): void {
    const comment = status === 'RESOLVED'
      ? 'Resolved by admin action.'
      : status === 'CLOSED'
        ? 'Closed by admin action.'
        : 'Status updated by admin dashboard.';

    this.ticketService.updateTicketStatus(ticket.id, status, comment).subscribe({
      next: () => this.loadDashboard(),
      error: () => this.loadDashboard()
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
