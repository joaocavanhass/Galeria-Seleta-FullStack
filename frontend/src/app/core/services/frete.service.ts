import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FreteApi {
  id: number;
  nome: string;
  prazoMinimo: number;
  prazoMaximo: number;
  preco: number;
}

@Injectable({ providedIn: 'root' })
export class FreteService {
  private readonly base = `${environment.apiUrl}/frete`;

  constructor(private http: HttpClient) {}

  listar(): Observable<FreteApi[]> {
    return this.http.get<FreteApi[]>(this.base);
  }
}