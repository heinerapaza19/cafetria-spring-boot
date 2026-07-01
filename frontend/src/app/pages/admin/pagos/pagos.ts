import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PagoService } from '../../../core/services/pago';

@Component({
  selector: 'app-pagos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pagos.html',
  styleUrl: './pagos.scss',
})
export class Pagos implements OnInit {
  private pagoSvc = inject(PagoService);

  // =========================
  // 📦 DATA
  // =========================
  pagos = signal<any[]>([]);
  loading = signal(true);

  selectedDate = signal<string | null>(null);
  selectedMonth = signal<number>(new Date().getMonth());

  pagosDelDia = signal<any[]>([]);

  days: (number | null)[] = [];
  currentYear = new Date().getFullYear();
  getEstado(p: any): string {
    return p.ticketGenerado ? 'PAGADO' : 'PENDIENTE';
  }

  // =========================
  // 📅 MESES
  // =========================
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

  // =========================
  // 📊 DASHBOARD
  // =========================
  totalDia = computed(() => this.pagosDelDia().reduce((sum, p) => sum + (p.monto || 0), 0));

  totalMes = computed(() => this.pagos().reduce((sum, p) => sum + (p.monto || 0), 0));

  ventasDia = computed(() => this.pagosDelDia().filter((p) => p.estado === 'PAGADO').length);

  pendientesDia = computed(() => this.pagosDelDia().filter((p) => p.estado === 'PENDIENTE').length);

  // =========================
  // 🚀 INIT
  // =========================
  ngOnInit() {
    this.cargar();
    this.generarCalendario(this.selectedMonth());
  }

  // =========================
  // 📥 CARGAR PAGOS
  // =========================
  cargar() {
    this.loading.set(true);

    this.pagoSvc.listar().subscribe({
      next: (data) => {
        console.log('PAGOS DESDE BACKEND:', data);
        this.pagos.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  rechazarPago(id: number) {
    this.pagoSvc.rechazar(id).subscribe({
      next: () => this.cargar(),
    });
  }

  // =========================
  // 📅 MES
  // =========================
  seleccionarMes(index: number) {
    this.selectedMonth.set(index);
    this.generarCalendario(index);
    this.selectedDate.set(null);
    this.pagosDelDia.set([]);
  }

  generarCalendario(month: number | null) {
    if (month === null) return;

    const firstDay = new Date(this.currentYear, month, 1).getDay();
    const totalDays = new Date(this.currentYear, month + 1, 0).getDate();

    const start = (firstDay + 6) % 7;

    const arr: (number | null)[] = [];

    for (let i = 0; i < start; i++) arr.push(null);
    for (let d = 1; d <= totalDays; d++) arr.push(d);

    this.days = arr;
  }

  // =========================
  // 📅 DÍA
  // =========================
  seleccionarDia(day: number) {
    const month = this.selectedMonth();
    const fecha = this.formatDate(day, month);

    this.selectedDate.set(fecha);

    const filtrados = this.pagos().filter((p) => p.fecha?.substring(0, 10) === fecha);

    this.pagosDelDia.set(filtrados);
  }

  formatDate(day: number, month: number) {
    const mm = String(month + 1).padStart(2, '0');
    const dd = String(day).padStart(2, '0');
    return `${this.currentYear}-${mm}-${dd}`;
  }

  ventasPorDia(day: number | null) {
    if (!day) return [];

    const fecha = this.formatDate(day, this.selectedMonth());

    return this.pagos().filter((p) => p.fecha?.substring(0, 10) === fecha);
  }

  tieneVentas(day: number | null): boolean {
    return this.ventasPorDia(day).length > 0;
  }

  // =========================
  // ✔ CONFIRMAR
  // =========================
  confirmarPago(id: number) {
    this.pagoSvc.confirmar(id).subscribe({
      next: () => this.cargar(),
    });
  }

  // =========================
  // 🎫 FIX ID + TICKET
  // =========================
  getPagoId(p: any): number | null {
    return p.id ?? p.pagoId ?? p.pedidoId ?? null;
  }

  verTicket(p: any) {
    const id = this.getPagoId(p);

    if (!id) {
      console.error('❌ Pago sin ID válido:', p);
      alert('Pago sin ID válido');
      return;
    }

    const url = `http://localhost:7091/api/v1/pagos/${id}/ticket`;

    console.log('🎫 URL ticket:', url);

    window.open(url, '_blank');
  }

  estadoNormalizado(p: any): string {
    return (p.estado ?? '').toLowerCase();
  }

  // =========================
  // 📄 EXPORT
  // =========================
  exportarPDF() {
    console.log('EXPORT PDF:', this.pagosDelDia());
  }

  exportarExcel() {
    console.log('EXPORT EXCEL:', this.pagosDelDia());
  }
}
