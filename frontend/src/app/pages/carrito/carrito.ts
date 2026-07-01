import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router'; // 🔥 FIX AQUÍ
import { FormsModule } from '@angular/forms';

import { Header } from '../../shared/header/header';
import { Footer } from '../../shared/footer/footer';

import { CarritoService } from '../../core/services/carrito';
import { PagoService } from '../../core/services/pago';
import { PedidoService } from '../../core/services/pedido'; // 🔥 FIX AQUÍ
import { AuthService } from '../../core/services/auth';

import { Carrito as CarritoModel } from '../../core/models';

@Component({
  selector: 'app-carrito',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, Header, Footer],
  templateUrl: './carrito.html',
  styleUrl: './carrito.scss',
})
export class Carrito implements OnInit {
  private carritoSvc = inject(CarritoService);
  private pagoSvc = inject(PagoService);
  private pedidoSvc = inject(PedidoService); // 🔥 FIX AQUÍ
  private router = inject(Router); // 🔥 FIX AQUÍ

  auth = inject(AuthService);

  carrito = signal<CarritoModel | null>(null);
  loading = signal(true);
  pagando = signal(false);

  metodoPago: 'EFECTIVO' | 'YAPE' = 'EFECTIVO';

  // YAPE
  yapeNumero = '980 814 183';
  yapeNombre = 'Heiner Apa';
  yapeQr = 'assets/img/yape.png';

  mensaje = signal('');
  mensajeTipo = signal<'success' | 'error'>('success');

  baseUrl = 'http://localhost:7091/uploads/productos';

  ngOnInit() {
    this.cargarCarrito();
  }

  cargarCarrito() {
    const userId = this.auth.getUserId();

    this.carritoSvc.getCarrito(userId).subscribe({
      next: (res: any) => {
        this.carrito.set(res.data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.carrito.set(null);
      },
    });
  }

  cambiarCantidad(idDetalle: number, cantidad: number) {
    if (cantidad < 1) return;

    this.carritoSvc.actualizarCantidad(this.auth.getUserId(), idDetalle, cantidad).subscribe({
      next: (res: any) => {
        this.carrito.set(res.data);
      },
    });
  }

  eliminarItem(idDetalle: number) {
    this.carritoSvc.eliminarProducto(this.auth.getUserId(), idDetalle).subscribe({
      next: () => this.cargarCarrito(),
    });
  }

  vaciarCarrito() {
    this.carritoSvc.vaciarCarrito(this.auth.getUserId()).subscribe({
      next: () => this.carrito.set(null),
    });
  }

  confirmarYPagar() {
    if (this.pagando()) return;

    if (!this.metodoPago) {
      this.mostrarMensaje('Selecciona un método de pago', 'error');
      return;
    }

    const userId = Number(this.auth.getUserId());

    if (!userId) {
      this.mostrarMensaje('Usuario no válido', 'error');
      return;
    }

    const carritoActual = this.carrito();

    if (!carritoActual || !carritoActual.detalles?.length) {
      this.mostrarMensaje('El carrito está vacío', 'error');
      return;
    }

    if (carritoActual.estado !== 'ACTIVO') {
      this.mostrarMensaje('Este carrito ya fue procesado', 'error');
      return;
    }

    this.pagando.set(true);

    const pedidoPayload = {
      detalles: carritoActual.detalles.map((d) => ({
        idProducto: d.idProducto,
        cantidad: d.cantidad,
      })),
    };

    this.pedidoSvc.crearPedido(pedidoPayload).subscribe({
      next: (pedido: any) => {
        if (!pedido?.id) {
          this.pagando.set(false);
          this.mostrarMensaje('No se pudo generar el pedido', 'error');
          return;
        }

        const pagoRequest = {
          pedidoId: pedido.id,
          monto: carritoActual.total ?? 0,
          metodo: this.metodoPago,
        };

        this.pagoSvc.crear(pagoRequest).subscribe({
          next: () => {
            this.pagando.set(false);

            this.carrito.set(null);

            this.mostrarMensaje('Pedido y pago registrados correctamente', 'success');

            this.router.navigate(['/mis-pedidos']);
          },

          error: (err: any) => {
            this.pagando.set(false);
            console.error('Error pago:', err);

            this.mostrarMensaje(err?.error?.message ?? 'Error al registrar pago', 'error');
          },
        });
      },

      error: (err: any) => {
        this.pagando.set(false);
        console.error('Error creando pedido:', err);

        this.mostrarMensaje(err?.error?.message ?? 'Error al crear pedido', 'error');
      },
    });
  }

  mostrarMensaje(msg: string, tipo: 'success' | 'error') {
    this.mensaje.set(msg);
    this.mensajeTipo.set(tipo);

    setTimeout(() => this.mensaje.set(''), 3000);
  }

  getImageUrl(img: string | null | undefined): string {
    if (!img) return 'assets/img/no-image.png';
    return `http://localhost:7091/uploads/productos/${img}`;
  }
}
