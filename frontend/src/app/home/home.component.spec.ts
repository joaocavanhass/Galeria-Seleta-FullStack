// ============================================================
// ARQUIVO: home.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do HomeComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático criados pelo Angular CLI.
// Cada arquivo .spec.ts testa o componente de mesmo nome.
// Eles verificam automaticamente se o componente funciona corretamente.
//
// COMO EXECUTAR: ng test (abre o Karma e roda todos os testes)
//
// ESTRUTURA:
// - describe(): agrupa os testes de um componente
// - beforeEach(): prepara o ambiente antes de cada teste
// - it('deve fazer X', () => { ... }): um teste individual
// - expect(valor).toBeTruthy(): verifica que o valor é verdadeiro (não nulo/falso)
//
// ComponentFixture<T>: wrapper do ambiente de teste que dá acesso ao componente
//   e ao DOM renderizado para verificações.
// ============================================================

// ComponentFixture: wrapper do ambiente de teste para interagir com o componente
// TestBed: configura o ambiente de teste Angular (como um mini-módulo Angular)
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeComponent } from './home.component';

describe('HomeComponent', () => {
  let component: HomeComponent;                   // A instância TypeScript do componente
  let fixture: ComponentFixture<HomeComponent>;   // O wrapper do ambiente de teste

  // beforeEach(): executa antes de CADA teste, garantindo um ambiente limpo
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent] // Standalone component: importado diretamente
    })
    .compileComponents(); // compileComponents(): compila o template HTML do componente

    // Cria uma instância do HomeComponent no ambiente de teste
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // Dispara o ngOnInit e aplica o data binding inicial
  });

  // Teste básico: verifica se o componente é criado sem erros
  it('should create', () => {
    expect(component).toBeTruthy(); // toBeTruthy(): garante que o componente não é null
  });
});
