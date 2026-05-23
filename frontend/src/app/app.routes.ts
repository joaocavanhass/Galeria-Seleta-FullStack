import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { CadastroComponent } from './cadastro/cadastro.component';
import { LoginComponent } from './login/login.component';
import { EsqueciSenhaComponent } from './esqueci-senha/esqueci-senha.component';
import { SobreComponent } from './sobre/sobre.component';
import { ProdutosComponent } from './produtos/produtos.component';
import { ProdutoDetalhesComponent } from './produto-detalhes/produto-detalhes.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { CarrinhoComponent } from './carrinho/carrinho.component';

export const routes: Routes = [
  { path: '',              component: HomeComponent            },
  { path: 'cadastro',      component: CadastroComponent        },
  { path: 'login',         component: LoginComponent           },
  { path: 'esqueci-senha', component: EsqueciSenhaComponent    },
  { path: 'sobre',         component: SobreComponent           },
  { path: 'produtos',      component: ProdutosComponent        },
  { path: 'produtos/:id',  component: ProdutoDetalhesComponent },
  { path: 'checkout',      component: CheckoutComponent        },
  { path: 'carrinho',      component: CarrinhoComponent        },
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.routes').then(m => m.adminRoutes)
  },
  { path: '**', redirectTo: '' }
];