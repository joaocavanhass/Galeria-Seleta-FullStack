import { Routes } from '@angular/router';
import { adminGuard } from './guards/admin.guard';
import { AdminLayoutComponent } from './layout/admin-layout.component';
import { AdminDashboardComponent } from './dashboard/admin-dashboard.component';
import { AdminUsuariosComponent } from './usuarios/admin-usuarios.component';
import { AdminProdutosComponent } from './produtos/admin-produtos.component';
import { AdminPedidosComponent } from './pedidos/admin-pedidos.component';

export const adminRoutes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [adminGuard],
    children: [
      { path: '',          redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: AdminDashboardComponent         },
      { path: 'usuarios',  component: AdminUsuariosComponent          },
      { path: 'produtos',  component: AdminProdutosComponent          },
      { path: 'pedidos',   component: AdminPedidosComponent           },
    ]
  }
];