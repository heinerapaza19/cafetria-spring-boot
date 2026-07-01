import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit {
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  loading = signal(false);
  loggedIn = signal(false);
  userName = signal('');

  ngOnInit() {
    this.loggedIn.set(this.auth.isLoggedIn());

    // 🔥 SI YA ESTÁ LOGUEADO → MANDAR DIRECTO AL CARRO O DONDE VENÍA
    if (this.auth.isLoggedIn()) {
      const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || '/carrito';

      this.router.navigateByUrl(returnUrl);
    }
  }

  login() {
    this.loading.set(true);
    this.auth.login();
  }

  logout() {
    this.auth.logout();
  }
}
