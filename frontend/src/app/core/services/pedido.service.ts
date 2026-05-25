import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ItemPedidoApi {
  id: number;
  produto: { id: number; nome: string; preco: number };
  quantidade: number;
  precoPago: number;
}

export interface PedidoApi {
  id: number;
  status: string;
  subtotal: number;
  valorFrete: number;
  desconto: number;
  total: number;
  criadoEm: string;
  usuario: { id: number; nome: string; email: string };
  endereco: { id: number; rua: string; cidade: string; estado: string; cep: string };
  itens: ItemPedidoApi[];
  cupom?: { id: number; codigo: string };
  frete?: { id: number; nome: string; preco: number };
}

export interface CriarPedidoRequest {
  usuarioId: number;
  enderecoId: number;
  produtoIds: number[];
  freteId?: number;
  codigoCupom?: string;
}

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private readonly base = `${environment.apiUrl}/pedidos`;

  constructor(private http: HttpClient) {}

  listar(status?: string): Observable<PedidoApi[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<PedidoApi[]>(this.base, { params });
  }

  buscarPorId(id: number): Observable<PedidoApi> {
    return this.http.get<PedidoApi>(`${this.base}/${id}`);
  }

  criar(dados: CriarPedidoRequest): Observable<PedidoApi> {
    return this.http.post<PedidoApi>(this.base, dados);
  }

  cancelar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.base}/${id}/cancelar`, {});
  }

  atualizarStatus(id: number, status: string): Observable<void> {
    return this.http.patch<void>(`${this.base}/${id}/status`, { status });
  }
}
