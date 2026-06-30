# Observability Platform

Stack de observabilidad para **cafetria-spring-boot** (Sesion 07 del curso).

Incluye: **Prometheus**, **Loki**, **Promtail** y **Grafana**.

Guia completa: [SESION-07-OBSERVABILIDAD-CON-HERRAMIENTAS.md](./SESION-07-OBSERVABILIDAD-CON-HERRAMIENTAS.md)

Presentacion del curso: [DIST07-P2 Observabilidad y trazabilidad](https://docs.google.com/presentation/d/1Hw131Q7WQjGiXqTvK1q_nlm3r6XVtv93k2uEmOYkJzA/edit)

## Adaptaciones a este monorepo

| Curso (repos separados) | Este proyecto |
|-------------------------|---------------|
| `services/catalogo/` | `services/categoria/` (contenedor Docker prod: `catalogo`) |
| LogQL `{service="catalogo"}` | Sigue igual (etiqueta Loki `catalogo`) |
| Archivo de log | `services/categoria/logs/catalogo.log` |

## Arranque rapido (DEV)

### 1. Infraestructura y microservicios

En terminales separadas:

```bash
cd infra/config-server && ./mvnw spring-boot:run
cd infra/registry-server && ./mvnw spring-boot:run
cd infra/gateway && ./mvnw spring-boot:run
```

MySQL de desarrollo:

```bash
cd services/categoria && docker compose -f docker-compose-dev.yml up -d
cd services/producto && docker compose -f docker-compose-dev.yml up -d
```

Aplicaciones:

```bash
cd services/categoria && ./mvnw spring-boot:run
cd services/producto && ./mvnw spring-boot:run
```

### 2. Stack de observabilidad

```bash
cd observability
docker compose -f docker-compose-dev.yml up -d
```

## URLs de validacion (DEV)

| Recurso | URL |
|---------|-----|
| Prometheus targets | http://localhost:19090/targets |
| Grafana | http://localhost:13000 (admin / admin) |
| Loki | http://localhost:13100 |
| Gateway health | http://localhost:7091/actuator/health |
| Categoria metrics | http://localhost:8081/actuator/prometheus |
| Producto metrics | http://localhost:9091/actuator/prometheus |

## Arranque (PROD)

```bash
cd infra && docker compose up -d
cd services/categoria && docker compose up -d
cd services/producto && docker compose up -d
cd observability && docker compose up -d
```

| Recurso | URL |
|---------|-----|
| Prometheus | http://localhost:29090 |
| Grafana | http://localhost:23000 |

## Consultas utiles en Grafana Explore

**Prometheus:**

```promql
up
sum by (job) (rate(http_server_requests_seconds_count[1m]))
```

**Loki:**

```logql
{service="gateway"}
{service="producto"}
{service="catalogo"}
{service=~"gateway|producto|catalogo"}
```

## Puertos

| Herramienta | DEV | PROD |
|-------------|----:|-----:|
| Prometheus | 19090 | 29090 |
| Loki | 13100 | 23100 |
| Grafana | 13000 | 23000 |

## Notas

- Los servicios deben generar logs en archivo antes de que Promtail los envie a Loki.
- En DEV, Prometheus usa `host.docker.internal` para alcanzar apps en el host.
- En PROD, todo usa la red Docker `ms-net`.
