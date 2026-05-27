// ============================================================
// ARQUIVO: footer.component.ts
// FUNÇÃO: Componente do rodapé da aplicação.
//
// Exibe o rodapé em todas as páginas do site (está no AppComponent).
// Contém o formulário de contato do rodapé — os dados são coletados
// no objeto "form" e o método enviarFormulario() os processa.
//
// NOTA: O enviarFormulario() atualmente apenas faz console.log()
// dos dados. Em produção, deveria chamar um service para enviar
// os dados ao backend (POST /api/contato).
//
// standalone: true → importa FormsModule e RouterModule diretamente,
// sem precisar de um NgModule.
// ============================================================

// Component: decorador de componente Angular
import { Component } from '@angular/core';
// FormsModule: habilita o binding de formulários com [(ngModel)]
import { FormsModule } from '@angular/forms';
// RouterModule: habilita links de navegação (<a routerLink="...">) no template
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-footer',  // Tag <app-footer> no app.component.html
  standalone: true,
  imports: [FormsModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {

  // Objeto "form" ligado ao formulário de contato do rodapé via [(ngModel)]
  // [(ngModel)]: two-way binding — TypeScript ↔ HTML (sincronizado em tempo real)
  form = {
    nome: '',       // Campo "Nome" do formulário
    sobrenome: '',  // Campo "Sobrenome"
    email: '',      // Campo "Email"
    telefone: '',   // Campo "Telefone"
    mensagem: ''    // Campo "Mensagem"
  };

  // Chamado quando o formulário é submetido
  // TODO: substituir console.log por chamada ao ContatoService
  enviarFormulario() {
    console.log(this.form); // Temporário — exibe os dados no console do navegador
  }
}
