import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService, PerfilApi } from '../../core/services/usuario.service';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-usuarios.component.html',
  styleUrls: ['./admin-usuarios.component.css']
})
export class AdminUsuariosComponent implements OnInit {
  usuarios           = signal<PerfilApi[]>([]);
  busca              = signal('');
  usuarioSelecionado = signal<PerfilApi | null>(null);
  confirmacao        = signal<{ acao: string; usuario: PerfilApi } | null>(null);
  carregando         = signal(true);

  filtrados = computed(() => {
    const q = this.busca().toLowerCase();
    if (!q) return this.usuarios();
    return this.usuarios().filter(u =>
      u.nome.toLowerCase().includes(q) || u.email.toLowerCase().includes(q)
    );
  });

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  carregarUsuarios(): void {
    this.carregando.set(true);
    this.usuarioService.listarTodos().subscribe({
      next: (lista) => { this.usuarios.set(lista); this.carregando.set(false); },
      error: () => { this.carregando.set(false); }
    });
  }

  verDetalhes(u: PerfilApi) { this.usuarioSelecionado.set(u); }
  fecharModal()              { this.usuarioSelecionado.set(null); }

  confirmar(acao: string, u: PerfilApi) { this.confirmacao.set({ acao, usuario: u }); }
  cancelarConfirmacao()                  { this.confirmacao.set(null); }

  executarAcao(): void {
    const conf = this.confirmacao();
    if (!conf) return;

    const novoPapel =
      conf.acao === 'promover'  ? 'admin'  :
      conf.acao === 'rebaixar'  ? 'cliente' : null;

    if (novoPapel) {
      this.usuarioService.atualizarPapel(conf.usuario.id, novoPapel).subscribe({
        next: () => { this.confirmacao.set(null); this.carregarUsuarios(); },
        error: () => { this.confirmacao.set(null); }
      });
    } else {
      this.confirmacao.set(null);
    }

    if (this.usuarioSelecionado()?.id === conf.usuario.id) {
      this.usuarioSelecionado.set(null);
    }
  }

  formatCurrency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
