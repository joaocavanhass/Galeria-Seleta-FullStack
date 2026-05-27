// ============================================================
// ARQUIVO: produto.service.ts
// FUNÇÃO: Serviço que gerencia todas as operações de produtos com a API.
//
// MAPEAMENTO DE DADOS (mapProduto):
// O backend retorna produtos em formato ApiProduto (com fotos, categoria aninhada, etc.)
// O frontend usa a interface Produto (com campos em snake_case e estrutura simplificada).
// A função mapProduto() faz a conversão entre os dois formatos.
//
// LÓGICA DE IMAGEM PRINCIPAL:
// api.fotos?.find(f => f.principal)?.url: busca a foto marcada como principal.
// ?? api.fotos?.[0]?.url: se não tiver foto principal, usa a primeira foto.
// O "?." é o "optional chaining" — não lança erro se o valor for null/undefined.
//
// MAPEAMENTO DE STATUS:
// Backend usa: "disponivel" / "indisponivel"
// Frontend usa: "ativo" / "inativo" / "rascunho"
// Isso permite que o frontend tenha um estado extra sem alterar o backend.
//
// OPERAÇÕES DISPONÍVEIS:
// - listar(): com filtros por categoria, ordenação e status
// - novidades(): produtos marcados como novidade
// - buscar(): busca por texto no nome
// - buscarPorId(): detalhes de um produto
// - criar(), atualizar(), atualizarStatus(), adicionarFoto(), deletar(): CRUD (admin)
// ============================================================

import { Injectable } from '@angular/core';
// HttpClient: requisições HTTP | HttpParams: constrói query string (?categoriaId=...)
import { HttpClient, HttpParams } from '@angular/common/http';
// Observable: valor assíncrono | map: transforma o valor emitido
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Produto } from '../models/produto.model';
import { ImagemProduto } from '../models/imagem-produto.model';

// Interfaces internas que representam o formato da API (não exportadas — uso local)
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

// Formato completo de produto retornado pelo backend
export interface ApiProduto {
  id: number;
  nome: string;
  descricao: string | null;
  preco: number;
  status: string;           // "ativo" ou "inativo" no backend
  novidade: boolean;
  criadoEm: string;
  categoria: ApiCategoria;  // Objeto aninhado com dados da categoria
  fotos: ApiFoto[];         // Array de fotos do produto
}

// Dados enviados ao criar ou atualizar um produto (admin)
export interface ProdutoRequest {
  nome: string;
  descricao?: string;
  preco: number;
  status: string;
  novidade: boolean;
  categoriaId: number;
}

// -------------------------------------------------------
// mapProduto(): converte o formato da API para o formato do frontend
// Chamada em cada produto recebido via API.
//
// Optional chaining (?.): api.fotos?.find() não lança erro se fotos for null.
// Nullish coalescing (??): usa o valor da direita se o da esquerda for null/undefined.
// -------------------------------------------------------
function mapProduto(api: ApiProduto): Produto {
  // Busca a URL da imagem principal (ou a primeira foto como fallback)
  const imagemPrincipal = api.fotos?.find(f => f.principal)?.url ?? api.fotos?.[0]?.url;

  // Converte cada foto para o modelo ImagemProduto do frontend
  const imagens: ImagemProduto[] = (api.fotos ?? []).map(f => ({
    id: f.id,
    produto_id: api.id,
    url: f.url,
    principal: f.principal,
    ordem: f.ordem
  }));

  // Traduz o status do backend para o formato do frontend
  const statusFront: 'ativo' | 'inativo' | 'rascunho' =
    api.status === 'disponivel'   ? 'ativo'    : // backend "disponivel" → frontend "ativo"
    api.status === 'indisponivel' ? 'inativo'  : // backend "indisponivel" → frontend "inativo"
    'rascunho';                                   // Qualquer outro valor → "rascunho"

  return {
    id: api.id,
    nome: api.nome,
    descricao: api.descricao,
    preco: api.preco,
    preco_desconto: null,           // Backend não tem este campo, usa null
    estoque: 0,                     // Backend não retorna estoque aqui, usa 0
    status: statusFront,
    criado_em: api.criadoEm,        // camelCase → snake_case
    categoria_id: api.categoria?.id ?? 0,   // Usa 0 se categoria for null
    categoria: api.categoria?.nome ?? '',   // Usa '' se categoria for null
    imagens,
    imagem_url: imagemPrincipal     // URL da imagem calculada acima
  };
}

// @Injectable({ providedIn: 'root' }): Singleton para toda a aplicação
@Injectable({ providedIn: 'root' })
export class ProdutoService {

  // URL base das rotas de produtos
  private readonly base = `${environment.apiUrl}/produtos`;

  constructor(private http: HttpClient) {}

  // -------------------------------------------------------
  // listar(): busca produtos com filtros opcionais
  //
  // HttpParams: constrói a query string da URL.
  //   Ex: listar({ categoriaId: 2, ordenacao: 'menor-preco' })
  //   → GET /api/produtos?categoriaId=2&ordenacao=menor-preco
  //
  // httpParams.set('campo', valor): adiciona um parâmetro (retorna nova instância)
  // -------------------------------------------------------
  listar(params?: { categoriaId?: number; ordenacao?: string; status?: string }): Observable<Produto[]> {
    let httpParams = new HttpParams();
    if (params?.categoriaId) httpParams = httpParams.set('categoriaId', params.categoriaId);
    if (params?.ordenacao)   httpParams = httpParams.set('ordenacao',   params.ordenacao);
    if (params?.status)      httpParams = httpParams.set('status',      params.status);

    return this.http.get<ApiProduto[]>(this.base, { params: httpParams }).pipe(
      map(lista => lista.map(mapProduto)) // Converte cada ApiProduto → Produto
    );
  }

  // Busca produtos marcados como novidade (campo novidade = true no backend)
  novidades(): Observable<Produto[]> {
    return this.http.get<ApiProduto[]>(`${this.base}/novidades`).pipe(
      map(lista => lista.map(mapProduto))
    );
  }

  // Busca produtos cujo nome contenha o termo informado
  buscar(termo: string): Observable<Produto[]> {
    return this.http.get<ApiProduto[]>(`${this.base}/busca`, {
      params: new HttpParams().set('termo', termo) // GET /api/produtos/busca?termo=...
    }).pipe(map(lista => lista.map(mapProduto)));
  }

  // Busca os detalhes completos de um produto pelo ID
  buscarPorId(id: number): Observable<Produto> {
    return this.http.get<ApiProduto>(`${this.base}/${id}`).pipe(map(mapProduto));
  }

  // Cria um novo produto (somente admin)
  criar(dados: ProdutoRequest): Observable<ApiProduto> {
    return this.http.post<ApiProduto>(this.base, dados); // POST /api/produtos
  }

  // Atualiza todos os campos de um produto existente (somente admin)
  atualizar(id: number, dados: ProdutoRequest): Observable<ApiProduto> {
    return this.http.put<ApiProduto>(`${this.base}/${id}`, dados); // PUT /api/produtos/:id
  }

  // Ativa ou desativa um produto sem alterar outros dados (somente admin)
  atualizarStatus(id: number, status: string): Observable<void> {
    return this.http.patch<void>(`${this.base}/${id}/status`, { status }); // PATCH /api/produtos/:id/status
  }

  // Define a imagem principal de um produto enviando a URL (somente admin)
  adicionarFoto(produtoId: number, url: string): Observable<ApiFoto> {
    return this.http.post<ApiFoto>(`${this.base}/${produtoId}/fotos`, { url }); // POST /api/produtos/:id/fotos
  }

  // Remove permanentemente um produto e suas fotos (somente admin)
  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`); // DELETE /api/produtos/:id
  }
}
