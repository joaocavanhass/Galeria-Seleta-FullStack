import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  termoBusca = '';
  produtosAberto = false;

  toggleProdutos(): void {
    this.produtosAberto = !this.produtosAberto;
  }

  fecharDropdown(): void {
    this.produtosAberto = false;
  }
}