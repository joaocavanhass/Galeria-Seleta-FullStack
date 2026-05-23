import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true, 
  imports: [FormsModule, RouterModule], 
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {
  form = {
    nome: '',
    sobrenome: '',
    email: '',
    telefone: '',
    mensagem: ''
  };

  enviarFormulario() {
    console.log(this.form);
  }
}