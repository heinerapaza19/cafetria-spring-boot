import Keycloak from 'keycloak-js';

export const keycloak = new Keycloak({
  url: 'http://localhost:8080',
  realm: 'cafeteria-realm',
  clientId: 'cafeteria-app',
});

export function hasRole(role: string): boolean {
  return keycloak.realmAccess?.roles?.includes(role) ?? false;
}
