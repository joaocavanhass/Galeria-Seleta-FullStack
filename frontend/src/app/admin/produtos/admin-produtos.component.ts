import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PRODUTOS_MOCK } from '../../core/mocks/produtos.mock';
import { Produto } from '../../core/models/produto.model';

@Component({
  selector: 'app-admin-produtos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-produtos.component.html',
  styleUrls: ['./admin-produtos.component.css']
})
export class AdminProdutosComponent {
  produtos = signal<Produto[]>([...PRODUTOS_MOCK]);
  busca = signal('');
  filtroStatus = signal('todos');
  filtroCategoria = signal('todas');
  modalAberto = signal(false);
  produtoEditando = signal<Partial<Produto> | null>(null);
  confirmDelete = signal<Produto | null>(null);

  categorias = ['Colares', 'Anéis', 'Brincos', 'Pulseiras', 'Tornozeleiras'];

  filtrados = computed(() => {
    let lista = this.produtos();
    const q = this.busca().toLowerCase();
    if (q) lista = lista.filter(p => p.nome.toLowerCase().includes(q) || p.categoria?.toLowerCase().includes(q));
    if (this.filtroStatus() !== 'todos') lista = lista.filter(p => p.status === this.filtroStatus());
    if (this.filtroCategoria() !== 'todas') lista = lista.filter(p => p.categoria === this.filtroCategoria());
    return lista;
  });

  abrirNovoProduto() {
    this.produtoEditando.set({ nome: '', descricao: '', preco: 0, estoque: 0, status: 'rascunho', categoria: '' });
    this.modalAberto.set(true);
  }

  abrirEditar(p: Produto) {
    this.produtoEditando.set({ ...p });
    this.modalAberto.set(true);
  }

  fecharModal() { this.modalAberto.set(false); this.produtoEditando.set(null); }

  salvarProduto() {
    const p = this.produtoEditando();
    if (!p) return;
    if (p.id) {
      this.produtos.update(lista => lista.map(x => x.id === p.id ? { ...x, ...p } as Produto : x));
    } else {
      const newId = Math.max(...this.produtos().map(x => x.id)) + 1;
      this.produtos.update(lista => [...lista, { ...p, id: newId, categoria_id: 1, criado_em: new Date().toISOString() } as Produto]);
    }
    this.fecharModal();
  }

  toggleStatus(p: Produto) {
    const novo = p.status === 'ativo' ? 'inativo' : 'ativo';
    this.produtos.update(lista => lista.map(x => x.id === p.id ? { ...x, status: novo as any } : x));
  }

  confirmarDelete(p: Produto) { this.confirmDelete.set(p); }
  cancelarDelete() { this.confirmDelete.set(null); }
  excluirProduto() {
    const p = this.confirmDelete();
    if (p) this.produtos.update(lista => lista.filter(x => x.id !== p.id));
    this.confirmDelete.set(null);
  }

  formatCurrency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
