import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import {
  NgApexchartsModule,
  ApexAxisChartSeries,
  ApexNonAxisChartSeries,
  ApexChart,
  ApexXAxis,
  ApexStroke,
  ApexFill,
  ApexDataLabels,
  ApexGrid,
  ApexTooltip,
  ApexLegend,
  ApexPlotOptions,
} from 'ng-apexcharts';

import { ReporteService } from '../../../core/services/reporte';

type ChartOptions = {
  series: ApexAxisChartSeries | ApexNonAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  stroke: ApexStroke;
  fill: ApexFill;
  dataLabels: ApexDataLabels;
  grid: ApexGrid;
  tooltip: ApexTooltip;
  colors: string[];
  labels: string[];
  legend: ApexLegend;
  plotOptions: ApexPlotOptions;
};

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule, NgApexchartsModule],
  templateUrl: './reportes.html',
  styleUrl: './reportes.scss',
})
export class Reportes implements OnInit {
  private reporteSvc = inject(ReporteService);

  // =========================
  // 📊 STATE
  // =========================
  loading = signal(true);

  dashboard = signal<any>(null);
  ventas = signal<any>(null);

  topProductos = signal<any[]>([]);
  clienteTop = signal<any>(null);

  pagosYape = signal<any>(null);
  pagosEfectivo = signal<any>(null);

  filtro = signal({
    anio: new Date().getFullYear(),
    mes: 'ALL',
  });

  // =========================
  // 📈 VENTAS (HORIZONTAL: HOY / SEMANA / MES / AÑO)
  // =========================
  ventasChart = computed<Partial<ChartOptions>>(() => {
    const v = this.ventas();

    const data = [
      v?.hoy?.total ?? 0,
      v?.semana?.total ?? 0,
      v?.mes?.total ?? 0,
      v?.anio?.total ?? 0,
    ];

    return {
      series: [{ name: 'Ventas', data }],
      chart: {
        type: 'area',
        height: 320,
        toolbar: { show: false },
        fontFamily: 'inherit',
      },
      colors: ['#c8a97e'],
      dataLabels: { enabled: false },
      stroke: { curve: 'smooth', width: 3 },
      fill: {
        type: 'gradient',
        gradient: {
          shadeIntensity: 1,
          opacityFrom: 0.5,
          opacityTo: 0.05,
          stops: [0, 90, 100],
        },
      },
      xaxis: {
        categories: ['Hoy', 'Semana', 'Mes', 'Año'],
      },
      grid: {
        borderColor: '#eee',
        strokeDashArray: 4,
      },
      tooltip: {
        y: {
          formatter: (val: number) => `S/ ${val.toLocaleString('es-PE')}`,
        },
      },
    };
  });

  // =========================
  // 💳 PAGOS (DONUT)
  // =========================
  pagosChart = computed<Partial<ChartOptions>>(() => {
    const yape = this.pagosYape()?.cantidad ?? 0;
    const efectivo = this.pagosEfectivo()?.cantidad ?? 0;

    return {
      series: [yape, efectivo],
      labels: ['Yape', 'Efectivo'],
      chart: {
        type: 'donut',
        height: 320,
        fontFamily: 'inherit',
      },
      colors: ['#7c3aed', '#16a34a'],
      legend: {
        position: 'bottom',
      },
      dataLabels: {
        enabled: true,
        formatter: (val: number) => `${val.toFixed(0)}%`,
      },
      plotOptions: {
        pie: {
          donut: {
            size: '65%',
          },
        },
      },
    };
  });

  // =========================
  // 🔥 TOP PRODUCTOS
  // =========================
  topChart = computed<Partial<ChartOptions>>(() => {
    const t = this.topProductos();

    return {
      series: [
        {
          name: 'Vendidos',
          data: t.map((x) => x.cantidad),
        },
      ],
      chart: {
        type: 'bar',
        height: 320,
        toolbar: { show: false },
        fontFamily: 'inherit',
      },
      colors: ['#c8a97e'],
      plotOptions: {
        bar: {
          horizontal: true,
          borderRadius: 6,
        },
      },
      dataLabels: {
        enabled: true,
      },
      xaxis: {
        categories: t.map((x) => x.nombreProducto || x.nombre),
      },
      grid: {
        borderColor: '#eee',
        strokeDashArray: 4,
      },
    };
  });

  // =========================
  // 🚀 INIT
  // =========================
  ngOnInit(): void {
    this.cargar();
  }

  // =========================
  // 📥 LOAD ALL API
  // =========================
  cargar(): void {
    this.loading.set(true);

    forkJoin({
      dashboard: this.reporteSvc.dashboard(),

      hoy: this.reporteSvc.ventasHoy(),
      semana: this.reporteSvc.ventasSemana(),
      mes: this.reporteSvc.ventasMes(),
      anio: this.reporteSvc.ventasAnio(),

      topProductos: this.reporteSvc.topProductos(),

      clienteTop: this.reporteSvc.clienteTop(),

      yape: this.reporteSvc.pagosYape(),
      efectivo: this.reporteSvc.pagosEfectivo(),
    }).subscribe({
      next: (res) => {
        this.dashboard.set(res.dashboard);

        this.ventas.set({
          hoy: res.hoy,
          semana: res.semana,
          mes: res.mes,
          anio: res.anio,
        });

        this.topProductos.set(res.topProductos ?? []);
        this.clienteTop.set(res.clienteTop);

        this.pagosYape.set(res.yape);
        this.pagosEfectivo.set(res.efectivo);

        this.loading.set(false);
      },

      error: (err) => {
        console.error('Error reportes:', err);
        this.loading.set(false);
      },
    });
  }

  cambiarFiltro(): void {
    this.cargar();
  }
}
