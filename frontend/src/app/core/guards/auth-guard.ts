import { CanActivateFn } from '@angular/router';
import { keycloak } from '../../auth/keycloak.config';

export const authGuard: CanActivateFn = () => {
  if (keycloak.authenticated) {
    return true;
  }

  keycloak.login({
    redirectUri: window.location.href,
  });

  return false;
};
