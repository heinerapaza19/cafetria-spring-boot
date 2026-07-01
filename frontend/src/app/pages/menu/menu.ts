import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { Header } from '../../shared/header/header';
import { Footer } from '../../shared/footer/footer';

import { ProductoService } from '../../core/services/producto';
import { CategoriaService } from '../../core/services/categoria';
import { CarritoService } from '../../core/services/carrito';
import { AuthService } from '../../core/services/auth';

import { Producto, Categoria } from '../../core/models/index';

@Component({
  selector: 'app-menu',
  imports: [CommonModule, RouterLink, Header, Footer],
  templateUrl: './menu.html',
  styleUrl: './menu.scss',
})
export class Menu implements OnInit {
  private productoSvc = inject(ProductoService);
  private categoriaSvc = inject(CategoriaService);
  private carritoSvc = inject(CarritoService);
  auth = inject(AuthService);

  productos = signal<Producto[]>([]);
  categorias = signal<Categoria[]>([]);
  productosFiltrados = signal<Producto[]>([]);

  categoriaActiva = signal<number | null>(null);
  loading = signal(true);
  addingId = signal<number | null>(null);
  mensaje = signal('');
  errorCarga = signal('');

  ngOnInit(): void {
    this.cargarCategorias();
    this.cargarProductos();
  }

  // =========================
  // CATEGORÍAS
  // =========================
  cargarCategorias(): void {
    this.categoriaSvc.getAll().subscribe({
      next: (cats) => {
        console.log('CATEGORÍAS DESDE BACKEND:', cats);
        this.categorias.set(cats);
      },
      error: (err) => {
        console.error('ERROR CATEGORÍAS:', err);
        this.errorCarga.set('No se pudieron cargar las categorías.');
      },
    });
  }

  // =========================
  // PRODUCTOS
  // =========================
  cargarProductos(): void {
    this.loading.set(true);

    this.productoSvc.getAll().subscribe({
      next: (data) => {
        console.log('PRODUCTOS DESDE BACKEND:', data);

        this.productos.set(data);
        this.productosFiltrados.set(data);

        this.loading.set(false);
      },
      error: (err) => {
        console.error('ERROR PRODUCTOS:', err);

        this.productos.set([]);
        this.productosFiltrados.set([]);

        if (err.status === 401 || err.status === 403) {
          this.errorCarga.set('Backend bloqueando acceso.');
        } else if (err.status === 0) {
          this.errorCarga.set('Error de conexión o CORS.');
        } else {
          this.errorCarga.set('Error al cargar productos.');
        }

        this.loading.set(false);
      },
    });
  }

  // =========================
  // FILTRO CATEGORÍAS
  // =========================
  filtrarPorCategoria(idCat: number | null): void {
    this.categoriaActiva.set(idCat);

    if (idCat === null) {
      this.productosFiltrados.set(this.productos());
      return;
    }

    const filtrados = this.productos().filter((p: any) => {
      const categoriaProducto =
        p.idCategoria ?? p.categoriaId ?? p.categoria_id ?? p.categoria?.id ?? null;

      return Number(categoriaProducto) === Number(idCat);
    });

    this.productosFiltrados.set(filtrados);
  }

  // =========================
  // CARRITO
  // =========================
  agregarAlCarrito(p: Producto): void {
    // ✔ USAR TOKEN REAL DIRECTO
    const token = localStorage.getItem('kc_token');

    if (!token) {
      this.setMensaje('Inicia sesión para agregar al carrito');
      return;
    }

    this.addingId.set(p.id);

    this.carritoSvc
      .agregarProducto(this.auth.getUserId(), {
        idProducto: p.id,
        cantidad: 1,
      })
      .subscribe({
        next: () => {
          this.addingId.set(null);
          this.setMensaje(`"${p.nombre}" añadido al carrito`);
        },
        error: (err) => {
          this.addingId.set(null);

          if (err.status === 401) {
            this.setMensaje('Sesión expirada. Inicia sesión nuevamente');
          } else {
            this.setMensaje('Error al agregar producto');
          }
        },
      });
  }

  // =========================
  // MENSAJE
  // =========================
  setMensaje(msg: string): void {
    this.mensaje.set(msg);

    setTimeout(() => {
      this.mensaje.set('');
    }, 3000);
  }

  // =========================
  // IMÁGENES (CORREGIDO)
  // =========================

  getImgUrl(img: string): string {
    if (!img) return '';

    if (img.startsWith('http')) return img;

    return `http://localhost:7091/uploads/productos/${img}`;
  }

  getImgCategoria(img: string): string {
    if (!img) {
      return 'assets/img/no-image.png';
    }

    if (img.startsWith('http')) {
      return img;
    }

    // CATEGORÍAS
    return `http://localhost:7091/api/v1/categorias/imagenes/${img}`;
  }
}
