import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { TicketService } from '../../core/services/ticket.service';

@Component({
  selector: 'app-support-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './support-dashboard.component.html',
  styleUrl: './support-dashboard.component.css'
})
export class SupportDashboardComponent implements OnInit {
  user = this.authService.getCurrentUser();
  supportName = this.user ? `${this.user.firstName} ${this.user.lastName}`.trim() || this.user.username : 'Support agent';
  totalTickets: any[] = [];
  recentTickets: any[] = [];
  highPriorityTickets: any[] = [];
  unassignedTickets: any[] = [];
  myAssignedTickets: any[] = [];
  supportUserId: number | null = null;

  constructor(
    private authService: AuthService,
    private ticketService: TicketService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
    this.loadSupportUser();
  }

  loadDashboard(): void {
    this.ticketService.getDashboard('IT_SUPPORT').subscribe({
      next: (data) => {
        this.totalTickets = data.totalTickets ?? [];
        this.recentTickets = data.recentTickets ?? [];
        this.highPriorityTickets = data.highPriorityTickets ?? [];
        this.unassignedTickets = data.unassignedTickets ?? [];
        this.myAssignedTickets = data.myAssignedTickets ?? [];
        this.supportName = this.authService.getCurrentUser()
          ? `${this.authService.getCurrentUser()!.firstName} ${this.authService.getCurrentUser()!.lastName}`.trim() || this.authService.getCurrentUser()!.username
          : 'Support agent';
      },
      error: () => this.router.navigate(['/login'])
    });
  }

  loadSupportUser(): void {
    this.ticketService.getAllUsers().subscribe({
      next: (users) => {
        const current = users.find((user) => user.username === this.user?.username);
        this.supportUserId = current?.id ?? null;
      },
      error: () => this.supportUserId = null
    });
  }

  priorityClass(ticket: any): string {
    return (ticket?.priority ?? 'normal').toLowerCase();
  }

  queuePreview(): any[] {
    return this.totalTickets.slice(0, 8);
  }

  takeTicket(ticket: any): void {
    if (!this.supportUserId) {
      return;
    }

    this.ticketService.assignTicket(ticket.id, this.supportUserId).subscribe({
      next: () => this.loadDashboard(),
      error: () => this.loadDashboard()
    });
  }

  updateTicketStatus(ticket: any, status: 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED'): void {
    const comment = status === 'RESOLVED'
      ? 'Resolved via support dashboard action.'
      : 'Status updated from support dashboard.';

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
