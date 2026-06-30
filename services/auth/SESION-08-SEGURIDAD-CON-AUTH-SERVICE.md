# Sesion 08 - Seguridad con Auth Service

Este documento sirve como guia de trabajo para la sesion de seguridad sobre la arquitectura de microservicios.

No reemplaza los `README` de cada modulo. Se usa como apoyo practico para esta fase del curso.

---

## Objetivo

Implementar una primera capa de seguridad industrial sobre el sistema distribuido:

- autenticacion centralizada
- usuarios y roles
- emision de JWT
- base lista para integracion con `gateway`
- arquitectura preparada para reemplazo posterior por `Keycloak`

---

## Idea central de la sesion

En esta fase, `gateway` no se convierte en proveedor de identidad.

Se crea un microservicio especializado:

- `auth-service`

Responsable de:

- login
- validacion de credenciales
- carga de usuarios y roles
- emision de JWT

En el estado actual de `S8 P1`:

- `gateway` ya valida ese JWT
- `producto` ya se comporta como resource server
- `catalogo` se mantiene protegido solo desde `gateway` para comparar enfoques
- el emisor de tokens podra ser reemplazado despues por `Keycloak`

---

## Arquitectura objetivo de P1

```text
Cliente -> Auth Service -> JWT
Cliente -> Gateway -> Producto / Catalogo
```

En `P1`:

- `auth-service` autentica
- `auth-service` emite JWT
- el resto del sistema se prepara para consumir esa identidad

---

## Cambios implementados en esta sesion

### 1. Creacion de auth-service

Se creo el modulo:

- `services/auth`

Con base alineada al resto de microservicios:

- Config Client
- Eureka Client
- Actuator
- Prometheus
- Flyway
- MySQL
- Spring Security
- SpringDoc

### 2. Configuracion externa

Se agregaron:

- `infra/config-repo/auth-dev.yml`
- `infra/config-repo/auth-prod.yml`

Incluyen:

- datasource
- eureka
- management
- configuracion JWT

### 3. Modelo de seguridad

Se definio una base minima:

- `users`
- `roles`
- `user_roles`

Con el objetivo de:

- autenticar usuarios locales en `P1`
- emitir JWT propios
- desacoplar identidad de `gateway`
- facilitar reemplazo posterior por `Keycloak`

### 4. Migracion y seed inicial

Se creo:

- `V1__create_users_roles_tables.sql`

Y se cargan usuarios iniciales:

- `admin / admin123`
- `user / user123`

Con roles:

- `ADMIN`
- `USER`

### 5. Login y JWT

Se implemento:

- `POST /auth/login`

Con:

- autenticacion via Spring Security
- contrasenas cifradas con `BCrypt`
- emision de JWT con claims estandar

Claims emitidos:

- `sub`
- `iss`
- `iat`
- `exp`
- `roles`
- `preferred_username`

Significado de los claims:

- `sub` -> sujeto autenticado; en esta fase representa el `username`
- `iss` -> emisor del token; identifica a `auth`
- `iat` -> momento exacto en que el token fue emitido
- `exp` -> momento exacto en que el token deja de ser valido
- `roles` -> autoridades del usuario, utiles para autorizacion coarse-grained
- `preferred_username` -> nombre de usuario legible y reusable en integraciones futuras

---

## Archivos trabajados

- `services/auth/pom.xml`
- `services/auth/src/main/resources/application.yml`
- `services/auth/src/main/resources/logback-spring.xml`
- `services/auth/src/main/resources/db/migration/V1__create_users_roles_tables.sql`
- `services/auth/src/main/java/com/upeu/auth/AuthApplication.java`
- `services/auth/src/main/java/com/upeu/auth/config/JwtProperties.java`
- `services/auth/src/main/java/com/upeu/auth/config/PasswordConfig.java`
- `services/auth/src/main/java/com/upeu/auth/config/SecurityConfig.java`
- `services/auth/src/main/java/com/upeu/auth/config/DataInitializer.java`
- `services/auth/src/main/java/com/upeu/auth/entity/AuthUser.java`
- `services/auth/src/main/java/com/upeu/auth/entity/Role.java`
- `services/auth/src/main/java/com/upeu/auth/repository/AuthUserRepository.java`
- `services/auth/src/main/java/com/upeu/auth/repository/RoleRepository.java`
- `services/auth/src/main/java/com/upeu/auth/dto/AuthLoginRequest.java`
- `services/auth/src/main/java/com/upeu/auth/dto/AuthLoginResponse.java`
- `services/auth/src/main/java/com/upeu/auth/service/CustomUserDetailsService.java`
- `services/auth/src/main/java/com/upeu/auth/service/JwtService.java`
- `services/auth/src/main/java/com/upeu/auth/service/AuthService.java`
- `services/auth/src/main/java/com/upeu/auth/controller/AuthController.java`
- `services/auth/docker-compose-dev.yml`
- `services/auth/docker-compose.yml`
- `infra/config-repo/auth-dev.yml`
- `infra/config-repo/auth-prod.yml`

---

## Preparacion

### DEV

Levantar:

- config-server
- registry-server
- mysql de `auth`
- auth

### PROD

Levantar:

- infra
- mysql de `auth`
- auth

---

## Paso 1. Levantar auth-service en DEV

### 1. Config Server

```bash
cd infra/config-server
./mvnw spring-boot:run
```

### 2. Registry Server

```bash
cd infra/registry-server
./mvnw spring-boot:run
```

### 3. MySQL auth dev

```bash
cd services/auth
docker compose -f docker-compose-dev.yml up -d
```

### 4. Auth dev

```bash
cd services/auth
./mvnw spring-boot:run
```

---

## Paso 2. Verificar salud y observabilidad base

Probar:

```text
http://localhost:8041/actuator/health
http://localhost:8041/actuator/metrics
http://localhost:8041/actuator/prometheus
```

Que debe aprender el alumno:

- `auth-service` nace integrado al ecosistema del sistema distribuido
- seguridad no se implementa aislada de operacion y observabilidad

---

## Paso 3. Verificar carga inicial de usuarios y roles

Validar que la aplicacion arranca sin error de migracion ni de seed.

Usuarios esperados:

- `admin / admin123`
- `user / user123`

Roles esperados:

- `ADMIN`
- `USER`

Que debe aprender el alumno:

- la seguridad parte de un modelo persistente
- no se depende de usuarios hardcodeados en memoria

---

## Paso 4. Probar login

### Request

```text
POST http://localhost:8041/auth/login
```

Payload:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Respuesta esperada

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600000,
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

Que debe aprender el alumno:

- Spring Security autentica
- `auth-service` emite JWT
- el token ya representa identidad y roles
- cada claim del token tiene una responsabilidad concreta dentro del sistema

---

## Paso 5. Probar fallos de autenticacion

### Caso 1

Usuario inexistente

### Caso 2

Password incorrecta

### Caso 3

Usuario deshabilitado

Que debe aprender el alumno:

- autenticacion no es solo emitir token
- el sistema debe fallar correctamente cuando las credenciales no son validas

---

## Paso 6. Inspeccionar el JWT

Revisar que el token contenga:

- `sub`
- `iss`
- `iat`
- `exp`
- `roles`
- `preferred_username`

Interpretacion recomendada:

- `sub` responde quien es el usuario autenticado
- `iss` responde quien emitio el token
- `iat` y `exp` controlan la vigencia del JWT
- `roles` habilita autorizacion por perfiles o autoridades
- `preferred_username` ayuda a interoperabilidad, depuracion y trazabilidad

Que debe aprender el alumno:

- el contrato real entre `auth-service` y el resto del sistema es el JWT
- esa es la clave para luego reemplazar el emisor por `Keycloak`

---

## Paso 7. Entender la frontera hacia gateway

En esta sesion:

- `auth-service` autentica y emite

En el cierre actual de esta fase:

- `gateway` ya valida el JWT
- ya existen rutas publicas y privadas
- `producto` ya valida JWT localmente
- `catalogo` queda sin seguridad propia en esta fase y se restringe desde `gateway`

Que debe aprender el alumno:

- identidad y enforcement no son la misma responsabilidad
- `auth-service` y `gateway` colaboran, pero no son lo mismo
- no todos los microservicios tienen que adoptar la misma estrategia de proteccion al mismo tiempo

---

## Paso 8. Integrar validacion JWT en gateway

En este bloque, `gateway` deja de ser solo punto de enrutamiento y pasa a comportarse como borde seguro del sistema.

Se implementa:

- validacion de firma del JWT
- validacion del `issuer`
- validacion estandar de vigencia del token
- lectura del claim `roles`
- mapeo de `roles` a autoridades de Spring Security

Tambien se dejan rutas publicas:

- `/auth/**`
- `/actuator/health`
- `/actuator/info`
- `/actuator/prometheus`
- rutas Swagger necesarias para documentacion

Y el resto de rutas queda protegido.

Que debe aprender el alumno:

- `auth-service` emite identidad
- `gateway` aplica enforcement
- el JWT es el contrato de seguridad entre ambos

---

## Paso 9. Probar rutas publicas y privadas desde gateway

### 1. Reiniciar servicios clave en DEV

Levantar o reiniciar:

- `config-server`
- `registry-server`
- `auth`
- `gateway`

### 2. Obtener JWT desde auth

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

Guardar el valor de:

- `accessToken`
- o usar directamente la variable `$token`

### 3. Probar una ruta privada sin token

```powershell
Invoke-WebRequest `
  -Uri "http://localhost:7091/api/v1/productos/detalle/1"
```

Resultado esperado:

- `401 Unauthorized`

### 4. Probar la misma ruta con token

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:7091/api/v1/productos/detalle/1" `
  -Headers @{ Authorization = "Bearer $token" }
```

Resultado esperado:

- acceso permitido
- respuesta del flujo normal del sistema

### 5. Probar una ruta publica

```text
http://localhost:7091/actuator/health
```

Resultado esperado:

- las rutas publicas siguen accesibles sin JWT

Que debe aprender el alumno:

- seguridad no significa bloquear todo indiscriminadamente
- el borde del sistema debe distinguir entre rutas publicas y privadas
- el token ya tiene valor operativo real en la arquitectura

---

## Preguntas para el alumno

1. Por que `gateway` no deberia ser el proveedor principal de identidad?
2. Que diferencia hay entre autenticar y autorizar?
3. Por que el modelo `users`, `roles`, `user_roles` es mejor que guardar un solo rol en la tabla de usuario?
4. Que ventajas da emitir JWT con claims estandar?
5. Por que este diseño facilita una migracion futura a `Keycloak`?

---

## Checklist de evaluacion

Para cerrar esta fase, el alumno debe presentar evidencia de:

- `auth-service` levantado en `dev`
- `GET /actuator/health`
- `GET /actuator/prometheus`
- migracion Flyway aplicada
- usuarios y roles iniciales cargados
- `POST /auth/login` exitoso
- fallo correcto ante credenciales invalidas
- token JWT emitido con claims esperados
- `gateway` rechazando rutas privadas sin token
- `gateway` aceptando rutas privadas con token valido
- `producto` rechazando acceso directo sin token
- `producto` aceptando acceso directo con token valido

---

## Cierre de la fase auth-service

La fase `S8 P1` se considera bien encaminada cuando el alumno puede demostrar:

- autenticacion centralizada
- usuarios y roles persistidos
- login funcional
- JWT emitido correctamente
- integracion base con `gateway`
- rutas publicas y privadas diferenciadas desde el borde
- `producto` protegido como resource server
- `catalogo` aun protegido solo por `gateway` para contraste didactico

---

## Siguiente paso recomendado

Despues de esta sesion, el siguiente bloque natural es:

- pruebas de autorizacion por rol sobre `producto`
- validacion integral del flujo `auth -> gateway -> producto`
- prueba comparativa entre `producto` protegido localmente y `catalogo` protegido solo desde `gateway`
- pruebas en `prod`
- preparacion para evolucionar a `Keycloak`
