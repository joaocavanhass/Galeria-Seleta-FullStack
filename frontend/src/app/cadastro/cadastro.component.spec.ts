// ============================================================
// ARQUIVO: cadastro.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do CadastroComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático gerados pelo Angular CLI.
// Cada .spec.ts verifica se o componente correspondente funciona corretamente.
//
// COMO EXECUTAR: ng test
//
// ESTRUTURA:
// - describe(): agrupa os testes do CadastroComponent
// - beforeEach(): configura o ambiente (cria o componente) antes de cada teste
// - it('deve criar', () => { ... }): verifica se o componente é criado sem erros
// - expect(component).toBeTruthy(): garante que component não é null/undefined
// ============================================================

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CadastroComponent } from './cadastro.component';

describe('CadastroComponent', () => {
  let component: CadastroComponent;
  let fixture: ComponentFixture<CadastroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CadastroComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CadastroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Teste básico: verifica se o componente é criado sem erros de inicialização
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
