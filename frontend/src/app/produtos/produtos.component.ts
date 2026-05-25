import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
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
  produtos: Produto[] = [];
  todosProdutos: Produto[] = [];
  categorias: Categoria[] = [];

  ordenacao            = 'padrao';
  categoriaSelecionada: number | null = null;
  filtroAberto         = false;
  ordenacaoAberta      = false;
  carregando           = true;

  constructor(
    private elRef: ElementRef,
    private produtoService: ProdutoService,
    private categoriaService: CategoriaService
  ) {}

  ngOnInit(): void {
    this.categoriaService.listar().subscribe({
      next: (cats) => { this.categorias = cats; },
      error: () => {}
    });

    this.produtoService.listar().subscribe({
      next: (lista) => {
        this.todosProdutos = lista;
        this.carregando = false;
        this.aplicarFiltros();
      },
      error: () => { this.carregando = false; }
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elRef.nativeElement.contains(event.target)) {
      this.ordenacaoAberta = false;
      this.filtroAberto    = false;
    }
  }

  toggleOrdenacao(event: MouseEvent) {
    event.stopPropagation();
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
    this.aplicarFiltros();
  }

  selecionarCategoria(id: number | null) {
    this.categoriaSelecionada = id;
    this.filtroAberto = false;
    this.aplicarFiltros();
  }

  aplicarFiltros() {
    let lista = [...this.todosProdutos];

    if (this.categoriaSelecionada !== null) {
      lista = lista.filter(p => p.categoria_id === this.categoriaSelecionada);
    }

    if (this.ordenacao === 'menor-preco') {
      lista.sort((a, b) => (a.preco_desconto ?? a.preco) - (b.preco_desconto ?? b.preco));
    } else if (this.ordenacao === 'maior-preco') {
      lista.sort((a, b) => (b.preco_desconto ?? b.preco) - (a.preco_desconto ?? a.preco));
    } else if (this.ordenacao === 'novidades') {
      lista.sort((a, b) => new Date(b.criado_em).getTime() - new Date(a.criado_em).getTime());
    }

    this.produtos = lista;
  }

  precoFinal(p: Produto): number   { return p.preco_desconto ?? p.preco; }
  temDesconto(p: Produto): boolean { return p.preco_desconto !== null && p.preco_desconto! < p.preco; }
}
