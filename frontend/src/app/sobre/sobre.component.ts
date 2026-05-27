// ============================================================
// ARQUIVO: sobre.component.ts
// FUNÇÃO: Componente da página "Sobre nós" (/sobre).
//
// Componente puramente estático — todo o conteúdo está no HTML
// (sobre.component.html) e CSS (sobre.component.css).
// Não tem lógica TypeScript, pois não precisa buscar dados da API
// nem gerenciar estado — apenas exibe texto e imagens fixas.
//
// NOTA: Não é standalone porque foi criado antes da adoção
// do padrão standalone neste projeto (sem standalone: true).
// ============================================================

// Component: decorador que transforma a classe em um componente Angular
import { Component } from '@angular/core';

@Component({
  selector: 'app-sobre',           // Tag HTML usada para inserir este componente
  templateUrl: './sobre.component.html',  // Arquivo com o conteúdo HTML da página
  styleUrls: ['./sobre.component.css']    // Arquivo com os estilos da página
})
// Classe vazia — sem lógica, pois a página é estática
export class SobreComponent {}
