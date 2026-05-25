import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Produto } from '../../core/models/produto.model';
import { ProdutoService, ApiProduto, ProdutoRequest } from '../../core/services/produto.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { Categoria } from '../../core/models/categoria.model';

@Component({
  selector: 'app-admin-produtos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-produtos.component.html',
  styleUrls: ['./admin-produtos.component.css']
})
export class AdminProdutosComponent implements OnInit {
  produtos        = signal<Produto[]>([]);
  categorias      = signal<Categoria[]>([]);
  busca           = signal('');
  filtroStatus    = signal('todos');
  filtroCategoria = signal('todas');
  modalAberto     = signal(false);
  produtoEditando = signal<Partial<Produto> | null>(null);
  confirmDelete   = signal<Produto | null>(null);
  carregando      = signal(true);
  salvando        = signal(false);
  erroSalvar      = signal('');

  filtrados = computed(() => {
    let lista = this.produtos();
    const q = this.busca().toLowerCase();
    if (q) lista = lista.filter(p =>
      p.nome.toLowerCase().includes(q) || p.categoria?.toLowerCase().includes(q)
    );
    if (this.filtroStatus() !== 'todos')  lista = lista.filter(p => p.status === this.filtroStatus());
    if (this.filtroCategoria() !== 'todas') lista = lista.filter(p => p.categoria === this.filtroCategoria());
    return lista;
  });

  constructor(
    private produtoService: ProdutoService,
    private categoriaService: CategoriaService
  ) {}

  ngOnInit(): void {
    this.carregarProdutos();
    this.categoriaService.listar().subscribe({
      next: (cats) => this.categorias.set(cats),
      error: () => {}
    });
  }

  carregarProdutos(): void {
    this.carregando.set(true);
    this.produtoService.listar().subscribe({
      next: (lista) => { this.produtos.set(lista); this.carregando.set(false); },
      error: () => { this.carregando.set(false); }
    });
  }

  abrirNovoProduto() {
    this.produtoEditando.set({ nome: '', descricao: '', preco: 0, status: 'rascunho', categoria: '' });
    this.modalAberto.set(true);
  }

  abrirEditar(p: Produto) {
    this.produtoEditando.set({ ...p });
    this.modalAberto.set(true);
  }

  fecharModal() { this.modalAberto.set(false); this.produtoEditando.set(null); this.erroSalvar.set(''); }

  salvarProduto() {
    const p = this.produtoEditando();
    this.erroSalvar.set('');

    if (!p || !p.nome?.trim()) { this.erroSalvar.set('Nome é obrigatório.'); return; }
    if (!p.preco || p.preco <= 0) { this.erroSalvar.set('Informe um preço válido.'); return; }

    const categoria = this.categorias().find(c => c.nome === p.categoria);
    if (!categoria && !p.categoria_id) { this.erroSalvar.set('Selecione uma categoria.'); return; }

    this.salvando.set(true);

    const request: ProdutoRequest = {
      nome: p.nome!,
      descricao: p.descricao ?? undefined,
      preco: p.preco!,
      status: p.status === 'ativo' ? 'disponivel' : p.status === 'inativo' ? 'indisponivel' : 'rascunho',
      novidade: false,
      categoriaId: categoria?.id ?? p.categoria_id!
    };

    const obs = p.id
      ? this.produtoService.atualizar(p.id, request)
      : this.produtoService.criar(request);

    obs.subscribe({
      next: (salvo) => {
        const finalId: number = (salvo as any).id ?? p.id!;
        const imagemUrl = p.imagem_url?.trim();
        if (imagemUrl) {
          this.produtoService.adicionarFoto(finalId, imagemUrl).subscribe({
            next:  () => { this.salvando.set(false); this.fecharModal(); this.carregarProdutos(); },
            error: () => { this.salvando.set(false); this.fecharModal(); this.carregarProdutos(); }
          });
        } else {
          this.salvando.set(false); this.fecharModal(); this.carregarProdutos();
        }
      },
      error: (err) => {
        this.salvando.set(false);
        this.erroSalvar.set(err?.error?.erro ?? err?.error?.message ?? 'Erro ao salvar produto. Tente novamente.');
      }
    });
  }

  toggleStatus(p: Produto) {
    const novoStatus = p.status === 'ativo' ? 'indisponivel' : 'disponivel';
    this.produtoService.atualizarStatus(p.id, novoStatus).subscribe({
      next: () => this.carregarProdutos(),
      error: () => {}
    });
  }

  confirmarDelete(p: Produto) { this.confirmDelete.set(p); }
  cancelarDelete()            { this.confirmDelete.set(null); }

  excluirProduto() {
    const p = this.confirmDelete();
    if (!p) return;
    this.produtoService.deletar(p.id).subscribe({
      next: () => { this.confirmDelete.set(null); this.carregarProdutos(); },
      error: () => { this.confirmDelete.set(null); }
    });
  }

  get nomesCategorias(): string[] {
    return this.categorias().map(c => c.nome);
  }

  formatCurrency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
