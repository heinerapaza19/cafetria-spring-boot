import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';
import { adminGuard } from './core/guards/admin-guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/home/home').then((m) => m.Home) },
  { path: 'about', loadComponent: () => import('./pages/about/about').then((m) => m.About) },
  { path: 'menu', loadComponent: () => import('./pages/menu/menu').then((m) => m.Menu) },
  {
    path: 'gallery',
    loadComponent: () => import('./pages/gallery/gallery').then((m) => m.Gallery),
  },
  {
    path: 'contact',
    loadComponent: () => import('./pages/contact/contact').then((m) => m.Contact),
  },
  {
    path: 'reservation',
    loadComponent: () => import('./pages/reservation/reservation').then((m) => m.Reservation),
  },
  { path: 'login', loadComponent: () => import('./pages/login/login').then((m) => m.Login) },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register').then((m) => m.Register),
  },
  {
    path: 'carrito',
    loadComponent: () => import('./pages/carrito/carrito').then((m) => m.Carrito),
    canActivate: [authGuard],
  },
  {
    path: 'mis-pedidos',
    loadComponent: () => import('./pages/mis-pedidos/mis-pedidos').then((m) => m.MisPedidos),
    canActivate: [authGuard],
  },

  //protegiso para adminostradores
  {
    path: 'admin',
    canActivate: [adminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      {
        path: 'dashboard',
        loadComponent: () => import('./pages/admin/dashboard/dashboard').then((m) => m.Dashboard),
      },

      {
        path: 'productos',
        loadComponent: () => import('./pages/admin/productos/productos').then((m) => m.Productos),
      },

      {
        path: 'categorias',
        loadComponent: () =>
          import('./pages/admin/categorias/categorias').then((m) => m.Categorias),
      },

      {
        path: 'pedidos',
        loadComponent: () => import('./pages/admin/pedidos/pedidos').then((m) => m.Pedidos),
      },

      // ✅ CORRECTO
      {
        path: 'pagos',
        loadComponent: () => import('./pages/admin/pagos/pagos').then((m) => m.Pagos),
      },
      {
        path: 'reportes',
        loadComponent: () => import('./pages/admin/reportes/reportes').then((m) => m.Reportes),
      },
    ],
  },

  { path: '**', redirectTo: '' },
];
