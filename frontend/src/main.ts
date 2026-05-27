// ============================================================
// ARQUIVO: main.ts
// FUNÇÃO: Ponto de entrada principal da aplicação Angular (Client-Side).
//
// O QUE É O PONTO DE ENTRADA?
// É o primeiro arquivo TypeScript que é executado quando o Angular carrega
// no navegador. Ele "inicializa" (bootstrap) a aplicação Angular.
//
// bootstrapApplication():
// Função do Angular 17+ que inicia uma aplicação standalone (sem NgModule).
// Recebe dois argumentos:
//   1. AppComponent: o componente raiz da aplicação (o componente pai de tudo)
//   2. appConfig:    a configuração global (providers, router, interceptors, etc.)
//
// .catch((err) => console.error(err)):
// Se der qualquer erro ao iniciar a aplicação (ex: configuração inválida,
// módulo não encontrado), o erro é capturado e exibido no console do navegador.
//
// CONEXÕES: importado pelo builder do Angular (vite/webpack) como entry point.
// O index.html referencia o bundle gerado a partir deste arquivo.
// ============================================================

// bootstrapApplication: função que inicializa a aplicação Angular standalone
import { bootstrapApplication } from '@angular/platform-browser';

// appConfig: configuração global (router, http, interceptors, etc.)
import { appConfig } from './app/app.config';

// AppComponent: o componente raiz — o "<app-root>" no index.html
import { AppComponent } from './app/app.component';

// Inicia a aplicação Angular com o componente raiz e a configuração global
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err)); // Captura erros de inicialização no console
