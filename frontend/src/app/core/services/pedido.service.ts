// ============================================================
// ARQUIVO: pedido.service.ts
// FUNÇÃO: Serviço que gerencia todas as operações de pedidos com a API.
//
// RESPONSABILIDADES:
// - Listar pedidos do usuário (com filtro por status)
// - Buscar detalhes de um pedido específico
// - Criar novo pedido (finalização do checkout)
// - Cancelar pedido
// - Atualizar status (uso administrativo)
//
// PAGINAÇÃO:
// O backend retorna pedidos paginados (PaginatedResponse), mas
// o método listar() extrai apenas o array "content" para simplificar
// o uso nos componentes. A verificação Array.isArray() trata ambos
// os formatos (lista simples ou paginada) por compatibilidade.
//
// INTERFACES EXPORTADAS:
// ItemPedidoApi e PedidoApi são exportadas para serem usadas
// nos componentes que exibem os pedidos.
// ============================================================

import { Injectable } from '@angular/core';
// HttpClient: requisições HTTP | HttpParams: constrói query string (?status=...)
import { HttpClient, HttpParams } from '@angular/common/http';
// Observable: valor assíncrono | map: transforma o valor do Observable
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

// Interface interna para a resposta paginada do backend
interface PaginatedResponse<T> {
  content: T[];         // Os dados desta página
  page: number;         // Número da página atual
  size: number;         // Quantidade por página
  totalElements: number; // Total de registros no banco
  totalPages: number;    // Total de páginas
}

// Item de um pedido — contém o produto, quantidade e preço pago no momento da compra
export interface ItemPedidoApi {
  id: number;
  produto?: { id: number; nome: string; preco: number } | null; // Produto pode ser nulo (deletado)
  quantidade: number;
  precoPago: number; // Preço no momento da compra (não muda se o produto mudar de preço)
}

// Pedido completo retornado pela API
export interface PedidoApi {
  id: number;
  status: string;        // Status atual (ex: "pendente", "CONFIRMADO", "ENTREGUE")
  subtotal: number;      // Soma dos preços dos produtos
  valorFrete: number;    // Custo do frete escolhido
  desconto: number;      // Desconto do cupom aplicado
  total: number;         // Valor final (subtotal + frete - desconto)
  criadoEm: string;      // Data de criação
  usuario?: { id: number; nome: string; email: string } | null;
  endereco?: { id: number; rua: string; cidade: string; estado: string; cep: string } | null;
  itens: ItemPedidoApi[];
  cupom?: { id: number; codigo: string };
  frete?: { id: number; nome: string; preco: number };
}

// Dados necessários para criar um novo pedido
export interface CriarPedidoRequest {
  usuarioId: number;        // ID do usuário que fez o pedido
  enderecoId: number;       // ID do endereço de entrega escolhido
  produtoIds: number[];     // Lista de IDs dos produtos (um ID por unidade)
  freteId?: number;         // ID da opção de frete escolhida (opcional)
  codigoCupom?: string;     // Código do cupom (opcional)
}

@Injectable({ providedIn: 'root' })
export class PedidoService {

  // URL base das rotas de pedidos
  private readonly base = `${environment.apiUrl}/pedidos`;

  constructor(private http: HttpClient) {}

  // -------------------------------------------------------
  // listar(): busca os pedidos do usuário logado
  //
  // HttpParams: constrói os parâmetros de query da URL (?status=...)
  // .pipe(map(...)): transforma a resposta antes de entregar ao componente
  //
  // Array.isArray(res): verifica se a resposta é um array simples ou paginada.
  // Se for array: retorna direto. Se for paginado: extrai o array "content".
  // -------------------------------------------------------
  listar(status?: string): Observable<PedidoApi[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status); // Adiciona ?status=... se informado

    return this.http.get<PaginatedResponse<PedidoApi> | PedidoApi[]>(this.base, { params }).pipe(
      map(res => Array.isArray(res) ? res : res.content) // Normaliza: extrai só o array
    );
  }

  // Busca um pedido específico com todos os seus detalhes (itens, endereço, etc.)
  buscarPorId(id: number): Observable<PedidoApi> {
    return this.http.get<PedidoApi>(`${this.base}/${id}`); // GET /api/pedidos/:id
  }

  // Cria um novo pedido (finalização do checkout)
  criar(dados: CriarPedidoRequest): Observable<PedidoApi> {
    return this.http.post<PedidoApi>(this.base, dados); // POST /api/pedidos
  }

  // Cancela um pedido (muda status para "cancelado")
  cancelar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.base}/${id}/cancelar`, {}); // PATCH /api/pedidos/:id/cancelar
  }

  // Atualiza o status de um pedido — usado pelo painel administrativo
  atualizarStatus(id: number, status: string): Observable<void> {
    return this.http.patch<void>(`${this.base}/${id}/status`, { status }); // PATCH /api/pedidos/:id/status
  }
}
