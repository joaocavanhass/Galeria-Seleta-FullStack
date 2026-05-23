import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PEDIDOS_MOCK } from '../mocks/admin.mocks';
import { Pedido } from '../models/admin.models';

type PedidoStatus = 'pendente' | 'pago' | 'enviado' | 'entregue' | 'cancelado';

@Component({
  selector: 'app-admin-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-pedidos.component.html',
  styleUrls: ['./admin-pedidos.component.css']
})
export class AdminPedidosComponent {
  pedidos = signal<Pedido[]>([...PEDIDOS_MOCK]);
  filtroStatus = signal('todos');
  pedidoDetalhe = signal<Pedido | null>(null);

  statusOptions: PedidoStatus[] = ['pendente', 'pago', 'enviado', 'entregue', 'cancelado'];

  filtrados = computed(() => {
    if (this.filtroStatus() === 'todos') return this.pedidos();
    return this.pedidos().filter(p => p.status === this.filtroStatus());
  });

  verDetalhe(p: Pedido) { this.pedidoDetalhe.set(p); }
  fecharDetalhe() { this.pedidoDetalhe.set(null); }

  atualizarStatus(p: Pedido, status: PedidoStatus) {
    this.pedidos.update(lista => lista.map(x =>
      x.id === p.id ? { ...x, status, atualizado_em: new Date().toISOString().slice(0,10) } : x
    ));
    if (this.pedidoDetalhe()?.id === p.id) {
      this.pedidoDetalhe.update(d => d ? { ...d, status } : null);
    }
  }

  getStatusClass(s: string): string {
    const m: Record<string, string> = { pendente:'status-pendente', pago:'status-pago', enviado:'status-enviado', entregue:'status-entregue', cancelado:'status-cancelado' };
    return m[s] || '';
  }

  getStatusLabel(s: string): string {
    const m: Record<string, string> = { pendente:'Pendente', pago:'Pago', enviado:'Enviado', entregue:'Entregue', cancelado:'Cancelado' };
    return m[s] || s;
  }

  getPagamentoLabel(p: string): string {
    const m: Record<string, string> = { cartao_credito:'Cartão de Crédito', pix:'PIX', boleto:'Boleto' };
    return m[p] || p;
  }

  formatCurrency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
