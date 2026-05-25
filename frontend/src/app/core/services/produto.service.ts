import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Produto } from '../models/produto.model';
import { ImagemProduto } from '../models/imagem-produto.model';

interface ApiCategoria {
  id: number;
  nome: string;
  nomeUrl: string;
  ativo: boolean;
}

interface ApiFoto {
  id: number;
  url: string;
  principal: boolean;
  ordem: number;
}

export interface ApiProduto {
  id: number;
  nome: string;
  descricao: string | null;
  preco: number;
  status: string;
  novidade: boolean;
  criadoEm: string;
  categoria: ApiCategoria;
  fotos: ApiFoto[];
}

export interface ProdutoRequest {
  nome: string;
  descricao?: string;
  preco: number;
  status: string;
  novidade: boolean;
  categoriaId: number;
}

function mapProduto(api: ApiProduto): Produto {
  const imagemPrincipal = api.fotos?.find(f => f.principal)?.url ?? api.fotos?.[0]?.url;
  const imagens: ImagemProduto[] = (api.fotos ?? []).map(f => ({
    id: f.id,
    produto_id: api.id,
    url: f.url,
    principal: f.principal,
    ordem: f.ordem
  }));

  const statusFront: 'ativo' | 'inativo' | 'rascunho' =
    api.status === 'disponivel' ? 'ativo' :
    api.status === 'indisponivel' ? 'inativo' : 'rascunho';

  return {
    id: api.id,
    nome: api.nome,
    descricao: api.descricao,
    preco: api.preco,
    preco_desconto: null,
    estoque: 0,
    status: statusFront,
    criado_em: api.criadoEm,
    categoria_id: api.categoria?.id ?? 0,
    categoria: api.categoria?.nome ?? '',
    imagens,
    imagem_url: imagemPrincipal
  };
}

@Injectable({ providedIn: 'root' })
export class ProdutoService {
  private readonly base = `${environment.apiUrl}/produtos`;

  constructor(private http: HttpClient) {}

  listar(params?: { categoriaId?: number; ordenacao?: string; status?: string }): Observable<Produto[]> {
    let httpParams = new HttpParams();
    if (params?.categoriaId) httpParams = httpParams.set('categoriaId', params.categoriaId);
    if (params?.ordenacao)   httpParams = httpParams.set('ordenacao',   params.ordenacao);
    if (params?.status)      httpParams = httpParams.set('status',      params.status);
    return this.http.get<ApiProduto[]>(this.base, { params: httpParams }).pipe(
      map(lista => lista.map(mapProduto))
    );
  }

  novidades(): Observable<Produto[]> {
    return this.http.get<ApiProduto[]>(`${this.base}/novidades`).pipe(
      map(lista => lista.map(mapProduto))
    );
  }

  buscar(termo: string): Observable<Produto[]> {
    return this.http.get<ApiProduto[]>(`${this.base}/busca`, {
      params: new HttpParams().set('termo', termo)
    }).pipe(map(lista => lista.map(mapProduto)));
  }

  buscarPorId(id: number): Observable<Produto> {
    return this.http.get<ApiProduto>(`${this.base}/${id}`).pipe(map(mapProduto));
  }

  criar(dados: ProdutoRequest): Observable<ApiProduto> {
    return this.http.post<ApiProduto>(this.base, dados);
  }

  atualizar(id: number, dados: ProdutoRequest): Observable<ApiProduto> {
    return this.http.put<ApiProduto>(`${this.base}/${id}`, dados);
  }

  atualizarStatus(id: number, status: string): Observable<void> {
    return this.http.patch<void>(`${this.base}/${id}/status`, { status });
  }

  adicionarFoto(produtoId: number, url: string): Observable<ApiFoto> {
    return this.http.post<ApiFoto>(`${this.base}/${produtoId}/fotos`, { url });
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
