import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Carrito, AgregarProductoRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class CarritoService {
  private base = `${environment.apiUrl}/api/v1/carrito/usuario`;

  constructor(private http: HttpClient) {}

  getCarrito(userId: number) {
    return this.http.get<Carrito>(`${this.base}/${userId}`);
  }

  crearCarrito(userId: number) {
    return this.http.post<Carrito>(`${this.base}/${userId}`, {});
  }

  agregarProducto(userId: number, req: AgregarProductoRequest) {
    return this.http.post<Carrito>(`${this.base}/${userId}/productos`, req);
  }

  actualizarCantidad(userId: number, idDetalle: number, cantidad: number) {
    return this.http.put<Carrito>(`${this.base}/${userId}/productos/cantidad`, {
      idDetalle,
      cantidad,
    });
  }

  eliminarProducto(userId: number, idDetalle: number) {
    return this.http.delete<void>(`${this.base}/${userId}/productos/${idDetalle}`);
  }

  vaciarCarrito(userId: number) {
    return this.http.delete<void>(`${this.base}/${userId}`);
  }

  confirmarCarrito(userId: number) {
    return this.http.post(`http://localhost:7091/api/v1/carrito/usuario/${userId}/confirmar`, {});
  }
}
