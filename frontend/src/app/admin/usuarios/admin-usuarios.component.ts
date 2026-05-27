// ============================================================
// ARQUIVO: admin-usuarios.component.ts
// FUNÇÃO: Componente de gerenciamento de usuários no painel admin (/admin/usuarios).
//
// RESPONSABILIDADES:
// - Listar todos os usuários da plataforma
// - Filtrar por nome ou email em tempo real (computed Signal)
// - Visualizar detalhes de um usuário em modal
// - Promover usuário para admin ou rebaixar para cliente
//
// COMPUTED SIGNAL — filtrados:
// Recalculado automaticamente toda vez que busca() ou usuarios() mudarem.
// .toLowerCase().includes(q): busca case-insensitive (não diferencia maiúsculas)
//
// FLUXO DE CONFIRMAÇÃO:
// Para ações destrutivas/importantes (promover/rebaixar), o admin vê
// um modal de confirmação antes de a ação ser executada.
// confirmacao: armazena { acao, usuario } até o admin confirmar ou cancelar.
//
// PADRÃO CRUD SIMPLES:
// carregarUsuarios() → listarTodos() → atualiza o Signal usuarios()
// executarAcao() → atualizarPapel() → recarrega a lista
// ============================================================

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

  // Signals de estado
  usuarios           = signal<PerfilApi[]>([]);                    // Lista completa de usuários
  busca              = signal('');                                  // Texto digitado no campo de busca
  usuarioSelecionado = signal<PerfilApi | null>(null);             // Usuário aberto no modal de detalhes
  confirmacao        = signal<{ acao: string; usuario: PerfilApi } | null>(null); // Modal de confirmação
  carregando         = signal(true);

  // -------------------------------------------------------
  // filtrados: Signal computado que filtra a lista em tempo real
  //
  // computed(): recalcula quando busca() ou usuarios() mudam.
  // q.toLowerCase(): normaliza para minúsculas
  // .includes(q): verifica se o texto contém a busca (parcial)
  // -------------------------------------------------------
  filtrados = computed(() => {
    const q = this.busca().toLowerCase();
    if (!q) return this.usuarios(); // Sem busca: retorna todos
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
      error: ()      => { this.carregando.set(false); }
    });
  }

  // Abre o modal de detalhes para um usuário específico
  verDetalhes(u: PerfilApi) { this.usuarioSelecionado.set(u); }
  fecharModal()              { this.usuarioSelecionado.set(null); }

  // Abre o modal de confirmação para uma ação específica
  // acao: 'promover' ou 'rebaixar'
  confirmar(acao: string, u: PerfilApi) { this.confirmacao.set({ acao, usuario: u }); }
  cancelarConfirmacao()                  { this.confirmacao.set(null); }

  // -------------------------------------------------------
  // executarAcao(): executa a ação confirmada pelo admin
  //
  // Determina o novo papel baseado na ação:
  //   'promover' → 'admin'
  //   'rebaixar' → 'cliente'
  //   null → ação desconhecida, não faz nada
  //
  // Após sucesso: fecha o modal de confirmação e recarrega a lista.
  // Se o usuário estava no modal de detalhes: fecha o modal também.
  // -------------------------------------------------------
  executarAcao(): void {
    const conf = this.confirmacao();
    if (!conf) return;

    // Determina o novo papel baseado na ação
    const novoPapel =
      conf.acao === 'promover' ? 'admin'   :
      conf.acao === 'rebaixar' ? 'cliente' : null;

    if (novoPapel) {
      this.usuarioService.atualizarPapel(conf.usuario.id, novoPapel).subscribe({
        next: () => {
          this.confirmacao.set(null); // Fecha o modal de confirmação
          this.carregarUsuarios();    // Recarrega para refletir a mudança
        },
        error: () => { this.confirmacao.set(null); }
      });
    } else {
      this.confirmacao.set(null);
    }

    // Se o usuário está aberto no modal de detalhes, fecha ele também
    if (this.usuarioSelecionado()?.id === conf.usuario.id) {
      this.usuarioSelecionado.set(null);
    }
  }

  // Formata valores monetários em Real
  formatCurrency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
