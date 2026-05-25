import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PedidoService, PedidoApi } from '../../core/services/pedido.service';
import { ProdutoService } from '../../core/services/produto.service';
import { UsuarioService } from '../../core/services/usuario.service';

interface PedidoResumo {
  id: number;
  usuario_nome: string;
  usuario_email: string;
  criado_em: string;
  total: number;
  forma_pagamento: string;
  status: string;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  pedidosRecentes:  PedidoResumo[] = [];
  produtosRecentes: { nome: string; status: string; imagem_url?: string }[] = [];
  totalPedidos     = 0;
  totalProdutos    = 0;
  totalUsuarios    = 0;
  receitaTotal     = 0;
  pedidosPendentes = 0;
  carregando       = true;

  // Gráfico — mantém estrutura do template; pode ser alimentado por API futuramente
  readonly vendasSemana = [
    { dia: 'Seg', valor: 0 }, { dia: 'Ter', valor: 0 }, { dia: 'Qua', valor: 0 },
    { dia: 'Qui', valor: 0 }, { dia: 'Sex', valor: 0 }, { dia: 'Sáb', valor: 0 },
    { dia: 'Dom', valor: 0 }
  ];
  readonly maxVenda = 100;

  get stats() {
    return {
      totalUsuarios:   this.totalUsuarios,
      totalPedidos:    this.totalPedidos,
      produtosEstoque: this.totalProdutos,
      receitaTotal:    this.receitaTotal,
      vendasSemana:    this.vendasSemana
    };
  }

  constructor(
    private pedidoService: PedidoService,
    private produtoService: ProdutoService,
    private usuarioService: UsuarioService
  ) {}

  ngOnInit() {
    this.pedidoService.listar().subscribe({
      next: (pedidos) => {
        this.totalPedidos     = pedidos.length;
        this.receitaTotal     = pedidos.reduce((s, p) => s + p.total, 0);
        this.pedidosPendentes = pedidos.filter(p => p.status === 'pendente').length;
        this.pedidosRecentes  = pedidos.slice(0, 5).map(p => this.adaptarPedido(p));
        this.carregando = false;
      },
      error: () => { this.carregando = false; }
    });

    this.produtoService.listar({ status: 'todos' }).subscribe({
      next: (lista) => {
        this.totalProdutos = lista.length;
        this.produtosRecentes = lista
          .slice()
          .sort((a, b) => (b.criado_em ?? '').localeCompare(a.criado_em ?? ''))
          .slice(0, 5)
          .map(p => ({ nome: p.nome, status: p.status, imagem_url: p.imagem_url }));
      },
      error: () => {}
    });

    this.usuarioService.listarTodos().subscribe({
      next: (lista) => { this.totalUsuarios = lista.length; },
      error: () => {}
    });
  }

  private adaptarPedido(p: PedidoApi): PedidoResumo {
    return {
      id:              p.id,
      usuario_nome:    p.usuario?.nome ?? '—',
      usuario_email:   p.usuario?.email ?? '—',
      criado_em:       (p.criadoEm ?? '').slice(0, 10),
      total:           p.total,
      forma_pagamento: '—',
      status:          p.status
    };
  }

  getBarHeight(valor: number): number {
    return this.maxVenda > 0 ? Math.round((valor / this.maxVenda) * 100) : 0;
  }

  getStatusClass(status: string): string {
    const m: Record<string, string> = {
      pendente: 'status-pendente', confirmado: 'status-pago', em_separacao: 'status-pago',
      enviado: 'status-enviado', entregue: 'status-entregue', cancelado: 'status-cancelado'
    };
    return m[status] || '';
  }

  getStatusLabel(status: string): string {
    const m: Record<string, string> = {
      pendente: 'Pendente', confirmado: 'Confirmado', em_separacao: 'Em separação',
      enviado: 'Enviado', entregue: 'Entregue', cancelado: 'Cancelado'
    };
    return m[status] || status;
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
