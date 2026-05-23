import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PEDIDOS_MOCK, DASHBOARD_STATS } from '../mocks/admin.mocks';
import { Pedido } from '../models/admin.models';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  stats = DASHBOARD_STATS;
  pedidosRecentes: Pedido[] = [];
  maxVenda = 0;

  ngOnInit() {
    this.pedidosRecentes = [...PEDIDOS_MOCK].slice(0, 5);
    this.maxVenda = Math.max(...this.stats.vendasSemana.map(v => v.valor));
  }

  getBarHeight(valor: number): number {
    return Math.round((valor / this.maxVenda) * 100);
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      pendente:  'status-pendente',
      pago:      'status-pago',
      enviado:   'status-enviado',
      entregue:  'status-entregue',
      cancelado: 'status-cancelado'
    };
    return map[status] || '';
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      pendente:  'Pendente',
      pago:      'Pago',
      enviado:   'Enviado',
      entregue:  'Entregue',
      cancelado: 'Cancelado'
    };
    return map[status] || status;
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
