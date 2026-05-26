import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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

  readonly srv = inject(CarrinhoService);

  cupomInput    = '';
  cupomErro     = false;
  cupomOk       = false;
  cupomCarregando = false;

  get itens()    { return this.srv.itens(); }
  get subtotal() { return this.srv.subtotal(); }
  get desconto() { return this.srv.desconto(); }
  get total()    { return this.srv.total(); }

  aumentarQtd(id: number, tamanho?: string) { this.srv.aumentarQtd(id, tamanho); }
  diminuirQtd(id: number, tamanho?: string) { this.srv.diminuirQtd(id, tamanho); }
  removerItem(id: number, tamanho?: string) { this.srv.removerItem(id, tamanho); }

  async aplicarCupom() {
    if (!this.cupomInput.trim()) return;
    this.cupomCarregando = true;
    this.cupomErro       = false;
    this.cupomOk         = false;

    const ok = await this.srv.aplicarCupomApi(this.cupomInput);
    this.cupomOk         = ok;
    this.cupomErro       = !ok;
    this.cupomCarregando = false;
  }
}