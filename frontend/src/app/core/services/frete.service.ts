// ============================================================
// ARQUIVO: frete.service.ts
// FUNÇÃO: Serviço para buscar as opções de frete disponíveis na API.
//
// RESPONSABILIDADE:
// Faz uma única chamada GET para /api/frete e retorna a lista
// de opções de entrega (PAC, SEDEX, etc.) com prazo e preço.
// Essas opções são exibidas no checkout para o usuário escolher.
//
// PADRÃO Observable:
// O método retorna Observable<FreteApi[]>. Isso significa que o
// componente que chamar listar() precisa fazer .subscribe() para
// receber os dados quando a resposta da API chegar.
// ============================================================

// Injectable: marca como serviço injetável
import { Injectable } from '@angular/core';
// HttpClient: realiza as requisições HTTP
import { HttpClient } from '@angular/common/http';
// Observable: representa um valor assíncrono (chegará depois)
import { Observable } from 'rxjs';
// environment: contém a URL base da API
import { environment } from '../../../environments/environment';

// Interface que representa o formato de resposta da API para uma opção de frete
export interface FreteApi {
  id: number;          // Identificador único da opção de frete
  nome: string;        // Nome da modalidade (ex: "PAC", "SEDEX")
  prazoMinimo: number; // Prazo mínimo de entrega em dias úteis
  prazoMaximo: number; // Prazo máximo de entrega em dias úteis
  preco: number;       // Valor do frete em reais
}

// @Injectable({ providedIn: 'root' }): serviço Singleton (uma instância para toda a aplicação)
@Injectable({ providedIn: 'root' })
export class FreteService {

  // URL base das rotas de frete (ex: http://localhost:8080/api/frete)
  private readonly base = `${environment.apiUrl}/frete`;

  // Construtor: o Angular injeta o HttpClient automaticamente
  constructor(private http: HttpClient) {}

  // -------------------------------------------------------
  // listar(): busca todas as opções de frete disponíveis
  // Retorna Observable<FreteApi[]> — a lista chegará de forma assíncrona
  // -------------------------------------------------------
  listar(): Observable<FreteApi[]> {
    return this.http.get<FreteApi[]>(this.base); // GET /api/frete
  }
}
