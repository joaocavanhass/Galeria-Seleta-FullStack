import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';

export interface AdminUser {
  id: number;
  nome: string;
  email: string;
  papel: 'admin';
}

@Injectable({ providedIn: 'root' })
export class AdminAuthService {
  private readonly STORAGE_KEY = 'gs_admin_token';

  private readonly MOCK_ADMIN = {
    id: 1,
    nome: 'Administrador',
    email: 'admin@galeriaseleta.com',
    senha: 'admin123',
    papel: 'admin' as const
  };

  currentAdmin = signal<AdminUser | null>(this.loadFromStorage());

  constructor(private router: Router) {}

  /** Chamado pelo LoginComponent ao fazer submit */
  tryAdminLogin(email: string, senha: string): boolean {
    if (email === this.MOCK_ADMIN.email && senha === this.MOCK_ADMIN.senha) {
      const user: AdminUser = {
        id: this.MOCK_ADMIN.id,
        nome: this.MOCK_ADMIN.nome,
        email: this.MOCK_ADMIN.email,
        papel: this.MOCK_ADMIN.papel
      };
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(user));
      this.currentAdmin.set(user);
      return true;
    }
    return false;
  }

  logout(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this.currentAdmin.set(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this.currentAdmin() !== null;
  }

  private loadFromStorage(): AdminUser | null {
    try {
      const raw = localStorage.getItem(this.STORAGE_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }
}