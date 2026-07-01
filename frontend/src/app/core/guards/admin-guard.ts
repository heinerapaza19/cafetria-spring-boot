import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { hasRole, keycloak } from '../../auth/keycloak.config';

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);

  if (!keycloak.authenticated) {
    keycloak.login({
      redirectUri: window.location.origin + '/admin/dashboard',
    });
    return false;
  }

  if (hasRole('ROLE_ADMIN')) {
    return true;
  }

  router.navigateByUrl('/menu');
  return false;
};
