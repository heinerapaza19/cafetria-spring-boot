import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Pedido } from '../models';

export interface CrearPedidoRequest {
  detalles: {
    idProducto: number;
    cantidad: number;
  }[];
}

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private url = `${environment.apiUrl}/api/v1/pedidos`;

  constructor(private http: HttpClient) {}

  // =========================
  // CREAR PEDIDO
  // =========================
  crearPedido(data: CrearPedidoRequest) {
    return this.http.post<Pedido>(this.url, data);
  }

  // =========================
  // TODOS (ADMIN)
  // =========================
  getAll() {
    return this.http.get<Pedido[]>(this.url);
  }

  // =========================
  // POR ID
  // =========================
  getById(id: number) {
    return this.http.get<Pedido>(`${this.url}/${id}`);
  }

  // =========================
  // MIS PEDIDOS (USER)
  // =========================
  getMyPedidos() {
    return this.http.get<Pedido[]>(`${this.url}/mis-pedidos`);
  }

  // =========================
  // ACTUALIZAR ESTADO
  // =========================
  actualizarEstado(id: number, estado: string) {
    return this.http.patch<Pedido>(`${this.url}/${id}/estado`, { estado });
  }
}
