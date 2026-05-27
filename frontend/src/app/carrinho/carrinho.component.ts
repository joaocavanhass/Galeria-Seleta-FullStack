// ============================================================
// ARQUIVO: carrinho.component.ts
// FUNÇÃO: Componente da página do carrinho de compras (/carrinho).
//
// PADRÃO DE DELEGAÇÃO:
// Este componente é intencionalmente simples. Não mantém estado próprio —
// toda a lógica e os dados estão no CarrinhoService (que usa Signals).
// O componente apenas expõe os valores do serviço via getters
// e delega as ações (aumentar, diminuir, remover) de volta ao serviço.
//
// Por que usar getters em vez de acessar o serviço diretamente no template?
// - Mais legível no template ({{ itens }} em vez de {{ srv.itens() }})
// - Centraliza os acessos em um lugar se precisar adicionar lógica
//
// REATIVIDADE:
// Como os dados vêm de Signals do CarrinhoService,
// qualquer mudança no carrinho (adicionar, remover, atualizar) atualiza
// o template automaticamente.
// ============================================================

import { Component, inject } from '@angular/core';
// CommonModule: *ngIf, *ngFor | FormsModule: [(ngModel)]
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
// RouterLink: link para /checkout
import { RouterLink } from '@angular/router';
import { CarrinhoService } from '../core/services/carrinho.service';

@Component({
  selector: 'app-carrinho',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './carrinho.component.html',
  styleUrls: ['./carrinho.component.css']
})
export class CarrinhoComponent {

  // inject(): obtém o serviço do carrinho (estado e lógica centralizados)
  readonly srv = inject(CarrinhoService);

  // Getters que leem os Signals do serviço (chamam o Signal como função)
  get itens()    { return this.srv.itens(); }     // Lista de produtos no carrinho
  get subtotal() { return this.srv.subtotal(); }  // Soma dos preços
  get desconto() { return this.srv.desconto(); }  // Valor do desconto do cupom
  get total()    { return this.srv.total(); }     // subtotal - desconto

  // Métodos que delegam as ações ao serviço
  aumentarQtd(id: number, tamanho?: string) { this.srv.aumentarQtd(id, tamanho); }
  diminuirQtd(id: number, tamanho?: string) { this.srv.diminuirQtd(id, tamanho); }
  removerItem(id: number, tamanho?: string) { this.srv.removerItem(id, tamanho); }
}
