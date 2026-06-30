# Sesion 07 - Observabilidad con Herramientas

Este documento sirve como guia de trabajo para la sesion de observabilidad con herramientas sobre la arquitectura de microservicios.

No reemplaza los `README` de cada modulo. Se usa como apoyo practico para esta fase del curso.

---

## Objetivo

Aprender a usar observabilidad centralizada paso a paso, desde lo mas simple hasta un caso real de investigacion:

- verificar disponibilidad de servicios
- medir trafico HTTP
- observar consumo basico de recursos
- consultar logs centralizados
- seguir una peticion end-to-end
- detectar una falla y entender su causa

Flujo esperado:

```text
Cliente -> Gateway -> Producto -> Catalogo
                       |           |
                       +---- logs y metricas ----+
                                                 |
                         Prometheus + Loki + Grafana
```

---

## Herramientas usadas

- Prometheus -> recoleccion de metricas
- Loki -> almacenamiento y consulta de logs
- Promtail -> envio de logs hacia Loki
- Grafana -> visualizacion de metricas y logs

---

## Idea central de la sesion

La idea no es comenzar con alertas ni con paneles complejos.

El recorrido didactico recomendado es:

1. Ver que todo esta arriba
2. Ver trafico
3. Ver recursos
4. Consultar logs
5. Seguir una peticion real
6. Provocar una falla controlada
7. Recien despues pensar en alertas

---

## Cambios implementados en esta sesion

### 1. Exportacion de metricas Prometheus

Se agrego la dependencia:

- `io.micrometer:micrometer-registry-prometheus`

en:

- `infra/gateway/pom.xml`
- `services/catalogo/pom.xml`
- `services/producto/pom.xml`

Y se habilito:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,circuitbreakers,circuitbreakerevents
  prometheus:
    metrics:
      export:
        enabled: true
```

en `dev` y `prod` dentro del `config-repo`.

### 2. Stack externo de observabilidad

Se creo el modulo:

- `observability/`

Con:

- `Prometheus`
- `Loki`
- `Promtail`
- `Grafana`

### 3. Diferencia entre DEV y PROD

En `dev`:

- `gateway`, `catalogo` y `producto` corren con `mvn spring-boot:run`
- Prometheus consulta `host.docker.internal`
- `observability` usa `obs-dev-net`

En `prod`:

- toda la plataforma corre en Docker
- Prometheus usa nombres Docker como `gateway`, `catalogo` y `producto`
- `observability` usa `ms-net`

### 4. Centralizacion de logs

Promtail consume:

- `infra/gateway/logs`
- `services/catalogo/logs`
- `services/producto/logs`

Y envia los logs a Loki con etiquetas por servicio.

---

## Archivos trabajados

- `observability/docker-compose-dev.yml`
- `observability/docker-compose.yml`
- `observability/prometheus/prometheus-dev.yml`
- `observability/prometheus/prometheus.yml`
- `observability/loki/config.yml`
- `observability/promtail/config.yml`
- `observability/grafana/provisioning/datasources/datasources.yml`
- `observability/README.md`
- `infra/config-repo/gateway-dev.yml`
- `infra/config-repo/catalogo-dev.yml`
- `infra/config-repo/producto-dev.yml`
- `infra/config-repo/gateway-prod.yml`
- `infra/config-repo/catalogo-prod.yml`
- `infra/config-repo/producto-prod.yml`
- `infra/gateway/pom.xml`
- `services/catalogo/pom.xml`
- `services/producto/pom.xml`

---

## Preparacion

### DEV

Levantar:

- config-server
- registry-server
- gateway
- mysql de `catalogo`
- mysql de `producto`
- catalogo
- producto

Luego:

```bash
cd observability
docker compose -f docker-compose-dev.yml up -d
```

### PROD

Levantar:

```bash
cd infra
docker compose up -d

cd ../services/catalogo
docker compose up -d

cd ../producto
docker compose up -d

cd ../../observability
docker compose up -d
```

---

## Paso 1. Ver que todo esta vivo

### Objetivo

Entender que Prometheus primero verifica disponibilidad.

### Prueba principal en DEV

Abrir:

```text
http://localhost:19090/targets
```

Se espera `UP` en:

- `prometheus`
- `gateway-dev`
- `catalogo-dev`
- `producto-dev`

### Consulta base

```promql
up
```

### Que debe aprender el alumno

- que es un target
- que significa `UP`
- que Prometheus ya esta recolectando informacion

Nota:

- en `dev`, Prometheus usa `host.docker.internal`
- esos links son validos para el contenedor
- desde el navegador del host, las URLs equivalentes son `localhost:7091`, `localhost:8081` y `localhost:9091`

---

## Paso 2. Ver trafico HTTP

### Objetivo

Pasar de "esta vivo" a "esta recibiendo requests".

### Generar trafico

Probar:

```text
http://localhost:7091/api/v1/catalogo/instancia
http://localhost:7091/api/v1/producto/instancia
http://localhost:9091/api/v1/productos/detalle/1
```

### Consulta recomendada

```promql
sum by (job) (rate(http_server_requests_seconds_count[1m]))
```

### Que debe aprender el alumno

- requests por segundo
- comparacion de trafico entre servicios
- diferencia entre metricas crudas y metricas agregadas

---

## Paso 3. Ver recursos basicos

### Objetivo

Observar consumo de recursos, no solo trafico.

### Consultas recomendadas

Memoria JVM por servicio:

```promql
sum by (job) (jvm_memory_used_bytes)
```

CPU por servicio:

```promql
avg by (job) (system_cpu_usage)
```

### Que debe aprender el alumno

- como cambia memoria y CPU segun la actividad
- por que monitorear recursos es distinto a monitorear trafico

---

## Paso 4. Entrar a Loki

### Objetivo

Entender cuando Loki empieza a aportar valor.

Prometheus responde:

- cuanto trafico hubo
- si un servicio esta arriba o abajo

Loki responde:

- que ocurrio dentro del servicio
- que mensaje se registro
- que error exacto aparecio

### Probar en Grafana Explore

Datasource:

- `Loki`

Consultas:

```logql
{service="gateway"}
```

```logql
{service="catalogo"}
```

```logql
{service="producto"}
```

Si hay mucho ruido de framework, usar consultas mas enfocadas:

```logql
{service="gateway"} |= "[GATEWAY]"
```

```logql
{service="producto"} |= "[PRODUCTO]"
```

```logql
{service="catalogo"} |= "[CATALOGO]"
```

### Que debe aprender el alumno

- que Loki centraliza logs
- que ya no necesita abrir varias consolas
- que puede investigar por servicio desde una sola interfaz

---

## Paso 5. Caso end-to-end

### Objetivo

Seguir una peticion real a traves de varios servicios.

### Peticion sugerida

```text
http://localhost:7091/api/v1/productos/detalle/1
```

### Que observar

En Prometheus o Grafana:

- aumento de requests HTTP

En Loki:

```logql
{service=~"gateway|producto|catalogo"}
```

Si el volumen de logs es alto, revisar por servicio:

```logql
{service="gateway"}
```

```logql
{service="producto"}
```

```logql
{service="catalogo"}
```

Y si se necesita reducir ruido:

```logql
{service="gateway"} |= "[GATEWAY]"
```

```logql
{service="producto"} |= "[PRODUCTO]"
```

```logql
{service="catalogo"} |= "[CATALOGO]"
```

### Que debe aprender el alumno

- que una peticion genera metricas y logs al mismo tiempo
- que las herramientas se complementan
- que la observabilidad no se reduce a un solo tipo de evidencia

---

## Paso 6. Dashboard inicial en Grafana

### Objetivo

Construir una vista minima pero util.

Paneles sugeridos:

1. Disponibilidad

```promql
up
```

2. Requests por servicio

```promql
sum by (job) (rate(http_server_requests_seconds_count[1m]))
```

3. Memoria JVM por servicio

```promql
sum by (job) (jvm_memory_used_bytes)
```

4. CPU por servicio

```promql
avg by (job) (system_cpu_usage)
```

### Que debe aprender el alumno

- como pasar de consultas manuales a paneles reutilizables
- como Grafana resume el estado del sistema

---

## Paso 7. Provocar una falla controlada

### Objetivo

Ver el valor real de la observabilidad cuando algo sale mal.

### Accion sugerida

Detener `catalogo`.

Luego repetir:

```text
http://localhost:9091/api/v1/productos/detalle/1
```

### Que observar

En metricas:

- cambios en el trafico
- posibles errores HTTP

Consulta sugerida:

```promql
sum by (job, status) (rate(http_server_requests_seconds_count{status=~"5.."}[1m]))
```

En logs:

```logql
{service="producto"}
```

y tambien:

```logql
{service=~"producto|catalogo"}
```

### Que debe aprender el alumno

- las metricas detectan la anomalia
- los logs explican la causa
- la observabilidad ayuda primero a detectar y luego a investigar

---

## Casos de trazabilidad

La plataforma ya tiene una trazabilidad basica basada en `X-Trace-ID`.

Eso permite seguir una peticion entre:

- `gateway`
- `producto`
- `catalogo`

sin requerir todavia una herramienta de tracing distribuido como Tempo o Jaeger.

### Caso 1. Peticion exitosa end-to-end

Ejecutar:

```text
http://localhost:7091/api/v1/productos/detalle/1
```

Luego revisar logs en:

- `infra/gateway/logs/gateway.log`
- `services/producto/logs/producto.log`
- `services/catalogo/logs/catalogo.log`

O en Loki con:

```logql
{service=~"gateway|producto|catalogo"}
```

Pasos para verlo en Loki:

1. Abrir Grafana en:

```text
http://localhost:13000
```

2. Ir a `Explore`.
3. Elegir el datasource `Loki`.
4. Definir un rango reciente, por ejemplo `Last 15 minutes`.
5. Ejecutar la consulta:

```logql
{service=~"gateway|producto|catalogo"}
```

6. Identificar los logs generados por la peticion reciente.
7. Buscar visualmente el mismo `traceId` en los registros de:

- `gateway`
- `producto`
- `catalogo`

8. Confirmar el recorrido:

- `gateway` recibe la peticion
- `producto` procesa la solicitud
- `producto` invoca a `catalogo`
- `catalogo` responde

9. Si hay muchos logs, reducir el rango de tiempo o filtrar primero por servicio:

```logql
{service="gateway"}
```

```logql
{service="producto"}
```

```logql
{service="catalogo"}
```

10. Si aun hay mucho ruido, filtrar por logs funcionales:

```logql
{service="gateway"} |= "[GATEWAY]"
```

```logql
{service="producto"} |= "[PRODUCTO]"
```

```logql
{service="catalogo"} |= "[CATALOGO]"
```

Evidencia esperada:

- mismo `traceId` en los tres servicios
- `gateway` recibe la peticion
- `producto` procesa y llama a `catalogo`
- `catalogo` responde la consulta

### Caso 2. Peticion con falla y fallback

1. Detener `catalogo`.
2. Ejecutar:

```text
http://localhost:9091/api/v1/productos/detalle/1
```

Luego revisar:

- logs de `producto`
- logs de `gateway`
- si existen registros previos o fallidos de `catalogo`

Consulta sugerida en Loki:

```logql
{service=~"gateway|producto|catalogo"}
```

Si el volumen es alto, revisar por servicio:

```logql
{service="producto"}
```

```logql
{service="catalogo"}
```

Y si se requiere menos ruido:

```logql
{service="producto"} |= "[PRODUCTO]"
```

Evidencia esperada:

- mismo `traceId` en el flujo observado
- error o falta de respuesta de `catalogo`
- activacion del fallback en `producto`
- relacion entre la falla observada en metricas y los logs del mismo flujo

---

## Paso 8. Introduccion a alertas

### Objetivo

Presentar la idea, sin convertirla todavia en el foco principal.

Ejemplos de expresiones utiles:

Servicio caido:

```promql
up{job="catalogo-dev"} == 0
```

Errores 5xx:

```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) by (job) > 0
```

Uso alto de CPU:

```promql
avg(system_cpu_usage) by (job) > 0.8
```

### Que debe aprender el alumno

- una alerta no reemplaza metricas ni logs
- una alerta solo avisa
- luego se investiga con Prometheus, Grafana y Loki

---

## Preguntas para el alumno

1. Que diferencia hay entre `health`, `metrics` y `prometheus`?
2. Que significa que un target este en estado `UP`?
3. Que informacion te da Prometheus que no te da Loki?
4. Que informacion te da Loki que no te da Prometheus?
5. Como seguirias una peticion `gateway -> producto -> catalogo`?
6. Que harias primero si ves una alerta de `catalogo` caido?

---

## Checklist de evaluacion

Para cerrar esta fase, el alumno debe presentar evidencia de:

- `http://localhost:19090/targets`
- targets `UP` en `gateway-dev`, `catalogo-dev` y `producto-dev`
- consulta `up`
- consulta `sum by (job) (rate(http_server_requests_seconds_count[1m]))`
- acceso a Grafana en `http://localhost:13000`
- datasource `Prometheus` operativo
- datasource `Loki` operativo
- consulta de logs por `service`
- evidencia de una peticion end-to-end
- evidencia de una falla controlada observada por metricas y logs

---

## Cierre de la fase con herramientas

La fase con herramientas se considera cerrada cuando el alumno puede demostrar:

- metricas centralizadas
- logs centralizados
- visualizacion unificada
- consultas PromQL
- consultas LogQL
- seguimiento de una peticion real
- investigacion basica de una falla

En esta etapa, la observabilidad pasa de ser:

- manual
- local
- basada en consola

a ser:

- centralizada
- visual
- consultable
- reutilizable para futuras plataformas y microservicios

---

## Siguiente paso recomendado

Despues de esta sesion, el siguiente bloque natural es:

- dashboards mas completos en Grafana
- filtros por `traceId`
- alertas reales en Grafana o Alertmanager
- integracion posterior con Kafka y microservicios dirigidos por eventos
- trazas distribuidas completas cuando el curso lo requiera
