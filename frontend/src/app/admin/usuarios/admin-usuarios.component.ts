import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { USUARIOS_MOCK } from '../mocks/admin.mocks';
import { Usuario } from '../models/admin.models';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-usuarios.component.html',
  styleUrls: ['./admin-usuarios.component.css']
})
export class AdminUsuariosComponent {
  usuarios = signal<Usuario[]>([...USUARIOS_MOCK]);
  busca = signal('');
  usuarioSelecionado = signal<Usuario | null>(null);
  confirmacao = signal<{acao: string; usuario: Usuario} | null>(null);

  filtrados = computed(() => {
    const q = this.busca().toLowerCase();
    if (!q) return this.usuarios();
    return this.usuarios().filter(u =>
      u.nome.toLowerCase().includes(q) || u.email.toLowerCase().includes(q)
    );
  });

  verDetalhes(u: Usuario) { this.usuarioSelecionado.set(u); }
  fecharModal() { this.usuarioSelecionado.set(null); }

  confirmar(acao: string, u: Usuario) {
    this.confirmacao.set({ acao, usuario: u });
  }
  cancelarConfirmacao() { this.confirmacao.set(null); }

  executarAcao() {
    const conf = this.confirmacao();
    if (!conf) return;
    this.usuarios.update(lista => lista.map(u => {
      if (u.id !== conf.usuario.id) return u;
      if (conf.acao === 'bloquear')   return { ...u, status: 'bloqueado' as const };
      if (conf.acao === 'desbloquear') return { ...u, status: 'ativo' as const };
      if (conf.acao === 'promover')   return { ...u, papel: 'admin' as const };
      if (conf.acao === 'rebaixar')   return { ...u, papel: 'cliente' as const };
      return u;
    }));
    this.confirmacao.set(null);
    if (this.usuarioSelecionado()?.id === conf.usuario.id) {
      const atualizado = this.usuarios().find(u => u.id === conf.usuario.id);
      this.usuarioSelecionado.set(atualizado ?? null);
    }
  }

  formatCurrency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
