import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminSidebar } from '../../../shared/admin-sidebar/admin-sidebar';
import { PedidoService } from '../../../core/services/pedido';
import { Pedido } from '../../../core/models';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule, AdminSidebar],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.scss',
})
export class Pedidos implements OnInit {
  private pedidoSvc = inject(PedidoService);

  pedidos = signal<Pedido[]>([]);
  pedidosDelDia = signal<Pedido[]>([]);
  loading = signal(true);
  mensaje = signal('');

  selectedDate = signal<string | null>(null);
  selectedMonth = signal<number>(new Date().getMonth());
  selectedDay = signal<number | null>(null);

  currentYear = new Date().getFullYear();
  days: (number | null)[] = [];

  months = [
    'Enero',
    'Febrero',
    'Marzo',
    'Abril',
    'Mayo',
    'Junio',
    'Julio',
    'Agosto',
    'Septiembre',
    'Octubre',
    'Noviembre',
    'Diciembre',
  ];

  estados: string[] = ['PENDIENTE', 'PAGADO', 'CONFIRMADO', 'EN_PROCESO', 'LISTO', 'ENTREGADO'];

  ngOnInit(): void {
    this.generarCalendario(this.selectedMonth());
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);

    this.pedidoSvc.getAll().subscribe({
      next: (data) => {
        this.pedidos.set(data);
        this.loading.set(false);

        if (this.selectedDate()) {
          this.refrescarPedidosDelDia();
        }
      },
      error: () => {
        this.loading.set(false);
        this.mostrarMensaje('Error al cargar pedidos');
      },
    });
  }

  seleccionarMes(index: number): void {
    this.selectedMonth.set(index);
    this.generarCalendario(index);

    this.selectedDate.set(null);
    this.selectedDay.set(null);
    this.pedidosDelDia.set([]);
  }

  generarCalendario(month: number): void {
    const firstDay = new Date(this.currentYear, month, 1).getDay();
    const totalDays = new Date(this.currentYear, month + 1, 0).getDate();

    const start = (firstDay + 6) % 7;
    const arr: (number | null)[] = [];

    for (let i = 0; i < start; i++) arr.push(null);
    for (let d = 1; d <= totalDays; d++) arr.push(d);

    this.days = arr;
  }

  seleccionarDia(day: number): void {
    const fecha = this.formatDate(day, this.selectedMonth());

    this.selectedDay.set(day);
    this.selectedDate.set(fecha);

    this.refrescarPedidosDelDia();
  }

  refrescarPedidosDelDia(): void {
    const fecha = this.selectedDate();

    if (!fecha) {
      this.pedidosDelDia.set([]);
      return;
    }

    const filtrados = this.pedidos().filter((p) => p.fechaCreacion?.substring(0, 10) === fecha);

    this.pedidosDelDia.set(filtrados);
  }

  formatDate(day: number, month: number): string {
    const mm = String(month + 1).padStart(2, '0');
    const dd = String(day).padStart(2, '0');
    return `${this.currentYear}-${mm}-${dd}`;
  }

  pedidosPorDia(day: number | null): Pedido[] {
    if (!day) return [];

    const fecha = this.formatDate(day, this.selectedMonth());

    return this.pedidos().filter((p) => p.fechaCreacion?.substring(0, 10) === fecha);
  }

  tienePedidos(day: number | null): boolean {
    return this.pedidosPorDia(day).length > 0;
  }

  cantidadPedidosDia(day: number | null): number {
    return this.pedidosPorDia(day).length;
  }

  totalDiaCalendario(day: number | null): number {
    return this.pedidosPorDia(day).reduce((sum, p) => sum + Number(p.total || 0), 0);
  }

  get totalPedidosDia(): number {
    return this.pedidosDelDia().length;
  }

  get totalMontoDia(): number {
    return this.pedidosDelDia().reduce((sum, p) => sum + Number(p.total || 0), 0);
  }

  get pendientesDia(): number {
    return this.pedidosDelDia().filter((p) => p.estado === 'PENDIENTE').length;
  }

  get entregadosDia(): number {
    return this.pedidosDelDia().filter((p) => p.estado === 'ENTREGADO').length;
  }

  cambiarEstado(id: number, event: Event): void {
    const estado = (event.target as HTMLSelectElement).value;

    this.pedidoSvc.actualizarEstado(id, estado).subscribe({
      next: () => {
        this.mostrarMensaje('Estado actualizado');
        this.cargar();
      },
      error: () => {
        this.mostrarMensaje('Error al actualizar estado');
      },
    });
  }

  estadoClass(estado: string): string {
    switch (estado) {
      case 'PENDIENTE':
        return 'estado-pendiente';
      case 'PAGADO':
        return 'estado-pagado';
      case 'CONFIRMADO':
        return 'estado-confirmado';
      case 'EN_PROCESO':
        return 'estado-proceso';
      case 'LISTO':
        return 'estado-listo';
      case 'ENTREGADO':
        return 'estado-entregado';
      default:
        return '';
    }
  }

  mostrarMensaje(msg: string): void {
    this.mensaje.set(msg);
    setTimeout(() => this.mensaje.set(''), 3000);
  }
}
