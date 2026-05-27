// ============================================================
// ARQUIVO: app.config.ts
// FUNÇÃO: Configura os serviços principais da aplicação Angular.
//
// O QUE É O ApplicationConfig?
// É o arquivo de configuração raiz do Angular 17+ (Standalone mode).
// Substitui o antigo AppModule do Angular tradicional.
// Aqui registramos os "providers" — serviços e funcionalidades globais.
//
// PROVIDERS REGISTRADOS:
//
// provideZoneChangeDetection({ eventCoalescing: true }):
//   Configura a estratégia de detecção de mudanças.
//   eventCoalescing: agrupa múltiplos eventos em uma única detecção,
//   melhorando a performance.
//
// provideRouter(routes):
//   Registra o sistema de rotas definido em app.routes.ts.
//   Sem isso, o Angular não sabe navegar entre páginas.
//
// provideClientHydration(withEventReplay()):
//   Habilitado para SSR (Server Side Rendering com Angular Universal).
//   Reidrata o HTML gerado no servidor no cliente.
//   withEventReplay(): reproduce eventos que ocorreram antes da hidratação.
//
// provideHttpClient(withFetch(), withInterceptors([authInterceptor])):
//   Registra o serviço de requisições HTTP (HttpClient) globalmente.
//   withFetch(): usa a API Fetch do browser (mais moderna que XMLHttpRequest).
//   withInterceptors([authInterceptor]): registra o interceptador que
//     adiciona o token JWT em todas as requisições automaticamente.
// ============================================================

// ApplicationConfig: tipo da configuração da aplicação
// provideZoneChangeDetection: configura a detecção de mudanças
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
// provideRouter: registra as rotas da aplicação
import { provideRouter } from '@angular/router';
// provideHttpClient: habilita o HttpClient | withInterceptors: registra interceptadores
// withFetch: usa a API Fetch moderna em vez do XMLHttpRequest legado
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
// provideClientHydration: suporte a SSR | withEventReplay: reproduz eventos pós-hidratação
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

// Importa as rotas definidas em app.routes.ts
import { routes } from './app.routes';
// Importa o interceptador que injeta o JWT em todas as requisições HTTP
import { authInterceptor } from './core/interceptors/auth.interceptor';

// appConfig: objeto de configuração exportado — usado pelo main.ts para bootstrapar o app
export const appConfig: ApplicationConfig = {
  providers: [
    // Configura a detecção de mudanças com coalescimento de eventos (melhor performance)
    provideZoneChangeDetection({ eventCoalescing: true }),

    // Registra o roteador com as rotas definidas no app.routes.ts
    provideRouter(routes),

    // Suporte a SSR: reidrata o HTML do servidor no browser e reproduz eventos capturados
    provideClientHydration(withEventReplay()),

    // Habilita o HttpClient com:
    //   withFetch(): usa a API Fetch moderna do browser
    //   withInterceptors([authInterceptor]): injeta o JWT em todas as requisições
    provideHttpClient(withFetch(), withInterceptors([authInterceptor]))
  ]
};
