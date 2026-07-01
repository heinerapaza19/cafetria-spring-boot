import { Injectable, signal } from '@angular/core';
import { keycloak } from '../../auth/keycloak.config';

export interface AuthUser {
  username: string;
  email?: string;
  role?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  loggedIn = signal(false);
  userName = signal('');
  currentUser = signal<AuthUser | null>(null);

  // NO inicializa Keycloak aquí
  // Keycloak ya se inicializa en main.ts
  init(): boolean {
    if (keycloak.authenticated) {
      this.loggedIn.set(true);
      this.cargarDatosUsuario();
      return true;
    }

    return false;
  }

  getToken(): string | null {
    return keycloak.token ?? null;
  }

  async updateToken(): Promise<string | null> {
    if (!keycloak.authenticated) {
      return null;
    }

    try {
      await keycloak.updateToken(30);
      return keycloak.token ?? null;
    } catch (error) {
      console.error('Error refresh token:', error);
      this.logout();
      return null;
    }
  }

  login(): void {
    keycloak.login({
      redirectUri: window.location.origin,
    });
  }

  register(): void {
    keycloak.register({
      redirectUri: window.location.origin,
    });
  }

  logout(): void {
    this.loggedIn.set(false);
    this.userName.set('');
    this.currentUser.set(null);

    keycloak.logout({
      redirectUri: window.location.origin,
    });
  }

  isLoggedIn(): boolean {
    return keycloak.authenticated === true;
  }

  isReady(): boolean {
    return true;
  }

  getUserName(): string {
    return this.currentUser()?.username || 'Usuario';
  }

  getUserId(): number {
    return 1;
  }

  getRoles(): string[] {
    const tokenParsed: any = keycloak.tokenParsed;

    const realmRoles: string[] = tokenParsed?.realm_access?.roles || [];
    const clientRoles: string[] = tokenParsed?.resource_access?.['cafeteria-app']?.roles || [];

    return [...realmRoles, ...clientRoles];
  }

  isAdmin(): boolean {
    return this.getRoles().includes('ROLE_ADMIN');
  }

  isUser(): boolean {
    return this.getRoles().includes('ROLE_USER');
  }

  private cargarDatosUsuario(): void {
    const tokenParsed: any = keycloak.tokenParsed;

    const username =
      tokenParsed?.preferred_username || tokenParsed?.name || tokenParsed?.email || 'Usuario';

    const email = tokenParsed?.email || '';
    const roles = this.getRoles();

    const role = roles.includes('ROLE_ADMIN') ? 'ROLE_ADMIN' : 'ROLE_USER';

    this.loggedIn.set(true);
    this.userName.set(username);

    this.currentUser.set({
      username,
      email,
      role,
    });

    console.log('👤 USER LOGGED:', this.currentUser());
    console.log('🎭 ROLES:', roles);
  }

  isAuthenticated(): boolean {
    return keycloak.authenticated === true;
  }
}
