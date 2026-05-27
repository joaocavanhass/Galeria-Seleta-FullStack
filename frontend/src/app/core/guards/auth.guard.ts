// ============================================================
// ARQUIVO: auth.guard.ts
// FUNÇÃO: Guarda de rota que protege páginas que exigem login.
//
// O QUE É UM GUARD (Guarda de rota)?
// Um guard é uma função que o Angular executa ANTES de carregar
// uma rota. Se retornar true, a navegação prossegue normalmente.
// Se retornar false ou uma URL, o Angular redireciona para lá.
//
// COMO FUNCIONA:
// Quando o usuário tenta acessar /meus-pedidos (ou outra rota protegida),
// o Angular executa authGuard. Se o usuário não estiver logado,
// ele é redirecionado automaticamente para /login.
//
// COMO É REGISTRADO:
// No app.routes.ts, a rota protegida tem: canActivate: [authGuard]
//
// CONCEITO: CanActivateFn
// CanActivateFn é um tipo do Angular que representa uma função guard.
// Ela recebe informações sobre a rota e retorna true/false/UrlTree.
// ============================================================

// inject(): função para obter serviços dentro de funções (sem usar construtor)
import { inject } from '@angular/core';
// CanActivateFn: tipo da função guard | Router: serviço de navegação
import { CanActivateFn, Router } from '@angular/router';
// AuthService: verifica se o usuário está autenticado
import { AuthService } from '../services/auth.service';

// authGuard: função do tipo CanActivateFn — é o guard em si
// Usa a sintaxe de "arrow function" (=>): parecida com function, mas mais curta
export const authGuard: CanActivateFn = () => {
  // inject(): obtém uma instância do serviço sem precisar de construtor
  const auth   = inject(AuthService); // Serviço de autenticação
  const router = inject(Router);      // Serviço de navegação de rotas

  // isAutenticado(): verifica se há um usuário logado (currentUser() !== null)
  if (auth.isAutenticado()) {
    return true; // Usuário logado: permite acessar a rota
  }

  // Usuário NÃO logado: cria uma URL para /login e redireciona
  // createUrlTree(['/login']): cria um objeto de navegação para a rota /login
  // Retornar uma UrlTree (em vez de false) redireciona automaticamente
  return router.createUrlTree(['/login']);
};
