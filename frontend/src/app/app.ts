import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { hasRole, keycloak } from './auth/keycloak.config';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
})
export class App implements OnInit {
  constructor(private router: Router) {}

  ngOnInit() {
    if (!keycloak.authenticated) {
      return;
    }

    console.log('Usuario:', keycloak.tokenParsed);
    console.log('Roles:', keycloak.realmAccess?.roles);

    if (hasRole('ROLE_ADMIN')) {
      this.router.navigateByUrl('/admin/dashboard');
    } else if (hasRole('ROLE_USER')) {
      this.router.navigateByUrl('/menu');
    }
  }
}
