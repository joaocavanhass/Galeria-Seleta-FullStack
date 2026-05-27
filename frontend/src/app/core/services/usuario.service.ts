// ============================================================
// ARQUIVO: usuario.service.ts
// FUNÇÃO: Serviço que gerencia operações de perfil e endereços do usuário logado.
//
// RESPONSABILIDADES:
// - Listar todos os usuários (admin)
// - Atualizar papel de um usuário (admin)
// - Buscar, atualizar e deletar o próprio perfil (/me)
// - Alterar a própria senha
// - Gerenciar endereços do usuário logado
//
// CONVENÇÃO /me:
// As rotas com "/me" representam o usuário autenticado.
// O backend identifica o usuário pelo JWT, não pelo ID na URL.
//
// PAGINAÇÃO:
// listarTodos() recebe PageResponse do backend mas extrai só o array
// "content" usando map(res => res.content) para simplificar.
// ============================================================

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

// Interface interna para a resposta paginada (usada por listarTodos)
interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

// Formato do endereço retornado pela API
export interface EnderecoApi {
  id: number;
  rua: string;
  cidade: string;
  estado: string;
  cep: string;
  principal: boolean; // true = endereço padrão do usuário
}

// Formato do perfil do usuário retornado pela API
export interface PerfilApi {
  id: number;
  nome: string;
  email: string;
  telefone: string | null;
  cpf: string | null;
  papel: string;      // "cliente" ou "admin"
  criadoEm: string;
}

// Dados necessários para cadastrar um novo endereço
export interface EnderecoRequest {
  rua: string;
  cidade: string;
  estado: string;
  cep: string;
  principal?: boolean; // Opcional — se true, define como endereço padrão
}

// @Injectable({ providedIn: 'root' }): Singleton para toda a aplicação
@Injectable({ providedIn: 'root' })
export class UsuarioService {

  // URL base das rotas de usuários
  private readonly base = `${environment.apiUrl}/usuarios`;

  // HttpClient injetado via construtor
  constructor(private http: HttpClient) {}

  // -------------------------------------------------------
  // listarTodos(): busca todos os usuários (somente admin)
  //
  // Extrai apenas o array "content" da resposta paginada usando map.
  // O componente recebe uma lista simples sem precisar lidar com paginação.
  // -------------------------------------------------------
  listarTodos(): Observable<PerfilApi[]> {
    return this.http.get<PageResponse<PerfilApi>>(this.base).pipe(
      map(res => res.content) // Extrai apenas a lista de usuários da resposta paginada
    );
  }

  // Altera o papel de um usuário ("cliente" ↔ "admin") — somente admin
  atualizarPapel(id: number, papel: string): Observable<PerfilApi> {
    return this.http.patch<PerfilApi>(`${this.base}/${id}/papel`, { papel }); // PATCH /api/usuarios/:id/papel
  }

  // Busca os dados do perfil do usuário logado
  perfil(): Observable<PerfilApi> {
    return this.http.get<PerfilApi>(`${this.base}/me`); // GET /api/usuarios/me
  }

  // Atualiza os dados do perfil do usuário logado (nome e/ou telefone)
  atualizar(dados: { nome?: string; telefone?: string }): Observable<PerfilApi> {
    return this.http.put<PerfilApi>(`${this.base}/me`, dados); // PUT /api/usuarios/me
  }

  // -------------------------------------------------------
  // alterarSenha(): troca a senha do usuário logado
  //
  // O backend exige a senha atual para confirmar a identidade.
  // Se senhaAtual estiver errada, retorna 401 Unauthorized.
  // -------------------------------------------------------
  alterarSenha(senhaAtual: string, novaSenha: string): Observable<void> {
    return this.http.patch<void>(`${this.base}/me/senha`, { senhaAtual, novaSenha }); // PATCH /api/usuarios/me/senha
  }

  // Retorna todos os endereços cadastrados pelo usuário logado
  listarEnderecos(): Observable<EnderecoApi[]> {
    return this.http.get<EnderecoApi[]>(`${this.base}/me/enderecos`); // GET /api/usuarios/me/enderecos
  }

  // Adiciona um novo endereço ao perfil do usuário logado
  adicionarEndereco(dados: EnderecoRequest): Observable<EnderecoApi> {
    return this.http.post<EnderecoApi>(`${this.base}/me/enderecos`, dados); // POST /api/usuarios/me/enderecos
  }

  // Remove um endereço pelo seu ID
  removerEndereco(enderecoId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/me/enderecos/${enderecoId}`); // DELETE /api/usuarios/me/enderecos/:id
  }
}
