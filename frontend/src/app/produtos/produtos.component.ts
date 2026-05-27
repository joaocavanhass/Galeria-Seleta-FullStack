// ============================================================
// ARQUIVO: produtos.component.ts
// FUNÇÃO: Componente da página de listagem de produtos (/produtos).
//
// RESPONSABILIDADES:
// - Buscar todos os produtos e categorias da API
// - Filtrar por categoria e ordenar por preço ou data
// - Controlar abertura/fechamento dos dropdowns de filtro e ordenação
//
// PADRÃO DE FILTRO LOCAL:
// Os produtos são buscados uma vez da API e armazenados em todosProdutos[].
// Quando o usuário muda filtro ou ordenação, aplicarFiltros() cria uma
// nova lista "produtos[]" a partir de todosProdutos[] sem fazer nova
// chamada à API (filtro local, mais rápido).
//
// @HostListener('document:click', ['$event']):
// Escuta cliques em QUALQUER lugar do documento.
// Fecha os dropdowns quando o usuário clica fora deles.
// ElRef.nativeElement.contains(event.target): verifica se o clique
// foi dentro do componente ou fora.
// ============================================================

import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
// CommonModule: *ngIf, *ngFor | CurrencyPipe: formata valores monetários
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Produto } from '../core/models/produto.model';
import { Categoria } from '../core/models/categoria.model';
import { ProdutoService } from '../core/services/produto.service';
import { CategoriaService } from '../core/services/categoria.service';

@Component({
  selector: 'app-produtos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, CurrencyPipe],
  templateUrl: './produtos.component.html',
  styleUrl: './produtos.component.css'
})
export class ProdutosComponent implements OnInit {

  produtos: Produto[]      = []; // Lista filtrada/ordenada (exibida no template)
  todosProdutos: Produto[] = []; // Lista completa da API (fonte de verdade)
  categorias: Categoria[]  = []; // Lista de categorias para o filtro

  ordenacao            = 'padrao';     // Ordenação selecionada pelo usuário
  categoriaSelecionada: number | null = null; // ID da categoria selecionada (null = todas)
  filtroAberto         = false;         // Controla dropdown de filtro por categoria
  ordenacaoAberta      = false;         // Controla dropdown de ordenação
  carregando           = true;          // Indicador de carregamento da API

  // ElementRef: referência ao elemento DOM do componente (usado para detectar cliques fora)
  constructor(
    private elRef: ElementRef,
    private produtoService: ProdutoService,
    private categoriaService: CategoriaService
  ) {}

  ngOnInit(): void {
    // Busca categorias para popular o dropdown de filtro
    this.categoriaService.listar().subscribe({
      next: (cats) => { this.categorias = cats; },
      error: () => {}
    });

    // Busca todos os produtos ativos da API
    this.produtoService.listar().subscribe({
      next: (lista) => {
        this.todosProdutos = lista; // Salva a lista completa para filtros locais
        this.carregando = false;
        this.aplicarFiltros(); // Aplica filtros/ordenação iniciais
      },
      error: () => { this.carregando = false; }
    });
  }

  // -------------------------------------------------------
  // @HostListener: escuta eventos no host (document, neste caso)
  // 'document:click': qualquer clique na página
  // ['$event']: passa o evento como argumento para o método
  //
  // Fecha os dropdowns ao clicar fora do componente.
  // elRef.nativeElement: o elemento <app-produtos> no DOM
  // .contains(event.target): true se o clique foi dentro do componente
  // -------------------------------------------------------
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elRef.nativeElement.contains(event.target)) {
      this.ordenacaoAberta = false;
      this.filtroAberto    = false;
    }
  }

  // Alterna o dropdown de ordenação (fecha o de filtro para não sobrepor)
  toggleOrdenacao(event: MouseEvent) {
    event.stopPropagation(); // Impede que o clique suba para o @HostListener e feche tudo
    this.ordenacaoAberta = !this.ordenacaoAberta;
    this.filtroAberto = false;
  }

  toggleFiltro(event: MouseEvent) {
    event.stopPropagation();
    this.filtroAberto    = !this.filtroAberto;
    this.ordenacaoAberta = false;
  }

  selecionarOrdenacao(valor: string) {
    this.ordenacao      = valor;
    this.ordenacaoAberta = false;
    this.aplicarFiltros(); // Re-aplica com a nova ordenação
  }

  selecionarCategoria(id: number | null) {
    this.categoriaSelecionada = id;
    this.filtroAberto = false;
    this.aplicarFiltros(); // Re-aplica com o novo filtro
  }

  // -------------------------------------------------------
  // aplicarFiltros(): filtra e ordena os produtos localmente
  //
  // [...this.todosProdutos]: cria uma CÓPIA do array (não modifica o original)
  // .filter(): cria novo array com apenas os itens que passam na condição
  // .sort(): ordena o array no lugar
  // -------------------------------------------------------
  aplicarFiltros() {
    let lista = [...this.todosProdutos]; // Cópia para não modificar a lista original

    // Filtra por categoria se alguma estiver selecionada
    if (this.categoriaSelecionada !== null) {
      lista = lista.filter(p => p.categoria_id === this.categoriaSelecionada);
    }

    // Aplica a ordenação selecionada
    if (this.ordenacao === 'menor-preco') {
      // Ordena crescente pelo preço final (desconto ou preço normal)
      lista.sort((a, b) => (a.preco_desconto ?? a.preco) - (b.preco_desconto ?? b.preco));
    } else if (this.ordenacao === 'maior-preco') {
      // Ordena decrescente (b - a inverte a ordem)
      lista.sort((a, b) => (b.preco_desconto ?? b.preco) - (a.preco_desconto ?? a.preco));
    } else if (this.ordenacao === 'novidades') {
      // Ordena pela data de criação (mais recente primeiro)
      // new Date().getTime(): converte a data para número de milissegundos
      lista.sort((a, b) => new Date(b.criado_em).getTime() - new Date(a.criado_em).getTime());
    }

    this.produtos = lista; // Atualiza a lista exibida no template
  }

  precoFinal(p: Produto): number   { return p.preco_desconto ?? p.preco; }
  temDesconto(p: Produto): boolean { return p.preco_desconto !== null && p.preco_desconto! < p.preco; }
}
