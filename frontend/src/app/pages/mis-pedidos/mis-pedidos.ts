import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { Header } from '../../shared/header/header';
import { Footer } from '../../shared/footer/footer';

import { PedidoService } from '../../core/services/pedido';
import { Pedido } from '../../core/models';

@Component({
  selector: 'app-mis-pedidos',
  standalone: true,
  imports: [CommonModule, RouterLink, Header, Footer],
  templateUrl: './mis-pedidos.html',
  styleUrl: './mis-pedidos.scss',
})
export class MisPedidos implements OnInit {
  private pedidoSvc = inject(PedidoService);

  pedidos = signal<Pedido[]>([]);
  loading = signal(true);
  errorMsg = signal<string | null>(null);
  pedidoAbierto = signal<number | null>(null);

  estadosPedido = [
    { key: 'PENDIENTE', label: 'Pendiente', icon: 'fa-clock' },
    { key: 'CONFIRMADO', label: 'Confirmado', icon: 'fa-circle-check' },
    { key: 'EN_PROCESO', label: 'En proceso', icon: 'fa-mug-hot' },
    { key: 'LISTO', label: 'Listo', icon: 'fa-bell' },
    { key: 'ENTREGADO', label: 'Entregado', icon: 'fa-bag-shopping' },
  ];

  totalPedidos = computed(() => this.pedidos().length);

  pedidosPendientes = computed(() => this.pedidos().filter((p) => p.estado === 'PENDIENTE').length);

  totalGastado = computed(() => this.pedidos().reduce((acc, p) => acc + Number(p.total || 0), 0));

  ngOnInit() {
    this.cargarPedidos();
  }

  cargarPedidos() {
    this.loading.set(true);
    this.errorMsg.set(null);

    this.pedidoSvc.getMyPedidos().subscribe({
      next: (data) => {
        console.log('PEDIDOS:', data);

        const pedidosOrdenados = [...(data ?? [])].sort((a, b) => {
          const fechaA = new Date(a.fechaCreacion as any).getTime();
          const fechaB = new Date(b.fechaCreacion as any).getTime();
          return fechaB - fechaA;
        });

        this.pedidos.set(pedidosOrdenados);

        if (pedidosOrdenados.length > 0) {
          this.pedidoAbierto.set(pedidosOrdenados[0].id);
        }

        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.pedidos.set([]);
        this.errorMsg.set('No pudimos obtener tus pedidos. Intenta nuevamente.');
        this.loading.set(false);
      },
    });
  }

  toggleDetalle(id: number) {
    this.pedidoAbierto.update((v) => (v === id ? null : id));
  }

  estadoClass(estado: string): string {
    const map: Record<string, string> = {
      PENDIENTE: 'estado-pendiente',
      CONFIRMADO: 'estado-confirmado',
      EN_PROCESO: 'estado-proceso',
      LISTO: 'estado-listo',
      ENTREGADO: 'estado-entregado',
      PAGADO: 'estado-pagado',
      CANCELADO: 'estado-cancelado',
    };

    return map[estado] || 'estado-default';
  }

  estadoLabel(estado: string): string {
    const map: Record<string, string> = {
      PENDIENTE: 'Pendiente',
      CONFIRMADO: 'Confirmado',
      EN_PROCESO: 'En proceso',
      LISTO: 'Listo',
      ENTREGADO: 'Entregado',
      PAGADO: 'Pagado',
      CANCELADO: 'Cancelado',
    };

    return map[estado] || estado;
  }

  estadoIndex(estado: string): number {
    const map: Record<string, number> = {
      PENDIENTE: 0,
      CONFIRMADO: 1,
      EN_PROCESO: 2,
      LISTO: 3,
      ENTREGADO: 4,
      PAGADO: 4,
    };

    return map[estado] ?? 0;
  }

  totalItems(pedido: Pedido): number {
    return pedido.detalles?.reduce((acc, d) => acc + Number(d.cantidad || 0), 0) ?? 0;
  }
}
