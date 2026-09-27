import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

export const roleGuard = (role: 'EMPLOYEE' | 'IT_SUPPORT' | 'ADMIN'): CanActivateFn => () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated() && authService.getCurrentUser()?.role === role) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
