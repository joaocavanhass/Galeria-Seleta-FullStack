import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Produto } from '../core/models/produto.model';
import { Categoria } from '../core/models/categoria.model';
import { PRODUTOS_MOCK } from '../core/mocks/produtos.mock';
import { CATEGORIAS_MOCK } from '../core/mocks/categorias.mock';

@Component({
  selector: 'app-produtos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, CurrencyPipe],
  templateUrl: './produtos.component.html',
  styleUrl: './produtos.component.css'
})
export class ProdutosComponent implements OnInit {
  produtos: Produto[] = [];
  categorias: Categoria[] = CATEGORIAS_MOCK;

  ordenacao = 'padrao';
  categoriaSelecionada: number | null = null;
  filtroAberto = false;
  ordenacaoAberta = false;

  ngOnInit() {
    this.aplicarFiltros();
  }

  aplicarFiltros() {
    let lista = [...PRODUTOS_MOCK];

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

  selecionarCategoria(id: number | null) {
    this.categoriaSelecionada = id;
    this.filtroAberto = false;
    this.aplicarFiltros();
  }

  precoFinal(p: Produto): number {
    return p.preco_desconto ?? p.preco;
  }

  temDesconto(p: Produto): boolean {
    return p.preco_desconto !== null && p.preco_desconto < p.preco;
  }
}
