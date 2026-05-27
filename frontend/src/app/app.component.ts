// ============================================================
// ARQUIVO: app.component.ts
// FUNÇÃO: Componente raiz da aplicação Angular.
//
// O QUE É O COMPONENTE RAIZ?
// É o primeiro componente carregado quando a aplicação inicia.
// Ele serve como o "esqueleto" principal da página:
// exibe o Header no topo, o Footer no rodapé, e no meio
// o <router-outlet> que troca o conteúdo conforme a rota.
//
// CONCEITO — Standalone Component (Angular 17+):
// standalone: true significa que este componente não precisa
// ser declarado em um NgModule. Ele importa diretamente os
// outros componentes que usa (RouterOutlet, Footer, Header).
//
// FLUXO DE RENDERIZAÇÃO:
// main.ts → bootstrapApplication(AppComponent) → app.component.html
//   → app.component.html contém: <app-header>, <router-outlet>, <app-footer>
//   → O conteúdo do <router-outlet> muda conforme o URL atual
//
// CONEXÕES: app.component.html define o layout geral da página.
// ============================================================

// Component: decorador que transforma a classe em um componente Angular
import { Component } from '@angular/core';
// RouterOutlet: diretiva que renderiza o componente da rota atual
import { RouterOutlet } from '@angular/router';
// Componentes de layout importados diretamente (standalone)
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';

// @Component: decorador que configura o componente
// selector: 'app-root' → é referenciado no index.html como <app-root>
// standalone: true → não precisa de NgModule
// imports: lista de componentes/módulos usados no template HTML
// templateUrl: arquivo HTML deste componente
// styleUrl: arquivo CSS deste componente
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FooterComponent, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  // Título da aplicação — pode ser usado no template via {{ title }}
  title = 'galeria-seleta';
}
