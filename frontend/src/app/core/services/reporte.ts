import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { DashboardPowerBI } from '../models';

export interface ProductoTop {
  nombreProducto: string;
  cantidad: number;
}

@Injectable({
  providedIn: 'root',
})
export class ReporteService {
  private url = `${environment.apiUrl}/api/v1/reportes`;

  constructor(private http: HttpClient) {}

  // ✅ POWER BI DASHBOARD (ARREGLADO)
  dashboard() {
    return this.http.get<DashboardPowerBI>(`${this.url}/dashboard`);
  }

  ventasHoy() {
    return this.http.get<number>(`${this.url}/ventas/hoy`);
  }

  ventasSemana() {
    return this.http.get<number>(`${this.url}/ventas/semana`);
  }

  ventasMes() {
    return this.http.get<number>(`${this.url}/ventas/mes`);
  }

  ventasAnio() {
    return this.http.get<number>(`${this.url}/ventas/anio`);
  }

  // 🔥 PRODUCTOS TOP
  topProductos() {
    return this.http.get<ProductoTop[]>(`${this.url}/productos/top`);
  }

  clienteTop() {
    return this.http.get<any>(`${this.url}/clientes/top`);
  }

  pagosYape() {
    return this.http.get<number>(`${this.url}/pagos/yape`);
  }

  pagosEfectivo() {
    return this.http.get<number>(`${this.url}/pagos/efectivo`);
  }
}
