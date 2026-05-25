import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Categoria } from '../models/categoria.model';

interface ApiCategoria {
  id: number;
  nome: string;
  nomeUrl: string;
  ativo: boolean;
}

function mapCategoria(api: ApiCategoria): Categoria {
  return {
    id: api.id,
    cat_pai_id: null,
    nome: api.nome,
    slug: api.nomeUrl,
    descricao: null,
    ativo: api.ativo
  };
}

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private readonly base = `${environment.apiUrl}/categorias`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Categoria[]> {
    return this.http.get<ApiCategoria[]>(this.base).pipe(
      map(lista => lista.map(mapCategoria))
    );
  }

  buscarPorId(id: number): Observable<Categoria> {
    return this.http.get<ApiCategoria>(`${this.base}/${id}`).pipe(map(mapCategoria));
  }

  criar(dados: { nome: string; nomeUrl: string; categoriaMaeId?: number; ativo?: boolean }): Observable<ApiCategoria> {
    return this.http.post<ApiCategoria>(this.base, dados);
  }

  atualizar(id: number, dados: { nome: string; nomeUrl: string; ativo?: boolean }): Observable<ApiCategoria> {
    return this.http.put<ApiCategoria>(`${this.base}/${id}`, dados);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
