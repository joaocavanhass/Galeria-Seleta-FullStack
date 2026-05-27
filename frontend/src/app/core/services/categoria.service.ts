// ============================================================
// ARQUIVO: categoria.service.ts
// FUNÇÃO: Serviço que gerencia todas as operações de categorias com a API.
//
// CONCEITO IMPORTANTE — MAPEAMENTO DE DADOS (mapCategoria):
// O backend retorna campos com nomes diferentes dos usados no frontend.
//   Backend: { id, nome, nomeUrl, ativo }
//   Frontend (modelo Categoria): { id, cat_pai_id, nome, slug, descricao, ativo }
//
// A função mapCategoria() faz a tradução entre os dois formatos.
// Isso permite que o backend mude nomes de campos sem quebrar o frontend,
// e vice-versa — eles ficam desacoplados.
//
// ApiCategoria (interface local):
// Representa os dados no formato que a API envia. É usada apenas neste
// arquivo — por isso não é exportada.
//
// OPERAÇÕES CRUD:
// - listar(): GET    /api/categorias
// - buscarPorId(): GET /api/categorias/:id
// - criar(): POST    /api/categorias
// - atualizar(): PUT /api/categorias/:id
// - deletar(): DELETE /api/categorias/:id
// ============================================================

// Injectable: marca como serviço injetável
import { Injectable } from '@angular/core';
// HttpClient: faz requisições HTTP
import { HttpClient } from '@angular/common/http';
// Observable: valor assíncrono | map: transforma o valor emitido pelo Observable
import { Observable, map } from 'rxjs';
// URL base da API
import { environment } from '../../../environments/environment';
// Modelo Categoria usado no frontend
import { Categoria } from '../models/categoria.model';

// Interface interna que representa o formato da API (não exportada — uso local apenas)
interface ApiCategoria {
  id: number;
  nome: string;
  nomeUrl: string; // Nome da URL (slug) — no frontend chamamos de "slug"
  ativo: boolean;
}

// -------------------------------------------------------
// mapCategoria(): função que converte ApiCategoria → Categoria
// Isola a lógica de mapeamento fora da classe para manter o código limpo.
// -------------------------------------------------------
function mapCategoria(api: ApiCategoria): Categoria {
  return {
    id: api.id,
    cat_pai_id: null,    // Backend não retorna hierarquia nesta listagem
    nome: api.nome,
    slug: api.nomeUrl,   // Renomeia: nomeUrl (backend) → slug (frontend)
    descricao: null,     // Campo não retornado pela API, definido como null
    ativo: api.ativo
  };
}

// @Injectable({ providedIn: 'root' }): instância única para toda a aplicação
@Injectable({ providedIn: 'root' })
export class CategoriaService {

  // URL base das rotas de categorias
  private readonly base = `${environment.apiUrl}/categorias`;

  // HttpClient injetado via construtor
  constructor(private http: HttpClient) {}

  // -------------------------------------------------------
  // listar(): busca todas as categorias
  //
  // .pipe(map(lista => lista.map(mapCategoria))):
  //   - pipe(): encadeia operadores no Observable
  //   - map(lista => ...): transforma o valor quando a resposta chegar
  //   - lista.map(mapCategoria): converte cada ApiCategoria → Categoria
  // -------------------------------------------------------
  listar(): Observable<Categoria[]> {
    return this.http.get<ApiCategoria[]>(this.base).pipe(
      map(lista => lista.map(mapCategoria)) // Converte o array inteiro usando a função de mapeamento
    );
  }

  // Busca uma categoria específica pelo ID e a mapeia para o formato do frontend
  buscarPorId(id: number): Observable<Categoria> {
    return this.http.get<ApiCategoria>(`${this.base}/${id}`).pipe(map(mapCategoria));
  }

  // -------------------------------------------------------
  // criar(): cadastra uma nova categoria no backend
  // Retorna ApiCategoria (formato do backend) — usado pelo painel admin
  // -------------------------------------------------------
  criar(dados: { nome: string; nomeUrl: string; categoriaMaeId?: number; ativo?: boolean }): Observable<ApiCategoria> {
    return this.http.post<ApiCategoria>(this.base, dados); // POST /api/categorias
  }

  // Atualiza os dados de uma categoria existente
  atualizar(id: number, dados: { nome: string; nomeUrl: string; ativo?: boolean }): Observable<ApiCategoria> {
    return this.http.put<ApiCategoria>(`${this.base}/${id}`, dados); // PUT /api/categorias/:id
  }

  // Remove permanentemente uma categoria pelo ID
  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`); // DELETE /api/categorias/:id
  }
}
