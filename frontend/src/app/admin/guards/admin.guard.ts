// ============================================================
// ARQUIVO: admin.guard.ts
// FUNÇÃO: Guarda de rota que protege todas as páginas do painel administrativo.
//
// DIFERENÇA DO authGuard:
// - authGuard (core): verifica apenas se o usuário está logado (qualquer papel)
// - adminGuard (admin): verifica se o usuário está logado E tem papel "admin"
//
// TRÊS CASOS POSSÍVEIS:
// 1. Usuário logado como admin → acessa normalmente (return true)
// 2. Usuário logado mas não é admin → redireciona para / (página inicial)
// 3. Usuário não logado → redireciona para /admin/login
//
// ONDE É USADO:
// No arquivo admin.routes.ts, aplicado a todas as rotas do painel admin.
// ============================================================

// inject: obtém serviços em funções (sem construtor)
import { inject } from '@angular/core';
// CanActivateFn: tipo da função guard | Router: serviço de navegação
import { CanActivateFn, Router } from '@angular/router';
// AuthService: contém o usuário logado como Signal
import { AuthService } from '../../core/services/auth.service';

// adminGuard: função guard que controla o acesso ao painel administrativo
export const adminGuard: CanActivateFn = () => {
  const auth   = inject(AuthService); // Serviço de autenticação
  const router = inject(Router);      // Serviço de navegação

  // Lê o usuário logado atualmente (null se não estiver logado)
  // auth.currentUser(): chama o Signal para obter o valor atual
  const usuario = auth.currentUser();

  // CASO 1: Usuário logado com papel "admin" → permite o acesso
  if (usuario?.papel === 'admin') {
    return true;
  }

  // CASO 2: Usuário logado mas sem permissão de admin
  // Redireciona para a página inicial (não para o login, pois já está logado)
  if (usuario) {
    return router.createUrlTree(['/']); // → /  (home)
  }

  // CASO 3: Usuário não está logado
  // Redireciona para o login específico do admin
  return router.createUrlTree(['/admin/login']); // → /admin/login
};
