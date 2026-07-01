import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Producto } from '../models';

@Injectable({
  providedIn: 'root',
})
export class ProductoService {
  private url = `${environment.apiUrl}/api/v1/productos`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.url);
  }

  getById(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.url}/${id}`);
  }

  create(formData: FormData): Observable<Producto> {
    return this.http.post<Producto>(this.url, formData);
  }

  update(id: number, formData: FormData): Observable<Producto> {
    return this.http.put<Producto>(`${this.url}/${id}`, formData);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
