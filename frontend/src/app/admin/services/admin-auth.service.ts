// ============================================================
// ARQUIVO: admin-auth.service.ts
// FUNÇÃO: Serviço de autenticação específico para o painel administrativo.
//
// DESIGN PATTERN — FACADE (Fachada):
// Este serviço não armazena estado próprio. Ele delega tudo para o
// AuthService principal e expõe uma interface simplificada para o admin.
// É um "atalho" que os componentes admin usam em vez do AuthService diretamente.
//
// POR QUE EXISTE SEPARADO DO AuthService?
// Permite que o painel admin tenha suas próprias abstrações e métodos
// específicos sem poluir o AuthService principal, que é usado por
// toda a aplicação. Se o admin precisar de lógica extra no futuro,
// fica isolada aqui.
//
// COMPUTED SIGNAL:
// currentAdmin é derivado automaticamente de authService.currentUser().
// Quando o usuário faz login/logout no AuthService, currentAdmin
// é atualizado automaticamente sem necessidade de código extra.
//
// MÉTODOS LEGADOS:
// tryAdminLogin() e setAdmin() existem para compatibilidade com código
// antigo que usava autenticação separada. Hoje não fazem nada — a
// autenticação é toda gerenciada pelo AuthService via JWT.
// ============================================================

// Injectable: marca como serviço | computed: Signal derivado de outro Signal
import { Injectable, computed } from '@angular/core';
// Router: serviço de navegação (para redirecionar após logout)
import { Router } from '@angular/router';
// AuthService e UsuarioLogado: serviço principal de autenticação
import { AuthService, UsuarioLogado } from '../../core/services/auth.service';

// AdminUser é apenas um alias para UsuarioLogado — mesmo tipo, nome diferente por clareza
export type AdminUser = UsuarioLogado;

// @Injectable({ providedIn: 'root' }): instância única para toda a aplicação
@Injectable({ providedIn: 'root' })
export class AdminAuthService {

  // currentAdmin: Signal derivado do AuthService principal.
  // computed(): cria um Signal somente-leitura que recalcula automaticamente
  //             quando authService.currentUser() muda.
  // Isso garante que currentAdmin está sempre sincronizado com o estado de login.
  currentAdmin = computed(() => this.authService.currentUser());

  // Construtor: injeta o Router e o AuthService principal via Injeção de Dependência
  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  // -------------------------------------------------------
  // logout(): faz logout e redireciona para a página de login
  //
  // Chama o logout do AuthService (que limpa localStorage e zera o Signal)
  // e depois navega para /login (não /admin/login, pois o usuário precisa
  // logar novamente como qualquer usuário).
  // -------------------------------------------------------
  logout(): void {
    this.authService.logout();          // Limpa tokens e zera currentUser
    this.router.navigate(['/login']);   // Redireciona para a página de login do site
  }

  // Verifica se o usuário logado tem papel de "admin"
  // Retorna true apenas se papel === 'admin'
  isAuthenticated(): boolean {
    return this.authService.currentUser()?.papel === 'admin';
  }

  // -------------------------------------------------------
  // tryAdminLogin(): mantido para não quebrar chamadas de código legado.
  // Anteriormente usava autenticação hardcoded; hoje sempre retorna false
  // pois a autenticação é feita via JWT pelo AuthService.
  //
  // Os parâmetros têm "_" no início (_email, _senha) para indicar que
  // são intencionalmente não utilizados (convenção TypeScript).
  // -------------------------------------------------------
  tryAdminLogin(_email: string, _senha: string): boolean {
    return false; // Obsoleto: use AuthService.login() + adminGuard
  }

  // -------------------------------------------------------
  // setAdmin(): mantido para compatibilidade. Não precisa mais fazer nada
  // porque currentAdmin é derivado automaticamente do AuthService.
  // -------------------------------------------------------
  setAdmin(_usuario: AdminUser): void {
    // Não precisa mais: currentAdmin deriva do AuthService automaticamente
  }
}
