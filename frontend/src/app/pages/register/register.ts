import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  private auth = inject(AuthService);

  username = '';
  email = '';
  password = '';
  confirmPassword = '';

  loading = signal(false);
  error = signal('');
  success = signal('');
  showPassword = signal(false);
  showConfirm = signal(false);

  submit(): void {
    this.error.set('');
    this.success.set('');
    this.loading.set(true);

    try {
      // Registro con Keycloak
      // No se envían username, email ni password desde Angular
      this.auth.register();
    } catch (error) {
      this.loading.set(false);
      this.error.set('No se pudo abrir el registro de Keycloak.');
      console.error('Error al abrir registro Keycloak:', error);
    }
  }

  togglePassword(): void {
    this.showPassword.update((value) => !value);
  }

  toggleConfirm(): void {
    this.showConfirm.update((value) => !value);
  }
}
