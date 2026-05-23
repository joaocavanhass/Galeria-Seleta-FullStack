import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CarrinhoService } from '../core/services/carrinho.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  termoBusca = '';
  readonly carrinho = inject(CarrinhoService);
  get totalItens() { return this.carrinho.totalItens(); }
}