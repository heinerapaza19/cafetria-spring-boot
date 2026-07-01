import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { appConfig } from './app/app.config';
import { keycloak } from './app/auth/keycloak.config';

keycloak
  .init({
    onLoad: 'check-sso',
    pkceMethod: 'S256',
    checkLoginIframe: false,
  })
  .then((authenticated) => {
    console.log('Keycloak ready:', authenticated);
    console.log('Roles:', keycloak.realmAccess?.roles);

    bootstrapApplication(App, appConfig);
  })
  .catch(console.error);
