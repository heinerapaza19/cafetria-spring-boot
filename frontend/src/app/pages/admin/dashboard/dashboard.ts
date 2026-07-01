import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminSidebar } from '../../../shared/admin-sidebar/admin-sidebar';
import { ReporteService } from '../../../core/services/reporte';
import { DashboardPowerBI } from '../../../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, AdminSidebar],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  private reporteSvc = inject(ReporteService);

  data = signal<DashboardPowerBI | null>(null);
  loading = signal(true);

  ngOnInit(): void {
    this.loading.set(true);

    this.reporteSvc.dashboard().subscribe({
      next: (d: DashboardPowerBI) => {
        this.data.set(d);
        this.loading.set(false);
      },
      error: (err: any) => {
        console.error('Error dashboard:', err);
        this.loading.set(false);
      },
    });
  }
}
