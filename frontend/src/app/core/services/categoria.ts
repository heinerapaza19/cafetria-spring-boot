import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Categoria } from '../models';

@Injectable({
  providedIn: 'root',
})
export class CategoriaService {
  private url = `${environment.apiUrl}/api/v1/categorias`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.url);
  }

  getById(id: number): Observable<Categoria> {
    return this.http.get<Categoria>(`${this.url}/${id}`);
  }

  create(formData: FormData): Observable<Categoria> {
    return this.http.post<Categoria>(this.url, formData);
  }

  update(id: number, formData: FormData): Observable<Categoria> {
    return this.http.put<Categoria>(`${this.url}/${id}`, formData);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
