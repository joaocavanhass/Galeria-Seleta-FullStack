// ============================================================
// ARQUIVO: admin.routes.ts
// FUNÇÃO: Define as rotas do painel administrativo (/admin/...).
//
// ESTRUTURA DE ROTAS ANINHADAS (children):
// Todas as páginas do admin (exceto /admin/login) ficam dentro
// do AdminLayoutComponent, que fornece o layout compartilhado
// (sidebar, header, etc.). As rotas filhas ("children") são
// renderizadas dentro do <router-outlet> do AdminLayoutComponent.
//
// ROTAS:
// - /admin/login         → Tela de login do admin (pública)
// - /admin               → Redireciona para /admin/dashboard
// - /admin/dashboard     → Dashboard com métricas gerais
// - /admin/usuarios      → Gerenciamento de usuários
// - /admin/produtos      → Gerenciamento de produtos
// - /admin/pedidos       → Gerenciamento de pedidos
//
// canActivate: [adminGuard]:
// Aplicado ao pai (AdminLayoutComponent). Protege TODAS as rotas
// filhas de uma só vez — se o guard bloquear, nenhuma rota filha
// é acessível.
//
// LAZY LOADING:
// Este arquivo é carregado de forma assíncrona a partir de app.routes.ts.
// Quando o usuário acessa /admin, o Angular baixa este arquivo separadamente.
// ============================================================

// Routes: tipo do array de definições de rota
import { Routes } from '@angular/router';
// Guard que verifica se o usuário está logado como admin
import { adminGuard } from './guards/admin.guard';
// Componentes de cada seção do painel
import { AdminLayoutComponent }    from './layout/admin-layout.component';
import { AdminDashboardComponent } from './dashboard/admin-dashboard.component';
import { AdminUsuariosComponent }  from './usuarios/admin-usuarios.component';
import { AdminProdutosComponent }  from './produtos/admin-produtos.component';
import { AdminPedidosComponent }   from './pedidos/admin-pedidos.component';
import { AdminLoginComponent }     from './login/admin-login.component';

// adminRoutes: exportado e importado dinamicamente pelo app.routes.ts (lazy loading)
export const adminRoutes: Routes = [

  // /admin/login → página de login do admin (pública, sem guard)
  { path: 'login', component: AdminLoginComponent },

  // /admin → todas as rotas protegidas dentro do layout do admin
  // canActivate: [adminGuard]: executa o adminGuard antes de qualquer rota filha
  {
    path: '',
    component: AdminLayoutComponent, // Shell: fornece o layout (sidebar + header)
    canActivate: [adminGuard],        // Protege: redireciona se não for admin
    children: [
      // /admin (sem sub-rota) → redireciona para /admin/dashboard
      // pathMatch: 'full': só redireciona se o caminho for EXATAMENTE '' (não apenas começa com '')
      { path: '',          redirectTo: 'dashboard', pathMatch: 'full' },

      // Rotas filhas — renderizadas dentro do <router-outlet> do AdminLayoutComponent
      { path: 'dashboard', component: AdminDashboardComponent }, // /admin/dashboard
      { path: 'usuarios',  component: AdminUsuariosComponent  }, // /admin/usuarios
      { path: 'produtos',  component: AdminProdutosComponent  }, // /admin/produtos
      { path: 'pedidos',   component: AdminPedidosComponent   }, // /admin/pedidos
    ]
  }
];
