// ============================================================
// ARQUIVO: admin-pedidos.component.ts
// FUNÇÃO: Componente de gerenciamento de pedidos no painel admin (/admin/pedidos).
//
// RESPONSABILIDADES:
// - Listar todos os pedidos da plataforma
// - Filtrar por status com um computed Signal
// - Visualizar detalhes de um pedido em modal
// - Atualizar o status de um pedido (avançar no fluxo)
//
// FLUXO DE STATUS:
// pendente → confirmado → em_separacao → enviado → entregue
//                                                 → cancelado
//
// ATUALIZAÇÃO OTIMISTA:
// atualizarStatus() atualiza o modal de detalhe imediatamente
// (.update()) enquanto a requisição à API ainda está em trânsito.
// Quando a API responde, a lista completa é recarregada.
// Isso dá feedback visual instantâneo ao admin.
//
// computed Signal — filtrados:
// Recalculado quando filtroStatus() ou pedidos() mudam.
// ============================================================

import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PedidoService, PedidoApi } from '../../core/services/pedido.service';

@Component({
  selector: 'app-admin-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-pedidos.component.html',
  styleUrls: ['./admin-pedidos.component.css']
})
export class AdminPedidosComponent implements OnInit {

  // Signals de estado
  pedidos       = signal<PedidoApi[]>([]);           // Lista completa de pedidos
  filtroStatus  = signal('todos');                   // Status filtrado ('todos' = sem filtro)
  pedidoDetalhe = signal<PedidoApi | null>(null);    // Pedido aberto no modal de detalhes
  carregando    = signal(true);

  // Todos os status possíveis para o dropdown de filtro
  statusOptions = ['pendente', 'confirmado', 'em_separacao', 'enviado', 'entregue', 'cancelado'];

  // Computed: filtra a lista baseado no status selecionado
  filtrados = computed(() => {
    if (this.filtroStatus() === 'todos') return this.pedidos(); // Sem filtro
    return this.pedidos().filter(p => p.status === this.filtroStatus());
  });

  constructor(private pedidoService: PedidoService) {}

  ngOnInit(): void {
    this.carregarPedidos();
  }

  carregarPedidos(): void {
    this.carregando.set(true);
    this.pedidoService.listar().subscribe({
      next: (lista) => { this.pedidos.set(lista); this.carregando.set(false); },
      error: ()      => { this.carregando.set(false); }
    });
  }

  // Abre o modal com os detalhes do pedido selecionado
  verDetalhe(p: PedidoApi) { this.pedidoDetalhe.set(p); }
  fecharDetalhe()           { this.pedidoDetalhe.set(null); }

  // -------------------------------------------------------
  // atualizarStatus(): muda o status de um pedido
  //
  // OTIMISMO: atualiza o modal imediatamente antes da API responder.
  // .update(d => ...): recebe o valor atual e retorna o novo valor.
  //   d ? { ...d, status } : null: copia o objeto com o status atualizado
  //
  // Após a API confirmar, recarrega a lista completa para garantir
  // que os dados estejam sincronizados com o banco.
  // -------------------------------------------------------
  atualizarStatus(p: PedidoApi, status: string) {
    this.pedidoService.atualizarStatus(p.id, status).subscribe({
      next: () => this.carregarPedidos(), // Recarrega lista após sucesso
      error: () => {}
    });

    // Atualiza o modal de detalhe imediatamente (feedback visual instantâneo)
    if (this.pedidoDetalhe()?.id === p.id) {
      this.pedidoDetalhe.update(d => d ? { ...d, status } : null);
    }
  }

  // Retorna a classe CSS do badge de status
  getStatusClass(s: string): string {
    const m: Record<string, string> = {
      pendente: 'status-pendente', confirmado: 'status-pago',
      em_separacao: 'status-pago', enviado: 'status-enviado',
      entregue: 'status-entregue', cancelado: 'status-cancelado'
    };
    return m[s] || '';
  }

  // Retorna o texto legível do status
  getStatusLabel(s: string): string {
    const m: Record<string, string> = {
      pendente: 'Pendente', confirmado: 'Confirmado', em_separacao: 'Em separação',
      enviado: 'Enviado', entregue: 'Entregue', cancelado: 'Cancelado'
    };
    return m[s] || s;
  }

  // Formata valores monetários em Real
  formatCurrency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
