// ============================================================
// ARQUIVO: admin-dashboard.component.ts
// FUNÇÃO: Componente do dashboard do painel administrativo (/admin/dashboard).
//
// RESPONSABILIDADES:
// - Exibir métricas resumidas (total de pedidos, produtos, usuários, receita)
// - Listar os 5 pedidos mais recentes
// - Listar os 5 produtos mais recentes
// - Calcular a altura das barras do gráfico de vendas
//
// PADRÃO implements OnInit:
// ngOnInit() é o lugar correto para fazer chamadas de API.
// Não se deve fazer isso no construtor, pois o Angular pode
// chamar o construtor antes de o componente estar totalmente montado.
//
// CARREGAMENTO PARALELO:
// As 3 chamadas de API (pedidos, produtos, usuários) são feitas
// de forma independente — não esperam uma pela outra.
// Quando cada uma responde, atualiza apenas seus próprios dados.
//
// GRÁFICO DE VENDAS:
// vendasSemana é uma estrutura estática preparada para receber dados reais.
// Atualmente exibe barras com valor 0 (aguarda integração futura).
// ============================================================

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
// Services para buscar os dados de cada seção
import { PedidoService, PedidoApi } from '../../core/services/pedido.service';
import { ProdutoService } from '../../core/services/produto.service';
import { UsuarioService } from '../../core/services/usuario.service';

// Interface simplificada de pedido para exibição no dashboard
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

  // Dados para exibição no template
  pedidosRecentes:  PedidoResumo[] = [];
  produtosRecentes: { nome: string; status: string; imagem_url?: string }[] = [];

  // Métricas resumidas exibidas nos cards do topo
  totalPedidos     = 0;
  totalProdutos    = 0;
  totalUsuarios    = 0;
  receitaTotal     = 0;
  pedidosPendentes = 0;

  carregando = true; // Controla o spinner de loading inicial

  // Estrutura do gráfico de vendas (valores zerados — aguarda integração futura)
  // readonly: não deve ser alterado (dados estáticos de exemplo)
  readonly vendasSemana = [
    { dia: 'Seg', valor: 0 }, { dia: 'Ter', valor: 0 }, { dia: 'Qua', valor: 0 },
    { dia: 'Qui', valor: 0 }, { dia: 'Sex', valor: 0 }, { dia: 'Sáb', valor: 0 },
    { dia: 'Dom', valor: 0 }
  ];
  readonly maxVenda = 100; // Valor máximo para cálculo de altura das barras

  // Getter que agrupa as métricas em um objeto (para uso no template)
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
    private pedidoService:  PedidoService,
    private produtoService: ProdutoService,
    private usuarioService: UsuarioService
  ) {}

  // ngOnInit(): ponto de entrada do componente — faz as chamadas de API
  ngOnInit() {
    // Carrega pedidos — calcula métricas e monta a lista de recentes
    this.pedidoService.listar().subscribe({
      next: (pedidos) => {
        this.totalPedidos     = pedidos.length;
        // reduce: soma o total de todos os pedidos (acumulador começa em 0)
        this.receitaTotal     = pedidos.reduce((s, p) => s + p.total, 0);
        // filter: conta apenas pedidos com status "pendente"
        this.pedidosPendentes = pedidos.filter(p => p.status === 'pendente').length;
        // slice(0, 5): pega apenas os 5 primeiros; map: converte para PedidoResumo
        this.pedidosRecentes  = pedidos.slice(0, 5).map(p => this.adaptarPedido(p));
        this.carregando = false;
      },
      error: () => { this.carregando = false; }
    });

    // Carrega produtos — conta total e lista os 5 mais recentes
    this.produtoService.listar({ status: 'todos' }).subscribe({
      next: (lista) => {
        this.totalProdutos = lista.length;
        this.produtosRecentes = lista
          .slice() // Copia o array (sort modifica in-place)
          .sort((a, b) => (b.criado_em ?? '').localeCompare(a.criado_em ?? '')) // Mais recente primeiro
          .slice(0, 5)
          .map(p => ({ nome: p.nome, status: p.status, imagem_url: p.imagem_url }));
      },
      error: () => {}
    });

    // Carrega usuários — apenas para contar o total
    this.usuarioService.listarTodos().subscribe({
      next: (lista) => { this.totalUsuarios = lista.length; },
      error: () => {}
    });
  }

  // Converte PedidoApi para PedidoResumo (formato simplificado para o dashboard)
  // ?? '—': usa traço se o campo for null/undefined
  private adaptarPedido(p: PedidoApi): PedidoResumo {
    return {
      id:              p.id,
      usuario_nome:    p.usuario?.nome  ?? '—',
      usuario_email:   p.usuario?.email ?? '—',
      criado_em:       (p.criadoEm ?? '').slice(0, 10), // Pega apenas a data (YYYY-MM-DD)
      total:           p.total,
      forma_pagamento: '—',
      status:          p.status
    };
  }

  // Calcula a altura proporcional de uma barra do gráfico (0 a 100%)
  // Evita divisão por zero com a verificação maxVenda > 0
  getBarHeight(valor: number): number {
    return this.maxVenda > 0 ? Math.round((valor / this.maxVenda) * 100) : 0;
  }

  // Mapeia o status para a classe CSS do badge (mesma lógica em outros componentes)
  getStatusClass(status: string): string {
    const m: Record<string, string> = {
      pendente: 'status-pendente', confirmado: 'status-pago', em_separacao: 'status-pago',
      enviado: 'status-enviado', entregue: 'status-entregue', cancelado: 'status-cancelado'
    };
    return m[status] || '';
  }

  // Mapeia o status para texto legível em português
  getStatusLabel(status: string): string {
    const m: Record<string, string> = {
      pendente: 'Pendente', confirmado: 'Confirmado', em_separacao: 'Em separação',
      enviado: 'Enviado', entregue: 'Entregue', cancelado: 'Cancelado'
    };
    return m[status] || status;
  }

  // Formata valores como moeda brasileira (ex: 1500 → "R$ 1.500,00")
  formatCurrency(value: number): string {
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
