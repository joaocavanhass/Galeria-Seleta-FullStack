// ============================================================
// ARQUIVO: main.server.ts
// FUNÇÃO: Ponto de entrada da aplicação Angular para SSR (Server-Side Rendering).
//
// DIFERENÇA ENTRE main.ts E main.server.ts:
// - main.ts:        inicializa o Angular no NAVEGADOR (client-side)
// - main.server.ts: inicializa o Angular no SERVIDOR Node.js (server-side)
//
// POR QUE SSR?
// Com SSR, o servidor renderiza o HTML completo antes de enviar para o navegador.
// Isso melhora:
//   - Velocidade de carregamento inicial (First Contentful Paint)
//   - SEO: robôs do Google conseguem ler o conteúdo da página
//
// BootstrapContext:
// Contexto fornecido pelo servidor SSR — inclui a URL da requisição
// para que o Angular renderize a página correta no servidor.
//
// bootstrap():
// Função exportada como default — é chamada pelo servidor (Express/Node.js)
// a cada requisição HTTP para renderizar a página correspondente.
//
// CONEXÕES: usado pelo server.ts (servidor Express) para integração SSR.
// ============================================================

// BootstrapContext: contexto da requisição HTTP atual (URL, headers, etc.)
// bootstrapApplication: inicializa a aplicação Angular (mesma função do main.ts)
import { BootstrapContext, bootstrapApplication } from '@angular/platform-browser';

// AppComponent: componente raiz da aplicação
import { AppComponent } from './app/app.component';

// config: configuração SSR (appConfig do cliente + provideServerRendering())
import { config } from './app/app.config.server';

// Função exportada como default — chamada pelo servidor a cada requisição
// context: contém informações da requisição (URL, parâmetros) para renderização
const bootstrap = (context: BootstrapContext) =>
    bootstrapApplication(AppComponent, config, context);

export default bootstrap;
