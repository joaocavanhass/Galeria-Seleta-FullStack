// ============================================================
// ARQUIVO: esqueci-senha.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do EsqueciSenhaComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático gerados pelo Angular CLI.
// Permitem verificar automaticamente se o componente funciona corretamente.
//
// COMO EXECUTAR: ng test
//
// NOTA: O EsqueciSenhaComponent usa @ViewChildren (para controlar inputs de código)
// e setInterval (timer de reenvio). Em testes completos, o Timer seria controlado
// com fakeAsync() e tick() para simular a passagem do tempo sem esperar de verdade.
//
// ESTRUTURA:
// - describe(): agrupa os testes do EsqueciSenhaComponent
// - beforeEach(): prepara o ambiente antes de cada teste
// - it(): define um teste individual
// ============================================================

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EsqueciSenhaComponent } from './esqueci-senha.component';

describe('EsqueciSenhaComponent', () => {
  let component: EsqueciSenhaComponent;
  let fixture: ComponentFixture<EsqueciSenhaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EsqueciSenhaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EsqueciSenhaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Teste básico: verifica se o componente é criado sem erros de inicialização
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
