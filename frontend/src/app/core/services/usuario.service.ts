import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface EnderecoApi {
  id: number;
  rua: string;
  cidade: string;
  estado: string;
  cep: string;
  principal: boolean;
}

export interface PerfilApi {
  id: number;
  nome: string;
  email: string;
  telefone: string | null;
  cpf: string | null;
  papel: string;
  criadoEm: string;
}

export interface EnderecoRequest {
  rua: string;
  cidade: string;
  estado: string;
  cep: string;
  principal?: boolean;
}

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly base = `${environment.apiUrl}/usuarios`;

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<PerfilApi[]> {
    return this.http.get<PageResponse<PerfilApi>>(this.base).pipe(
      map(res => res.content)
    );
  }

  atualizarPapel(id: number, papel: string): Observable<PerfilApi> {
    return this.http.patch<PerfilApi>(`${this.base}/${id}/papel`, { papel });
  }

  perfil(): Observable<PerfilApi> {
    return this.http.get<PerfilApi>(`${this.base}/me`);
  }

  atualizar(dados: { nome?: string; telefone?: string }): Observable<PerfilApi> {
    return this.http.put<PerfilApi>(`${this.base}/me`, dados);
  }

  alterarSenha(senhaAtual: string, novaSenha: string): Observable<void> {
    return this.http.patch<void>(`${this.base}/me/senha`, { senhaAtual, novaSenha });
  }

  listarEnderecos(): Observable<EnderecoApi[]> {
    return this.http.get<EnderecoApi[]>(`${this.base}/me/enderecos`);
  }

  adicionarEndereco(dados: EnderecoRequest): Observable<EnderecoApi> {
    return this.http.post<EnderecoApi>(`${this.base}/me/enderecos`, dados);
  }

  removerEndereco(enderecoId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/me/enderecos/${enderecoId}`);
  }
}
