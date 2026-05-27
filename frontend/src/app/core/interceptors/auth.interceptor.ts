// ============================================================
// ARQUIVO: auth.interceptor.ts
// FUNÇÃO: Interceptador HTTP que adiciona o JWT em todas as requisições.
//
// O QUE É UM INTERCEPTADOR (Interceptor)?
// Um interceptador é uma função que "intercepta" (intercepta mesmo)
// toda requisição HTTP que o Angular faz com o HttpClient.
// Ele pode modificar a requisição antes de enviá-la ao servidor.
//
// POR QUE USAR UM INTERCEPTADOR?
// Sem ele, teríamos que adicionar manualmente o token JWT em CADA
// chamada de API em CADA serviço. Com o interceptador, isso acontece
// automaticamente para todas as requisições de uma vez só.
//
// COMO FUNCIONA:
// 1. Angular faz uma requisição HTTP (ex: GET /api/produtos)
// 2. O interceptador é chamado antes de a requisição sair
// 3. Se há um token no localStorage, ele é adicionado no cabeçalho
// 4. A requisição modificada é enviada ao servidor com Authorization: Bearer <token>
//
// VERIFICAÇÃO DE PLATAFORMA (isPlatformBrowser):
// O Angular pode executar no servidor (SSR - Server Side Rendering).
// No servidor, não existe localStorage. Esta verificação garante
// que o código do localStorage só roda no navegador.
//
// COMO É REGISTRADO:
// No app.config.ts: withInterceptors([authInterceptor])
// ============================================================

// HttpInterceptorFn: tipo da função interceptadora
import { HttpInterceptorFn } from '@angular/common/http';
// inject: obtém serviços | PLATFORM_ID: identifica se estamos no browser ou servidor
import { inject, PLATFORM_ID } from '@angular/core';
// isPlatformBrowser: retorna true se estiver rodando no navegador (não no servidor)
import { isPlatformBrowser } from '@angular/common';

// authInterceptor: função do tipo HttpInterceptorFn
// Parâmetros: req = a requisição original | next = função para continuar a cadeia
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Injeta o identificador de plataforma para checar se está no browser
  const platformId = inject(PLATFORM_ID);

  // Se não estiver no browser (ex: SSR no servidor), envia a requisição sem modificação
  if (!isPlatformBrowser(platformId)) {
    return next(req); // next(req): passa a requisição para o próximo interceptador (ou para o servidor)
  }

  // Lê o token de acesso JWT do localStorage do navegador
  // 'gs_access_token': chave onde o AuthService salva o token após o login
  const token = localStorage.getItem('gs_access_token');

  if (token) {
    // req.clone(): cria uma CÓPIA da requisição com modificações
    // Requisições HTTP são imutáveis (não podem ser editadas), então clonamos
    req = req.clone({
      // setHeaders: adiciona (ou substitui) cabeçalhos na requisição
      // Authorization: "Bearer <token>" — formato padrão para JWT em APIs REST
      // O servidor lê este cabeçalho no JwtAuthenticationFilter
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  // next(req): envia a requisição (modificada ou não) para o servidor
  return next(req);
};
