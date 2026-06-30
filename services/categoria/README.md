# Microservicio Catalogo

Microservicio Spring Boot para la gestion del catalogo dentro de la arquitectura de microservicios 2026.

---

## Estado del proyecto

Actualmente incluye:

- API REST funcional
- Persistencia con MySQL
- Configuracion por perfiles (`dev`, `prod`)
- Contenerizacion con Docker
- Integracion operativa con **Config Server**
- Integracion operativa con **Registry Server (Eureka)**
- Integracion operativa con **API Gateway**
- Enrutamiento dinamico con **`lb://catalogo`**
- Observabilidad basica manual para salud, metricas y logs
- Integracion lista para observabilidad externa con Prometheus, Loki y Grafana

---

## Arquitectura (estado actual)

```text
Client -> API Gateway -> Microservicios -> Registry Server -> Config Server
```

Este repositorio implementa unicamente el microservicio **Catalogo**.

---

## Stack tecnologico base 2026

- Java 17
- Spring Boot 3.5.x
- Spring Cloud 2025.x
- Maven 3.9+
- MySQL 8
- Docker
- Docker Compose
- Spring Cloud Config Client
- Eureka Client
- Flyway
- Actuator
- SpringDoc OpenAPI

---

## Puertos utilizados

| Servicio | Puerto expuesto |
|---|---:|
| Catalogo DEV | 8081 |
| Catalogo PROD | 8082 |
| MySQL DEV | 3381 |
| MySQL PROD | 3382 |
| Config Server DEV | 7071 |
| Config Server PROD | 7072 |
| Registry Server DEV | 7081 |
| Registry Server PROD | 7082 |
| Gateway DEV | 7091 |
| Gateway PROD | 7092 |

---

## DEV vs PROD

| Modo | Ejecucion app | Base de datos | Configuracion | Registro | Puerto app |
|---|---|---|---|---|---:|
| DEV | `mvn spring-boot:run` | Docker/local | Config Server DEV | Registry DEV | 8081 |
| PROD | Docker | Docker | Config Server PROD | Registry PROD | 8082 |

---

## Ubicacion en la secuencia 2026-2

`catalogo` participa principalmente en:

- `S1-S4` como microservicio base, configurable, registrable y enrutable por Gateway
- `S6` como servicio remoto consumido por `producto`
- `S7` como fuente de metricas, logs y trazabilidad

En esta etapa, `catalogo` no implementa Feign ni Circuit Breaker, pero si forma parte del flujo distribuido que se observa y protege.

---

## Observabilidad actual

`catalogo` participa en la observabilidad basica manual del sistema con:

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`
- `GET /api/v1/catalogo/instancia`
- logs con `traceId` en consola y archivo local

Archivo de log en desarrollo:

- `services/catalogo/logs/catalogo.log`

Para la guia didactica paso a paso de observabilidad, evaluacion y evidencia transversal entre `gateway`, `producto` y `catalogo`, revisar:

- [SESION-06.P2-OBSERVABILIDAD.md](C:/ms1/ProyectosMS2026/infra/SESION-06.P2-OBSERVABILIDAD.md)
- [SESION-07-OBSERVABILIDAD-CON-HERRAMIENTAS.md](C:/ms1/ProyectosMS2026/observability/SESION-07-OBSERVABILIDAD-CON-HERRAMIENTAS.md)

En modo productivo, `catalogo` ya queda listo para integrarse con la capa centralizada:

- Prometheus consume metricas desde `/actuator/prometheus`
- Promtail recoge `services/catalogo/logs/*.log`
- Loki centraliza los logs
- Grafana consulta metricas y logs desde una sola interfaz

---

# Ejecucion DEV con Config + Registry

## Objetivo

Ejecutar `catalogo` en modo desarrollo consumiendo configuracion externa y registrando la instancia en Eureka.

---

## 1. Levantar Config Server (DEV)

Desde `infra/config-server`:

```bash
mvn spring-boot:run
```

Prueba:

```text
http://localhost:7071/catalogo/dev
```

---

## 2. Levantar Registry Server (DEV)

Desde `infra/registry-server`:

```bash
mvn spring-boot:run
```

Dashboard:

```text
http://localhost:7081
```

---

## 3. Levantar MySQL de desarrollo

Desde `services/catalogo`:

```bash
docker compose -f docker-compose-dev.yml up -d
```

---

## 4. Ejecutar catalogo en DEV

Desde `services/catalogo`:

```bash
mvn spring-boot:run
```

---

## 5. Probar

Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```

Endpoint de instancia:

```text
http://localhost:8081/api/v1/catalogo/instancia
```

Acceso via Gateway DEV:

```text
http://localhost:7091/api/v1/catalogo/instancia
```

Registro en Eureka:

```text
http://localhost:7081
```

---

# Escalado manual en DEV

Para levantar una segunda instancia local de `catalogo` en desarrollo, ejecuta la aplicacion en otro puerto:

```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8085"
```

Prueba la segunda instancia:

```text
http://localhost:8085/swagger-ui/index.html
http://localhost:8085/api/v1/catalogo/instancia
```

---

# Ejecucion PROD con Config + Registry

## Objetivo

Ejecutar `catalogo` en contenedor Docker consumiendo configuracion externa y registro de servicio en Eureka.

---

## 1. Levantar infraestructura (config + registry)

Desde `infra`:

```bash
docker compose up -d
```

Pruebas:

```text
http://localhost:7072/catalogo/prod
http://localhost:7082
```

---

## 2. Archivo `.env` (modo PROD)

En `services/catalogo/.env`:

```env
CATALOGO_MYSQL_ROOT_PASSWORD=root
CATALOGO_MYSQL_DATABASE=db_catalogo

SPRING_PROFILES_ACTIVE=prod

CONFIG_SERVER_URL=http://config-server:7071

CATALOGO_DB_HOST=mysql-catalogo
CATALOGO_DB_PORT=3306
CATALOGO_DB_NAME=db_catalogo
CATALOGO_DB_USERNAME=root
CATALOGO_DB_PASSWORD=root
```

---

## 3. Redes utilizadas

- `ms-net` -> red comun de infraestructura (config-server, registry-server, gateway futuro, microservicios)
- `catalogo-int` -> red interna de catalogo (mysql + app)

---

## 4. Levantar catalogo en modo productivo

Desde `services/catalogo`:

```bash
docker compose up -d
```

---

## 5. Probar

API:

```text
http://localhost:8082/api/v1/categorias
```

Endpoint de instancia:

```text
http://localhost:8082/api/v1/catalogo/instancia
```

Acceso via Gateway PROD:

```text
http://localhost:7092/api/v1/catalogo/instancia
```

Eureka PROD (host):

```text
http://localhost:7082
```

---

# Configuracion externa (config-repo)

Archivos esperados:

```text
infra/config-repo/catalogo-dev.yml
infra/config-repo/catalogo-prod.yml
```

Puntos clave ya configurados:

- `catalogo-dev.yml` usa `eureka.client.service-url.defaultZone=http://localhost:7081/eureka`
- `catalogo-prod.yml` usa `eureka.client.service-url.defaultZone=http://registry-server:7081/eureka`

---

# Escalado manual (sin Gateway)

## 1. Bajar stack de catalogo

```bash
docker compose down
```

## 2. Levantar solo MySQL

```bash
docker compose up -d mysql-catalogo
```

## 3. Construir imagen

```bash
docker build -t catalogo-service .
```

## 4. Crear instancias

### catalogo1

```powershell
docker create `
  --name catalogo1 `
  --network ms-net `
  --env-file .env `
  -p 8082:8082 `
  catalogo-service

docker network connect catalogo-int catalogo1
docker start catalogo1
```

### catalogo2

```powershell
docker create `
  --name catalogo2 `
  --network ms-net `
  --env-file .env `
  -p 8083:8082 `
  catalogo-service

docker network connect catalogo-int catalogo2
docker start catalogo2
```

### catalogo3

```powershell
docker create `
  --name catalogo3 `
  --network ms-net `
  --env-file .env `
  -p 8084:8082 `
  catalogo-service

docker network connect catalogo-int catalogo3
docker start catalogo3
```

## 5. Probar instancias

```text
http://localhost:8082/api/v1/categorias
http://localhost:8083/api/v1/categorias
http://localhost:8084/api/v1/categorias
http://localhost:8082/api/v1/catalogo/instancia
http://localhost:8083/api/v1/catalogo/instancia
http://localhost:8084/api/v1/catalogo/instancia
```

Si quieres validar balanceo por Gateway, repite varias veces:

```text
http://localhost:7092/api/v1/catalogo/instancia
```

---

# Problemas resueltos

- Config Server no accesible -> faltaba red `ms-net`
- Registry Server no accesible -> infraestructura no levantada completa
- MySQL no accesible -> faltaba red `catalogo-int`
- Error datasource -> configuracion externa no cargada
- Error `UnknownHost` -> nombres de host/redes mal definidos

---

# Gateway + Load Balance (trabajado)

Orden aplicado durante la implementacion:

1. Crear o clonar repo `infra` desde el tag `vs03-registry-server` y repo `catalogo` tambien desde `vs03-registry-server`.
2. Crear proyecto `gateway` en `infra`.
3. Conectar `gateway` a Config Server.
4. Probar `gateway` en DEV.
5. Suscribir `gateway` a Eureka en modo DEV.
6. Definir ruta: `uri: lb://catalogo`.
7. Escalado en DEV con multiples instancias.
8. Configurar `gateway` para PROD.
9. Probar `gateway` en PROD.
10. Levantar varias instancias de `catalogo` en PROD y probar.
11. Revision de escalado automatico.

---

# Estado de avance

- [x] Config Server
- [x] Registry Server (Eureka)
- [x] API Gateway
- [x] Enrutamiento `lb://catalogo`
- [x] Observabilidad basica manual
- [x] Observabilidad con herramientas
- [ ] Feign
- [ ] Circuit Breaker
- [ ] Seguridad
- [ ] Gestion del trafico (filtros, politicas y control de peticiones)
- [ ] Integracion con frontend

---

# Siguiente paso

Continuar con atributos de calidad sobre la base actual:

- seguir participando en el flujo distribuido observado desde `S6` y `S7`
- integrar seguridad con autenticacion y autorizacion
- aplicar gestion del trafico en Gateway
- habilitar integracion con frontend

---

# Tag sugerido

```bash
git tag -a vs07-obs-tools -m "Catalogo listo para observabilidad externa con Prometheus, Loki y Grafana"
git push origin vs07-obs-tools
```
