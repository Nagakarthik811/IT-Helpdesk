import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Ticket } from '../../tickets/ticket.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TicketService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getMyTickets(): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/tickets`);
  }

  getDashboard(role: 'EMPLOYEE' | 'IT_SUPPORT' | 'ADMIN'): Observable<any> {
    const path = role === 'IT_SUPPORT' ? 'support' : role.toLowerCase();
    return this.http.get<any>(`${this.apiUrl}/dashboard/${path}`);
  }

  getAllUsers(): Observable<any[]> {
    return this.http.get<any>(`${this.apiUrl}/users?page=0&size=100`).pipe(
      map((response) => response?.content ?? response ?? [])
    );
  }

  assignTicket(ticketId: number, supportUserId: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/tickets/${ticketId}/assign?supportUserId=${supportUserId}`, {});
  }

  updateTicketStatus(ticketId: number, status: string, comment?: string): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/tickets/${ticketId}/status`, { status, comment });
  }

  createTicket(payload: any): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/tickets`, payload);
  }

  getTicket(id: number): Observable<Ticket> {
    return this.http.get<Ticket>(`${this.apiUrl}/tickets/${id}`);
  }

  deleteTicket(ticketId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tickets/${ticketId}`);
  }
}
