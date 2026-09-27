export type TicketStatus = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type TicketPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type TicketCategory = 'HARDWARE' | 'SOFTWARE' | 'NETWORK' | 'EMAIL' | 'ACCESS' | 'PRINTER' | 'SECURITY' | 'OTHER';

export interface Ticket {
  id: number;
  ticketNumber: string;
  title: string;
  description: string;
  category: TicketCategory;
  priority: TicketPriority;
  status: TicketStatus;
  createdByUsername?: string;
  assignedToUsername?: string;
  resolution?: string;
  createdAt?: string;
  updatedAt?: string;
  resolvedAt?: string;
  closedAt?: string;
}

export interface DashboardSummary {
  myTickets?: Ticket[];
  totalTickets?: Ticket[];
  recentTickets?: Ticket[];
  highPriorityTickets?: Ticket[];
  unassignedTickets?: Ticket[];
  myAssignedTickets?: Ticket[];
}
