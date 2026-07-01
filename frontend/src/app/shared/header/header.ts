import { Component, signal, inject, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { keycloak } from '../../auth/keycloak.config';

import { CarritoService } from '../../core/services/carrito';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header implements OnInit {
  sidenavOpen = signal(false);

  userName = signal('Usuario');
  loggedIn = signal(false);
  isAdmin = signal(false);

  cartCount = signal(0);

  private carritoSvc = inject(CarritoService);
  private auth = inject(AuthService);

  constructor() {
    this.loadUser();
  }

  ngOnInit() {
    this.loadCartCount();
  }

  // =========================
  // USER KEYCLOAK
  // =========================
  private loadUser() {
    this.loggedIn.set(!!keycloak.authenticated);

    if (!keycloak.authenticated) return;

    const token: any = keycloak.tokenParsed;

    this.userName.set(token?.preferred_username || token?.name || 'Usuario');

    const roles: string[] = token?.realm_access?.roles || [];

    this.isAdmin.set(roles.includes('ROLE_ADMIN') || roles.includes('ADMIN'));
  }

  // =========================
  // CARRITO COUNT
  // =========================
  loadCartCount() {
    const userId = this.auth.getUserId();

    if (!userId) {
      this.cartCount.set(0);
      return;
    }

    /*  this.carritoSvc.getCarrito(userId).subscribe({
      next: (res: any) => {
        this.cartCount.set(res?.data?.detalles?.length || 0);
      },
      error: (err: any) => {
        const msg = err?.error?.message;

        // 💡 CASO NORMAL DESPUÉS DE PAGAR
        if (msg === 'No hay carrito activo') {
          this.cartCount.set(0);
          return;
        }

        console.error('Error carrito header:', err);
        this.cartCount.set(0);
      },
    });*/
  }

  openAdmin() {
    const token: any = keycloak.tokenParsed;

    const roles: string[] = token?.realm_access?.roles || [];

    const isAdmin = roles.includes('ROLE_ADMIN') || roles.includes('ADMIN');

    // 🚨 SI NO ESTÁ LOGUEADO → KEYCLOAK LOGIN
    if (!keycloak.authenticated) {
      keycloak.login({
        redirectUri: window.location.origin + '/admin',
      });
      return;
    }

    // 🚨 SI NO ES ADMIN → BLOQUEA
    if (!isAdmin) {
      alert('No tienes permisos');
      return;
    }

    // ✅ SI ES ADMIN → ENTRA
    window.location.href = '/admin';
  }

  refreshCart() {
    this.loadCartCount();
  }

  // =========================
  // AUTH
  // =========================
  login() {
    keycloak.login({
      redirectUri: window.location.origin,
    });
  }

  logout() {
    keycloak.logout({
      redirectUri: window.location.origin,
    });
  }

  // =========================
  // UI
  // =========================
  toggleSidenav() {
    this.sidenavOpen.update((v) => !v);
  }

  closeSidenav() {
    this.sidenavOpen.set(false);
  }

  // =========================
  // ADMIN LOGIN REDIRECT
  // =========================
  goToAdmin() {
    if (!keycloak.authenticated) {
      keycloak.login({
        redirectUri: window.location.origin + '/admin',
      });
      return;
    }

    const roles: string[] = keycloak.tokenParsed?.realm_access?.roles || [];

    if (roles.includes('ROLE_ADMIN') || roles.includes('ADMIN')) {
      window.location.href = '/admin';
    } else {
      alert('No tienes permisos de administrador');
    }
  }
}
