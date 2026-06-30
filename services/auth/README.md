# Microservicio Auth

Microservicio Spring Boot para autenticacion centralizada del sistema distribuido 2026.

---

## Estado del proyecto

Actualmente incluye:

- base del microservicio `auth`
- persistencia con MySQL
- configuracion por perfiles (`dev`, `prod`)
- configuracion externa con Config Server
- registro en Eureka
- migracion inicial con Flyway
- usuarios y roles base
- autenticacion con Spring Security
- emision de JWT propia para `S8 P1`
- observabilidad base con Actuator y Prometheus
- documentacion OpenAPI en `dev`

---

## Arquitectura (estado actual)

```text
Cliente -> Auth Service -> JWT
Cliente -> Gateway -> Microservicios
```

En esta fase, `auth-service` actua como emisor de identidad para el sistema.

La validacion del token ya fue integrada en:

- `gateway` como borde seguro del sistema
- `producto` como `resource server`

Para las pruebas locales en Windows, se recomienda usar `PowerShell` con `Invoke-RestMethod` en lugar de `bash`.

En la fase actual:

- `gateway` valida JWT en el borde
- `producto` valida JWT localmente como `resource server`
- `catalogo` se mantiene sin seguridad propia y se protege solo desde `gateway`

---

## Ubicacion en la secuencia 2026-2

`auth` corresponde principalmente a:

- `S8` Control de acceso al sistema

Se apoya sobre lo ya construido en:

- `S1-S4` infraestructura distribuida base
- `S6` interaccion entre servicios y resiliencia
- `S7` observabilidad y trazabilidad

Y deja preparado el salto futuro a:

- `P2` integracion con `Keycloak` u otro proveedor de identidad

---

## Stack tecnologico

- Java 17
- Spring Boot 3.5.x
- Spring Cloud 2025.x
- Maven 3.9+
- Spring Security
- JWT (`jjwt`)
- Spring Data JPA
- MySQL 8
- Flyway
- Config Client
- Eureka Client
- Actuator
- Micrometer Prometheus
- SpringDoc OpenAPI

---

## Puertos utilizados

| Servicio | Puerto |
|---|---:|
| Auth DEV | 8041 |
| Auth PROD | 8042 |
| MySQL Auth DEV | 3341 |
| MySQL Auth PROD | 3342 |
| Config Server DEV | 7071 |
| Config Server PROD | 7072 |
| Registry Server DEV | 7081 |
| Registry Server PROD | 7082 |

---

## DEV vs PROD

| Modo | Ejecucion app | Base de datos | Configuracion | Registro | Puerto app |
|---|---|---|---|---|---:|
| DEV | `mvn spring-boot:run` | Docker/local | Config Server DEV | Registry DEV | 8041 |
| PROD | Docker | Docker | Config Server PROD | Registry PROD | 8042 |

---

## Modelo base de seguridad

Base de datos minima para esta fase:

- `users`
- `roles`
- `user_roles`

Objetivo del modelo:

- autenticar usuarios locales para `S8 P1`
- emitir JWT con claims basicos
- desacoplar identidad del resto del sistema
- dejar el sistema preparado para reemplazar `auth-service` por `Keycloak` mas adelante

Claims base del JWT:

- `sub`
- `iss`
- `iat`
- `exp`
- `roles`
- `preferred_username`

Significado de cada claim:

- `sub` -> sujeto autenticado; en esta fase representa el `username`
- `iss` -> emisor del token; identifica que el JWT fue generado por `auth`
- `iat` -> instante de emision del token
- `exp` -> instante de expiracion del token
- `roles` -> lista de autoridades del usuario autenticado, usada luego por `gateway` y microservicios para autorizacion
- `preferred_username` -> nombre de usuario legible, util para interoperabilidad y futura integracion con proveedores como `Keycloak`

---

## Endpoints actuales

### Login

```text
POST /auth/login
```

Payload:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Respuesta esperada:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600000,
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

Interpretacion del token emitido:

- `accessToken` contiene la identidad autenticada
- `tokenType` indica el prefijo esperado en el header `Authorization`
- `expiresIn` expresa la vigencia del token en milisegundos
- `roles` refleja las autoridades cargadas desde base de datos

### Observabilidad

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`

---

## Configuracion externa (config-repo)

Archivos esperados:

```text
infra/config-repo/auth-dev.yml
infra/config-repo/auth-prod.yml
```

Variables importantes:

- `jwt.secret`
- `jwt.expiration`
- `jwt.issuer`

En `prod`, el secreto se resuelve desde:

```text
${JWT_SECRET}
```

---

## Ejecucion DEV

### 1. Levantar Config Server

Desde `infra/config-server`:

```bash
mvn spring-boot:run
```

### 2. Levantar Registry Server

Desde `infra/registry-server`:

```bash
mvn spring-boot:run
```

### 3. Levantar MySQL de desarrollo

Desde `services/auth`:

```bash
docker compose -f docker-compose-dev.yml up -d
```

### 4. Ejecutar auth en DEV

Desde `services/auth`:

```bash
mvn spring-boot:run
```

### 5. Probar

Swagger:

```text
http://localhost:8041/swagger-ui/index.html
```

Health:

```text
http://localhost:8041/actuator/health
```

Prometheus:

```text
http://localhost:8041/actuator/prometheus
```

Login:

```text
POST http://localhost:8041/auth/login
```

Prueba recomendada en `PowerShell`:

```powershell
$body = @{
  username = "admin"
  password = "admin123"
} | ConvertTo-Json

$response = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8041/auth/login" `
  -ContentType "application/json" `
  -Body $body

$response
$token = $response.accessToken
```

---

## Ejecucion PROD

### 1. Levantar infraestructura base

Desde `infra`:

```bash
docker compose up -d
```

### 2. Archivo `.env`

En `services/auth/.env`:

```env
AUTH_MYSQL_ROOT_PASSWORD=root
AUTH_MYSQL_DATABASE=db_auth

SPRING_PROFILES_ACTIVE=prod
CONFIG_SERVER_URL=http://config-server:7071
JWT_SECRET=REEMPLAZAR_POR_SECRET_BASE64_GENERADO

AUTH_DB_HOST=mysql-auth
AUTH_DB_PORT=3306
AUTH_DB_NAME=db_auth
AUTH_DB_USERNAME=root
AUTH_DB_PASSWORD=root
```

### 3. Levantar auth en modo productivo

Desde `services/auth`:

```bash
docker compose up -d
```

### 4. Probar

Health:

```text
http://localhost:8042/actuator/health
```

Prometheus:

```text
http://localhost:8042/actuator/prometheus
```

Login:

```text
POST http://localhost:8042/auth/login
```

Prueba recomendada en `PowerShell`:

```powershell
$body = @{
  username = "admin"
  password = "admin123"
} | ConvertTo-Json

$response = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8042/auth/login" `
  -ContentType "application/json" `
  -Body $body

$response
```

---

## Usuarios iniciales

Para pruebas de `S8 P1` se cargan usuarios base:

- `admin / admin123`
- `user / user123`

Roles iniciales:

- `ADMIN`
- `USER`

Las contrasenas se almacenan cifradas con `BCrypt`.

---

## Interpretacion del JWT

Cuando el login es exitoso, el token emitido contiene:

- `sub` con el identificador principal del usuario autenticado
- `iss` con valor `auth`
- `iat` con el momento exacto de emision
- `exp` con el momento exacto de expiracion
- `roles` con autoridades como `ROLE_ADMIN` o `ROLE_USER`
- `preferred_username` con el nombre de usuario legible

Esto permite que el resto del sistema, especialmente `gateway`, pueda validar:

- quien es el usuario
- quien emitio el token
- si el token sigue vigente
- que roles trae asociados

---

## Observabilidad actual

`auth-service` ya expone:

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`
- logs en consola y archivo local

Archivo de log en desarrollo:

- `services/auth/logs/auth.log`

---

## Material de apoyo

- [SESION-08-SEGURIDAD-CON-AUTH-SERVICE.md](C:/ms1/ProyectosMS2026/services/auth/SESION-08-SEGURIDAD-CON-AUTH-SERVICE.md)

---

## Estado de avance

- [x] Microservicio base `auth`
- [x] Config Server
- [x] Registry Server listo para integracion
- [x] Base de datos y migracion Flyway
- [x] Usuarios y roles base
- [x] Login con Spring Security
- [x] Emision propia de JWT
- [x] Integracion base de validacion JWT en Gateway
- [x] `producto` protegido como resource server
- [ ] `catalogo` protegido localmente como resource server
- [ ] Reemplazo por `Keycloak`

---

## Siguiente paso

Continuar con el bloque de seguridad sobre la base actual:

- probar autorizacion por roles sobre `producto`
- validar el flujo completo `auth -> gateway -> producto`
- mantener `catalogo` solo detras de `gateway` en esta fase para contraste didactico
- probar el mismo esquema en `prod`
- dejar preparado el reemplazo posterior por `Keycloak`

---

## Tag sugerido

```bash
git tag -a vs08-auth -m "Auth service con Spring Security, usuarios, roles y JWT integrado a gateway"
git push origin vs08-auth
```
