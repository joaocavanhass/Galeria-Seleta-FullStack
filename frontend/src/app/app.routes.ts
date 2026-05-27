// ============================================================
// ARQUIVO: app.routes.ts
// FUNÇÃO: Define todas as rotas (URLs) da aplicação Angular.
//
// O QUE SÃO ROTAS?
// Rotas conectam URLs a componentes. Quando o usuário navega para
// /produtos, o Angular carrega o ProdutosComponent. Quando vai para
// /checkout, carrega o CheckoutComponent. E assim por diante.
//
// CONCEITOS IMPORTANTES:
//
// canActivate: [authGuard]
//   Guard de rota — executa uma verificação antes de carregar o componente.
//   Se o usuário não estiver logado, redireciona para /login.
//
// loadChildren (Lazy Loading):
//   O módulo admin é carregado "preguiçosamente" — só quando o usuário
//   tenta acessar /admin. Isso reduz o tamanho do bundle inicial.
//   import('./admin/admin.routes') → carrega o arquivo de rotas admin apenas quando necessário.
//
// path: '**'  (wildcard)
//   Captura qualquer URL não reconhecida. Redireciona para '' (página inicial).
//   Deve sempre ser a ÚLTIMA rota da lista.
//
// ESTRUTURA:
// Rotas públicas (acessíveis sem login):
//   '', 'cadastro', 'login', 'esqueci-senha', 'sobre', 'produtos', 'produtos/:id',
//   'carrinho', 'checkout'
//
// Rotas protegidas (exigem login — canActivate: [authGuard]):
//   'meus-pedidos'
//
// Rotas admin (carregadas sob demanda):
//   'admin' → delega para admin.routes.ts
// ============================================================

// Routes: tipo que representa um array de definições de rota
import { Routes } from '@angular/router';

// Importa cada componente que será renderizado em uma rota específica
import { HomeComponent }           from './home/home.component';
import { CadastroComponent }       from './cadastro/cadastro.component';
import { LoginComponent }          from './login/login.component';
import { EsqueciSenhaComponent }   from './esqueci-senha/esqueci-senha.component';
import { SobreComponent }          from './sobre/sobre.component';
import { ProdutosComponent }       from './produtos/produtos.component';
import { ProdutoDetalhesComponent } from './produto-detalhes/produto-detalhes.component';
import { CheckoutComponent }       from './checkout/checkout.component';
import { CarrinhoComponent }       from './carrinho/carrinho.component';
import { MeusPedidosComponent }    from './meus-pedidos/meus-pedidos.component';

// Guard de autenticação — verifica se o usuário está logado antes de acessar a rota
import { authGuard } from './core/guards/auth.guard';

// routes: array de rotas da aplicação (exportado para uso no app.config.ts)
export const routes: Routes = [
  // Rotas públicas — qualquer usuário pode acessar
  { path: '',              component: HomeComponent            }, // Página inicial (/  ou /home)
  { path: 'cadastro',      component: CadastroComponent        }, // Formulário de registro
  { path: 'login',         component: LoginComponent           }, // Formulário de login
  { path: 'esqueci-senha', component: EsqueciSenhaComponent    }, // Recuperação de senha
  { path: 'sobre',         component: SobreComponent           }, // Página "Sobre nós"
  { path: 'produtos',      component: ProdutosComponent        }, // Listagem de produtos
  { path: 'produtos/:id',  component: ProdutoDetalhesComponent }, // Detalhes de um produto (:id = parâmetro dinâmico)
  { path: 'carrinho',      component: CarrinhoComponent        }, // Carrinho de compras
  { path: 'checkout',      component: CheckoutComponent        }, // Finalização da compra

  // Rota protegida — exige que o usuário esteja logado
  // canActivate: [authGuard] — o guard redireciona para /login se não autenticado
  { path: 'meus-pedidos',  component: MeusPedidosComponent,   canActivate: [authGuard] },

  // Rota do painel administrativo — carregamento sob demanda (Lazy Loading)
  // loadChildren: carrega o módulo de rotas admin apenas quando /admin for acessado
  // import('./admin/admin.routes').then(m => m.adminRoutes): importação dinâmica
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.routes').then(m => m.adminRoutes)
  },

  // Rota curinga — captura qualquer URL não mapeada acima
  // Redireciona para a página inicial (path: '')
  // DEVE ser sempre a última rota da lista
  { path: '**', redirectTo: '' }
];
