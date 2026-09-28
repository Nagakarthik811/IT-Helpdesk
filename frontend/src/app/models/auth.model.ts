export type Role = 'EMPLOYEE' | 'IT_SUPPORT' | 'ADMIN';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  employeeId?: string;
  department?: string;
  role: Role;
}

export interface AuthResponse {
  token: string;
  username: string;
  firstName: string;
  lastName: string;
  role: Role;
}

export interface UserProfile {
  username: string;
  firstName: string;
  lastName: string;
  role: Role;
}
