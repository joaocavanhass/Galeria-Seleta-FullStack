import { Injectable, signal, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface UsuarioLogado {
  id: number;
  nome: string;
  email: string;
  telefone: string | null;
  cpf: string | null;
  papel: string;
  criadoEm: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  usuario: UsuarioLogado;
}

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface RegistroRequest {
  nome: string;
  email: string;
  senha: string;
  telefone?: string;
  cpf?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY   = 'gs_access_token';
  private readonly REFRESH_KEY = 'gs_refresh_token';
  private readonly USER_KEY    = 'gs_user';

  currentUser = signal<UsuarioLogado | null>(this.carregarUsuarioStorage());

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: object
  ) {}

  login(dados: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, dados)
      .pipe(tap(res => this.salvarSessao(res)));
  }

  registrar(dados: RegistroRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/register`, dados)
      .pipe(tap(res => this.salvarSessao(res)));
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.REFRESH_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
    this.currentUser.set(null);
  }

  getToken(): string | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAutenticado(): boolean {
    return this.currentUser() !== null;
  }

  patchUsuario(dados: Partial<UsuarioLogado>): void {
    const atual = this.currentUser();
    if (!atual) return;
    const atualizado = { ...atual, ...dados };
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.USER_KEY, JSON.stringify(atualizado));
    }
    this.currentUser.set(atualizado);
  }

  private salvarSessao(res: AuthResponse): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.TOKEN_KEY,   res.accessToken);
      localStorage.setItem(this.REFRESH_KEY, res.refreshToken);
      localStorage.setItem(this.USER_KEY,    JSON.stringify(res.usuario));
    }
    this.currentUser.set(res.usuario);
  }

  private carregarUsuarioStorage(): UsuarioLogado | null {
    try {
      if (typeof localStorage === 'undefined') return null;
      const raw = localStorage.getItem(this.USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }
}
