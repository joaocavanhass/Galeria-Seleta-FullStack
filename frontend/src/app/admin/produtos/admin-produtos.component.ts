// ============================================================
// ARQUIVO: admin-produtos.component.ts
// FUNÇÃO: Componente de gerenciamento de produtos no painel admin (/admin/produtos).
//
// RESPONSABILIDADES:
// - Listar todos os produtos (ativos e inativos)
// - Filtrar por nome/categoria (computed Signal)
// - Criar e editar produtos via modal
// - Ativar/desativar produto (toggle de status)
// - Excluir produto com confirmação
//
// FLUXO DE SALVAR PRODUTO:
// 1. Validar campos obrigatórios (nome, preço, categoria)
// 2. Montar ProdutoRequest com os dados do formulário
// 3. Se p.id existe → atualizar; se não → criar
// 4. Se há URL de imagem → chamar adicionarFoto()
// 5. Recarregar a lista de produtos
//
// MAPEAMENTO DE STATUS:
// Frontend usa "ativo"/"inativo"; backend usa "disponivel"/"indisponivel"
// A conversão acontece ao montar o ProdutoRequest.
//
// COMPUTED SIGNAL — filtrados:
// Aplica filtros de busca (nome/categoria), status e categoria em sequência.
// Cada filtro é aplicado sobre o resultado do anterior (encadeado).
// ============================================================

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

  // Signals de estado
  produtos        = signal<Produto[]>([]);           // Lista completa de produtos da API
  categorias      = signal<Categoria[]>([]);          // Lista de categorias para o select
  busca           = signal('');                       // Texto do campo de busca
  filtroStatus    = signal('todos');                  // Filtro de status ('todos', 'ativo', 'inativo')
  filtroCategoria = signal('todas');                  // Filtro por categoria ('todas' = sem filtro)
  modalAberto     = signal(false);                    // Controla visibilidade do modal de edição
  produtoEditando = signal<Partial<Produto> | null>(null); // Produto sendo editado/criado
  confirmDelete   = signal<Produto | null>(null);     // Produto selecionado para exclusão (confirmação)
  carregando      = signal(true);
  salvando        = signal(false);   // true enquanto a API processa o save
  erroSalvar      = signal('');      // Mensagem de erro do formulário de edição

  // -------------------------------------------------------
  // filtrados: encadeia 3 filtros em sequência
  //   1. Filtro de texto (nome ou categoria)
  //   2. Filtro de status (ativo/inativo)
  //   3. Filtro de categoria (nome da categoria)
  // -------------------------------------------------------
  filtrados = computed(() => {
    let lista = this.produtos();
    const q = this.busca().toLowerCase();

    // 1. Filtro de texto
    if (q) lista = lista.filter(p =>
      p.nome.toLowerCase().includes(q) || p.categoria?.toLowerCase().includes(q)
    );

    // 2. Filtro de status
    if (this.filtroStatus() !== 'todos')    lista = lista.filter(p => p.status === this.filtroStatus());

    // 3. Filtro de categoria (pelo nome da categoria)
    if (this.filtroCategoria() !== 'todas') lista = lista.filter(p => p.categoria === this.filtroCategoria());

    return lista;
  });

  constructor(
    private produtoService:  ProdutoService,
    private categoriaService: CategoriaService
  ) {}

  ngOnInit(): void {
    this.carregarProdutos();
    // Carrega categorias para popular o select do formulário
    this.categoriaService.listar().subscribe({
      next: (cats) => this.categorias.set(cats),
      error: () => {}
    });
  }

  carregarProdutos(): void {
    this.carregando.set(true);
    // status: 'todos' → carrega ativos e inativos
    this.produtoService.listar({ status: 'todos' }).subscribe({
      next: (lista) => { this.produtos.set(lista); this.carregando.set(false); },
      error: ()      => { this.carregando.set(false); }
    });
  }

  // Abre o modal para criar um novo produto (objeto inicial com valores padrão)
  abrirNovoProduto() {
    this.produtoEditando.set({ nome: '', descricao: '', preco: 0, status: 'ativo', categoria: '' });
    this.modalAberto.set(true);
  }

  // Abre o modal para editar um produto existente (cópia do objeto para não modificar o original)
  abrirEditar(p: Produto) {
    this.produtoEditando.set({ ...p }); // Spread: cópia rasa do objeto
    this.modalAberto.set(true);
  }

  fecharModal() {
    this.modalAberto.set(false);
    this.produtoEditando.set(null);
    this.erroSalvar.set('');
  }

  // -------------------------------------------------------
  // salvarProduto(): valida e persiste o produto no backend
  //
  // Validações:
  //   - Nome obrigatório (.trim(): remove espaços em branco)
  //   - Preço > 0
  //   - Categoria selecionada (pelo nome ou pelo ID)
  //
  // MAPEAMENTO DE STATUS:
  //   "ativo"   → "disponivel"   (formato do backend)
  //   "inativo" → "indisponivel" (formato do backend)
  //
  // OBS → obs: operador ternário que decide entre criar ou atualizar
  //   p.id existe → atualizar; não existe → criar
  //
  // FOTO: se o admin informou uma URL de imagem, chama adicionarFoto()
  //   após salvar o produto.
  // -------------------------------------------------------
  salvarProduto() {
    const p = this.produtoEditando();
    this.erroSalvar.set('');

    // Validações obrigatórias
    if (!p || !p.nome?.trim()) { this.erroSalvar.set('Nome é obrigatório.'); return; }
    if (!p.preco || p.preco <= 0) { this.erroSalvar.set('Informe um preço válido.'); return; }

    // Busca a categoria pelo nome (para obter o ID)
    const categoria = this.categorias().find(c => c.nome === p.categoria);
    if (!categoria && !p.categoria_id) { this.erroSalvar.set('Selecione uma categoria.'); return; }

    this.salvando.set(true);

    // Monta o ProdutoRequest para envio à API
    const request: ProdutoRequest = {
      nome: p.nome!,
      descricao: p.descricao ?? undefined,
      preco: p.preco!,
      // Converte o status do frontend para o formato do backend
      status: p.status === 'ativo' ? 'disponivel' : p.status === 'inativo' ? 'indisponivel' : 'rascunho',
      novidade: false,
      categoriaId: categoria?.id ?? p.categoria_id! // Usa o ID da categoria encontrada ou o existente
    };

    // Decide entre criar (sem ID) ou atualizar (com ID)
    const obs = p.id
      ? this.produtoService.atualizar(p.id, request)
      : this.produtoService.criar(request);

    obs.subscribe({
      next: (salvo) => {
        // (salvo as any).id: extrai o ID do produto recém-criado/atualizado
        const finalId: number = (salvo as any).id ?? p.id!;
        const imagemUrl = p.imagem_url?.trim();

        if (imagemUrl) {
          // Se há URL de imagem, faz upload após salvar o produto
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
        // Tenta extrair a mensagem de erro da resposta do backend
        this.erroSalvar.set(err?.error?.erro ?? err?.error?.message ?? 'Erro ao salvar produto. Tente novamente.');
      }
    });
  }

  // Alterna o status do produto entre ativo ("disponivel") e inativo ("indisponivel")
  toggleStatus(p: Produto) {
    const novoStatus = p.status === 'ativo' ? 'indisponivel' : 'disponivel';
    this.produtoService.atualizarStatus(p.id, novoStatus).subscribe({
      next: () => this.carregarProdutos(),
      error: () => {}
    });
  }

  // Modal de confirmação de exclusão
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

  // Getter: retorna apenas os nomes das categorias para o select do formulário
  get nomesCategorias(): string[] {
    return this.categorias().map(c => c.nome);
  }

  formatCurrency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
