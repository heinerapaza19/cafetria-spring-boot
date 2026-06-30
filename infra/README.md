# Infraestructura de Microservicios

Este modulo contiene la infraestructura base de la arquitectura de microservicios.

---

## Componentes actuales

- config-repo (configuracion externa)
- Config Server (Spring Cloud Config Server)
- Registry Server (Eureka)
- API Gateway

---

## Componentes planificados

- Seguridad
- Gestion del trafico en Gateway
- Integracion con frontend

---

## Arquitectura actual

```text
Client -> Gateway -> Microservicios -> Registry Server -> Config Server -> config-repo
```

Evolucion objetivo:

```text
Client -> Gateway + atributos de calidad -> Microservicios -> Registry Server -> Config Server
```

---

## Puertos utilizados

| Servicio | Puerto |
|---|---:|
| Config Server DEV | 7071 |
| Config Server PROD | 7072 |
| Registry Server DEV | 7081 |
| Registry Server PROD | 7082 |
| Gateway DEV | 7091 |
| Gateway PROD | 7092 |

---

## Red de infraestructura

Se utiliza una red Docker comun:

```text
ms-net
```

Esta red permite la comunicacion entre:

- config-server
- registry-server
- gateway
- microservicios

---

## Estructura del modulo

```text
infra/
  config-server/
  registry-server/
  gateway/
  config-repo/
  docker-compose.yml
```

---

## Ubicacion en la secuencia 2026-2

Este modulo sostiene principalmente la Unidad 1:

- `S1` Arquitectura base orientada a produccion
- `S2` Configuracion centralizada del sistema
- `S3` Registro y descubrimiento de servicios + ejecucion concurrente
- `S4` Punto unico de acceso y distribucion de trafico

Tambien sirve de base para la Unidad 2, especialmente en:

- `S6` Interaccion entre servicios y resiliencia
- `S7` Observabilidad y trazabilidad
- `S8` Control de acceso al sistema
- `S9` Gestion del trafico del sistema

---

## Config Server

Servidor de configuracion centralizada para los microservicios.

Permite:

- externalizar configuracion
- separar codigo de configuracion
- soportar multiples entornos (`dev`, `prod`)
- facilitar despliegue de microservicios

Modo utilizado:

```text
native
```

Ruta del repositorio montado:

```text
/config-repo
```

### Levantar Config Server

DEV:

```bash
cd infra/config-server
mvn spring-boot:run
```

PROD:

```bash
cd infra
docker compose up -d config-server
```

### Pruebas

DEV:

```bash
curl http://localhost:7071/catalogo/dev
```

PROD:

```bash
curl http://localhost:7072/catalogo/prod
```

---

## Registry Server

Servidor de registro y descubrimiento de servicios.

Permite:

- registro automatico de microservicios
- descubrimiento dinamico
- integracion con API Gateway mediante `lb://`

### Levantar Registry Server

DEV:

```bash
cd infra/registry-server
mvn spring-boot:run
```

PROD:

```bash
cd infra
docker compose up -d registry-server
```

### Acceso a Eureka

```text
DEV  -> http://localhost:7081
PROD -> http://localhost:7082
```

---

## config-repo

Contiene la configuracion externa de infraestructura y microservicios.

Archivos actuales:

```text
config-repo/
  catalogo-dev.yml
  catalogo-prod.yml
  gateway-dev.yml
  gateway-prod.yml
  producto-dev.yml
  producto-prod.yml
  registry-server-dev.yml
  registry-server-prod.yml
```

### Levantar Geteway
DEV:

```bash
cd infra/gateway
mvn spring-boot:run
```




Ejemplo base:

```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

Ejemplo actual de resiliencia externa para `producto`:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      catalogo:
        slidingWindowSize: 5
        minimumNumberOfCalls: 3
        failureRateThreshold: 50
        waitDurationInOpenState: 5s
        permittedNumberOfCallsInHalfOpenState: 2
        automaticTransitionFromOpenToHalfOpenEnabled: true
```

---

## Flujo de uso

1. Levantar infraestructura base.

```bash
docker compose up -d
```

2. Verificar endpoints.

```text
http://localhost:7072/catalogo/prod
http://localhost:7082
http://localhost:7092/api/v1/catalogo/instancia
http://localhost:7092/api/v1/producto/instancia
```

3. Levantar microservicios.
4. Verificar registro en Eureka.
5. Probar enrutamiento por Gateway con `lb://catalogo`.
6. Probar enrutamiento por Gateway con `lb://producto`.

---

## Observabilidad actual

La infraestructura ya soporta observabilidad basica manual para la operacion diaria y validacion de integracion:

- configuracion de Actuator en `gateway`, `producto` y `catalogo`
- logs locales en archivo para `gateway`
- propagacion de `X-Trace-ID` desde `gateway` hacia los microservicios
- soporte para validaciones de `health`, `metrics`, `circuitbreakers` y `circuitbreakerevents`

Este `README` documenta la capacidad operativa e integracion.

La guia paso a paso para clase y evaluacion se mantiene aparte en:

- [SESION-06.P2-OBSERVABILIDAD.md](C:/ms1/ProyectosMS2026/infra/SESION-06.P2-OBSERVABILIDAD.md)
- [SESION-07-OBSERVABILIDAD-CON-HERRAMIENTAS.md](../observability/SESION-07-OBSERVABILIDAD-CON-HERRAMIENTAS.md)

La observabilidad con herramientas ya no vive dentro de `infra`.

Separacion actual:

- `infra/` mantiene infraestructura base y compartida
- `observability/` centraliza Prometheus, Loki, Promtail y Grafana

Relacion entre modulos:

```text
infra -> expone servicios y logs
services -> exponen metricas y logs
observability -> consume metricas y logs desde infra y services
```

Importante:

- `infra` no depende de `observability`
- `observability` si depende de que `infra` y los microservicios esten levantados para poder scrapear metricas y leer logs
- habilitar `/actuator/prometheus` en los servicios no obliga a levantar Prometheus

--- 

## Resiliencia actual

La infraestructura ya soporta configuracion externa para Circuit Breaker desde `config-repo`.

Estado actual:

- `producto` consume configuracion Resilience4j desde `producto-dev.yml` y `producto-prod.yml`
- el circuito configurado se llama `catalogo`
- protege la llamada remota de `producto` hacia `catalogo`
- `catalogo` no requirio cambios para esta fase

Separacion de responsabilidades:

- `infra` centraliza la configuracion externa
- `producto` implementa el uso del Circuit Breaker
- `catalogo` mantiene su API sin cambios

---

## Problemas comunes

### 1. Microservicio no conecta a config-server

Causa:

- red incorrecta

Solucion:

- conectar el servicio a `ms-net`

### 2. Microservicio no aparece en Eureka

Causa:

- `defaultZone` incorrecto
- `registry-server` no disponible

Solucion:

- en DEV usar `http://localhost:7081/eureka`
- en Docker usar `http://registry-server:7081/eureka`

### 3. Configuracion no cargada

Causa:

- archivo no existe en `config-repo`

Solucion:

- verificar nombres por entorno (`*-dev.yml`, `*-prod.yml`)

### 4. Uso incorrecto de localhost en Docker

Dentro de Docker:

- Incorrecto: `localhost`
- Correcto: `config-server`, `registry-server`

---

## Estado de avance

- [x] Config Server
- [x] Registry Server (Eureka)
- [x] API Gateway
- [x] Enrutamiento `lb://catalogo` y `lb://producto`
- [x] Feign
- [x] Circuit Breaker + Observabilidad basica manual
- [x] Integracion lista para observabilidad externa
- [x] Base de seguridad en Gateway para validacion JWT
- [x] Integracion con `auth-service` y restriccion de rutas privadas desde el borde
- [ ] Gestion del trafico (filtros, politicas y control de peticiones)
- [ ] Integracion con frontend

---

## Siguiente paso

Continuar con los atributos de calidad sobre la base actual:

- consolidar `S6` como capa de interaccion y resiliencia sobre la base distribuida
- usar `S7` para observar el sistema ya integrado con herramientas externas
- consolidar `S8` con pruebas de autorizacion y rutas protegidas desde `gateway`
- aplicar gestion del trafico en Gateway
- habilitar integracion con frontend

---

## Tag sugerido

```bash
git tag -a vs08-auth -m "Gateway integrado con auth-service y validacion JWT en el borde"
git push origin vs08-auth
```
