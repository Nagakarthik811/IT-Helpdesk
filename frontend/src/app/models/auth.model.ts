export type Role = 'EMPLOYEE' | 'IT_SUPPORT' | 'ADMIN';

export interface LoginRequest {
  username: string;
  password: string;
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
