// ============================================================
// ARQUIVO: app.config.server.ts
// FUNÇÃO: Configuração do Angular para renderização no servidor (SSR — Server-Side Rendering).
//
// O QUE É SSR?
// Por padrão, o Angular gera o HTML no navegador (Client-Side Rendering).
// Com SSR, o servidor também consegue renderizar as páginas antes de enviá-las
// ao navegador. Isso melhora:
//   - Performance inicial (a página aparece mais rápido)
//   - SEO (os robôs de busca conseguem ler o conteúdo)
//
// mergeApplicationConfig():
// Esta função MESCLA a configuração base do cliente (appConfig)
// com as configurações específicas do servidor (serverConfig).
// O resultado (config) é a configuração completa para o ambiente SSR.
//
// provideServerRendering():
// Habilita os provedores necessários para o Angular funcionar no Node.js
// (servidor), onde não existe navegador, DOM ou localStorage.
//
// CONEXÕES: importado pelo arquivo server.ts (ponto de entrada do servidor SSR).
// ============================================================

// mergeApplicationConfig: função para combinar duas configurações Angular
// ApplicationConfig: tipo que representa a configuração de uma aplicação Angular
import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';

// provideServerRendering: habilita o modo SSR no servidor (Node.js)
import { provideServerRendering } from '@angular/platform-server';

// Importa a configuração base do cliente (providers padrão: router, http, etc.)
import { appConfig } from './app.config';

// Configuração exclusiva do servidor: adiciona apenas o provider de SSR
const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(), // Habilita renderização no servidor (Node.js/Express)
  ]
};

// Mescla a configuração do cliente com a do servidor.
// Resultado: tudo que funciona no cliente também funciona no servidor + SSR.
export const config = mergeApplicationConfig(appConfig, serverConfig);
