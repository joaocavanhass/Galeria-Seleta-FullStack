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
  pedidos        = signal<PedidoApi[]>([]);
  filtroStatus   = signal('todos');
  pedidoDetalhe  = signal<PedidoApi | null>(null);
  carregando     = signal(true);

  statusOptions = ['pendente', 'confirmado', 'em_separacao', 'enviado', 'entregue', 'cancelado'];

  filtrados = computed(() => {
    if (this.filtroStatus() === 'todos') return this.pedidos();
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
      error: () => { this.carregando.set(false); }
    });
  }

  verDetalhe(p: PedidoApi) { this.pedidoDetalhe.set(p); }
  fecharDetalhe()           { this.pedidoDetalhe.set(null); }

  atualizarStatus(p: PedidoApi, status: string) {
    this.pedidoService.atualizarStatus(p.id, status).subscribe({
      next: () => this.carregarPedidos(),
      error: () => {}
    });
    if (this.pedidoDetalhe()?.id === p.id) {
      this.pedidoDetalhe.update(d => d ? { ...d, status } : null);
    }
  }

  getStatusClass(s: string): string {
    const m: Record<string, string> = {
      pendente: 'status-pendente', confirmado: 'status-pago',
      em_separacao: 'status-pago', enviado: 'status-enviado',
      entregue: 'status-entregue', cancelado: 'status-cancelado'
    };
    return m[s] || '';
  }

  getStatusLabel(s: string): string {
    const m: Record<string, string> = {
      pendente: 'Pendente', confirmado: 'Confirmado', em_separacao: 'Em separação',
      enviado: 'Enviado', entregue: 'Entregue', cancelado: 'Cancelado'
    };
    return m[s] || s;
  }

  formatCurrency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
