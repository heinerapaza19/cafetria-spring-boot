import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';

@Injectable({ providedIn: 'root' })
export class PagoService {
  private api = 'http://localhost:7091/api/v1/pagos';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
  ) {}

  listar(): Observable<any[]> {
    return this.http.get<any[]>(this.api);
  }

  crear(data: any): Observable<any> {
    const token = this.auth.getToken();

    return this.http.post(this.api, data, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  rechazar(id: number) {
    return this.http.put(`${this.api}/rechazar/${id}`, {});
  }

  confirmar(id: number): Observable<any> {
    const token = this.auth.getToken();

    return this.http.put(
      `${this.api}/confirmar/${id}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    );
  }
}
