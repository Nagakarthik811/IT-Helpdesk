import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { EmployeeDashboardComponent } from './dashboard/employee-dashboard/employee-dashboard.component';
import { NewTicketComponent } from './tickets/new-ticket/new-ticket.component';
import { SupportDashboardComponent } from './dashboard/support-dashboard/support-dashboard.component';
import { AdminDashboardComponent } from './dashboard/admin-dashboard/admin-dashboard.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'employee',
    component: EmployeeDashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'support',
    component: SupportDashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'admin',
    component: AdminDashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'tickets/new',
    component: NewTicketComponent,
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '/login' }
];
