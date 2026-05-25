import { Injectable, computed } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, UsuarioLogado } from '../../core/services/auth.service';

export type AdminUser = UsuarioLogado;

@Injectable({ providedIn: 'root' })
export class AdminAuthService {

  // Sempre em sincronia com o AuthService principal
  currentAdmin = computed(() => this.authService.currentUser());

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this.authService.currentUser()?.papel === 'admin';
  }

  // Mantido para não quebrar chamadas legadas
  tryAdminLogin(_email: string, _senha: string): boolean {
    return false;
  }

  setAdmin(_usuario: AdminUser): void {
    // Não precisa mais: currentAdmin deriva do AuthService automaticamente
  }
}
