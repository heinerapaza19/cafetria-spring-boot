import { HttpInterceptorFn } from '@angular/common/http';
import { keycloak } from '../../auth/keycloak.config';
import { from } from 'rxjs';
import { switchMap } from 'rxjs/operators';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!keycloak.authenticated || !keycloak.token) {
    return next(req);
  }

  return from(keycloak.updateToken(30)).pipe(
    switchMap(() => {
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${keycloak.token}`,
        },
      });

      return next(authReq);
    }),
  );
};
