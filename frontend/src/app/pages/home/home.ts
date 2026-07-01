import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { RouterLink } from '@angular/router';

import { Header } from '../../shared/header/header';
import { Footer } from '../../shared/footer/footer';

import { ProductoService } from '../../core/services/producto';
import { CategoriaService } from '../../core/services/categoria';
import { CarritoService } from '../../core/services/carrito';
import { AuthService } from '../../core/services/auth';

import { Producto, Categoria } from '../../core/models';

type ButtonType = 'primary' | 'secondary' | 'ghost';

interface SlideButton {
  text: string;
  link: string;
  icon?: string;
  type: ButtonType;
}

interface Slide {
  bg: string;
  tag: string;
  title: string;
  sub: string;
  buttons: SlideButton[];
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, Header, Footer, NgClass],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  private productoSvc = inject(ProductoService);
  private categoriaSvc = inject(CategoriaService);
  private carritoSvc = inject(CarritoService);

  auth = inject(AuthService);

  productos = signal<Producto[]>([]);
  categorias = signal<Categoria[]>([]);
  loading = signal(true);
  addingId = signal<number | null>(null);
  mensaje = signal('');
  activeSlide = signal(0);

  slides: Slide[] = [
    {
      bg: 'linear-gradient(135deg, #2c1a0e 0%, #5c3317 100%)',
      tag: 'Café & Restaurante',
      title: '¡Bienvenido!',
      sub: 'EL MEJOR CAFÉ DE LA CIUDAD',
      buttons: [
        {
          text: 'VER MENÚ',
          link: '/menu',
          icon: 'fa-utensils',
          type: 'primary',
        },
        {
          text: 'RESERVAR MESA',
          link: '/reservation',
          icon: 'fa-calendar-check',
          type: 'secondary',
        },
        {
          text: 'SOBRE NOSOTROS',
          link: '/about',
          icon: 'fa-info-circle',
          type: 'ghost',
        },
      ],
    },
    {
      bg: 'linear-gradient(135deg, #1a0e05 0%, #3d2010 100%)',
      tag: 'Café & Restaurante',
      title: 'CAPPUCCINO',
      sub: 'ESPECIALIDADES DEL DÍA',
      buttons: [
        {
          text: 'VER MENÚ',
          link: '/menu',
          icon: 'fa-utensils',
          type: 'primary',
        },
        {
          text: 'RESERVAR MESA',
          link: '/reservation',
          icon: 'fa-calendar-check',
          type: 'secondary',
        },
        {
          text: 'GALERÍA',
          link: '/gallery',
          icon: 'fa-images',
          type: 'ghost',
        },
      ],
    },
  ];

  ngOnInit(): void {
    this.productoSvc.getAll().subscribe({
      next: (data) => {
        this.productos.set(data.slice(0, 6));
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });

    this.categoriaSvc.getAll().subscribe({
      next: (data) => {
        this.categorias.set(data);
      },
      error: () => {
        this.categorias.set([]);
      },
    });
  }

  agregarAlCarrito(producto: Producto): void {
    if (!this.auth.isLoggedIn()) {
      this.mensaje.set('Inicia sesión para agregar al carrito');

      setTimeout(() => {
        this.mensaje.set('');
      }, 3000);

      return;
    }

    this.addingId.set(producto.id);

    const userId = this.auth.getUserId();

    this.carritoSvc
      .agregarProducto(userId, {
        idProducto: producto.id,
        cantidad: 1,
      })
      .subscribe({
        next: () => {
          this.addingId.set(null);
          this.mensaje.set(`"${producto.nombre}" añadido al carrito`);

          setTimeout(() => {
            this.mensaje.set('');
          }, 3000);
        },
        error: () => {
          this.addingId.set(null);
          this.mensaje.set('Error al agregar producto');

          setTimeout(() => {
            this.mensaje.set('');
          }, 3000);
        },
      });
  }

  setSlide(i: number): void {
    this.activeSlide.set(i);
  }

  getImgUrl(img: string): string {
    if (!img) {
      return 'assets/img/no-image.png';
    }

    return img.startsWith('http') ? img : `http://localhost:7091${img}`;
  }
}
