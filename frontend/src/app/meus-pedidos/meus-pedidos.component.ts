// ============================================================
// ARQUIVO: meus-pedidos.component.ts
// FUNÇÃO: Componente da página de pedidos do usuário (/meus-pedidos).
//
// PROTEÇÃO DE ROTA:
// Esta rota tem canActivate: [authGuard] no app.routes.ts.
// Se o usuário não estiver logado, o authGuard redireciona para /login
// antes mesmo de este componente ser criado.
//
// TEMPLATE INLINE:
// Este componente usa template e styles diretamente no TypeScript
// (em vez de arquivos .html e .css separados). Isso é adequado para
// componentes simples onde a proximidade do código facilita a leitura.
//
// SIGNALS:
// pedidos: lista de pedidos do usuário
// carregando: controla o indicador de loading
//
// UTILITÁRIOS:
// getStatusClass(): mapeamento status → classe CSS para cores
// getStatusLabel(): mapeamento status → texto em português
// formatCurrency(): formata valores monetários em Real (R$)
// ============================================================

import { Component, OnInit, signal } from '@angular/core';
// CommonModule: *ngIf, *ngFor, ngClass
import { CommonModule } from '@angular/common';
// RouterLink: link para /produtos (quando carrinho vazio)
import { RouterLink } from '@angular/router';
import { PedidoService, PedidoApi } from '../core/services/pedido.service';

@Component({
  selector: 'app-meus-pedidos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  // template inline: HTML e styles definidos diretamente no componente
  template: `
    <div class="pedidos-page">
      <div class="pedidos-header">
        <h1>Meus Pedidos</h1>
        <p>Acompanhe o status dos seus pedidos</p>
      </div>

      <!-- Indicador de loading enquanto a API responde -->
      <div *ngIf="carregando()" class="loading">Carregando pedidos…</div>

      <!-- Estado vazio: nenhum pedido ainda -->
      <div *ngIf="!carregando() && pedidos().length === 0" class="empty">
        <p>Você ainda não fez nenhum pedido.</p>
        <a routerLink="/produtos" class="btn-primary">Ver produtos</a>
      </div>

      <!-- Lista de pedidos -->
      <div class="pedidos-lista" *ngIf="!carregando() && pedidos().length > 0">
        <div class="pedido-card" *ngFor="let p of pedidos()">
          <div class="pedido-top">
            <div>
              <span class="pedido-id">#{{ p.id }}</span>
              <span class="pedido-data">{{ (p.criadoEm || '').slice(0,10) }}</span>
            </div>
            <!-- [ngClass]: aplica dinamicamente a classe CSS de status -->
            <span class="status-badge" [ngClass]="getStatusClass(p.status)">
              {{ getStatusLabel(p.status) }}
            </span>
          </div>

          <div class="pedido-itens">
            <div class="item-row" *ngFor="let item of p.itens">
              <!-- "?? 'Produto'": exibe fallback se o produto foi deletado -->
              <span>{{ item.produto?.nome ?? 'Produto' }}</span>
              <span>{{ item.quantidade }}x {{ formatCurrency(item.precoPago) }}</span>
            </div>
          </div>

          <div class="pedido-bottom">
            <span class="pedido-total">Total: <strong>{{ formatCurrency(p.total) }}</strong></span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .pedidos-page {
      max-width: 800px;
      margin: 0 auto;
      padding: 40px 24px;
      font-family: 'Lato', sans-serif;
    }
    .pedidos-header { margin-bottom: 32px; }
    .pedidos-header h1 { font-size: 28px; font-weight: 700; color: #1a1a1a; margin-bottom: 6px; }
    .pedidos-header p  { color: #666; font-size: 14px; }

    .loading, .empty { text-align: center; padding: 60px 0; color: #888; }
    .btn-primary {
      display: inline-block; margin-top: 16px;
      background: #e8441a; color: #fff;
      padding: 12px 24px; border-radius: 8px;
      text-decoration: none; font-weight: 600;
    }

    .pedidos-lista { display: flex; flex-direction: column; gap: 16px; }

    .pedido-card {
      background: #fff;
      border: 1px solid #eee;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    }
    .pedido-top {
      display: flex; justify-content: space-between; align-items: center;
      margin-bottom: 16px;
    }
    .pedido-id   { font-weight: 700; color: #1a1a1a; margin-right: 12px; }
    .pedido-data { font-size: 13px; color: #888; }

    .pedido-itens { border-top: 1px solid #f0f0f0; padding-top: 12px; margin-bottom: 12px; }
    .item-row {
      display: flex; justify-content: space-between;
      font-size: 14px; color: #444; padding: 4px 0;
    }

    .pedido-bottom { border-top: 1px solid #f0f0f0; padding-top: 12px; text-align: right; }
    .pedido-total  { font-size: 15px; color: #1a1a1a; }

    .status-badge {
      padding: 4px 12px; border-radius: 20px;
      font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em;
    }
    .status-pendente  { background: #fff3e0; color: #e65100; }
    .status-pago      { background: #e3f2fd; color: #1565c0; }
    .status-enviado   { background: #ede7f6; color: #4527a0; }
    .status-entregue  { background: #e8f5e9; color: #2e7d32; }
    .status-cancelado { background: #ffebee; color: #c62828; }
  `]
})
export class MeusPedidosComponent implements OnInit {

  // Signals para estado reativo
  pedidos    = signal<PedidoApi[]>([]);
  carregando = signal(true);

  constructor(private pedidoService: PedidoService) {}

  ngOnInit(): void {
    // Carrega os pedidos do usuário logado (o backend filtra pelo JWT)
    this.pedidoService.listar().subscribe({
      next: (lista) => { this.pedidos.set(lista); this.carregando.set(false); },
      error: ()      => { this.carregando.set(false); }
    });
  }

  // Mapeia o status para a classe CSS correspondente (define a cor do badge)
  // Record<string, string>: objeto com chaves e valores do tipo string
  getStatusClass(s: string): string {
    const m: Record<string, string> = {
      pendente: 'status-pendente', confirmado: 'status-pago',
      em_separacao: 'status-pago', enviado: 'status-enviado',
      entregue: 'status-entregue', cancelado: 'status-cancelado'
    };
    return m[s] || ''; // Retorna string vazia se status não mapeado
  }

  // Mapeia o status para texto em português legível
  getStatusLabel(s: string): string {
    const m: Record<string, string> = {
      pendente: 'Pendente', confirmado: 'Confirmado', em_separacao: 'Em separação',
      enviado: 'Enviado', entregue: 'Entregue', cancelado: 'Cancelado'
    };
    return m[s] || s; // Retorna o próprio status se não mapeado
  }

  // Formata número como moeda brasileira (ex: 150.5 → "R$ 150,50")
  formatCurrency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
