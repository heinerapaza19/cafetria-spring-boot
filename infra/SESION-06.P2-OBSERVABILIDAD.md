# Sesion 6 P2 - Observabilidad Basica

Este documento sirve como guia de trabajo para alumnos sobre la sesion de observabilidad basica en una arquitectura de microservicios.

No reemplaza los `README` de cada modulo. Se usa como apoyo practico para esta fase del curso.

---

## Objetivo

Poder responder preguntas como:

- que servicio recibio la peticion
- que servicio llamo a otro servicio
- que request fallo
- cuando entro en accion el Circuit Breaker
- que instancia respondio

Flujo esperado:

```text
Cliente -> Gateway -> Producto -> Catalogo
```

---

## Evidencias esperadas

Al finalizar, el alumno debe poder mostrar:

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/metrics/http.server.requests`
- `GET /actuator/metrics/system.cpu.usage`
- `GET /actuator/metrics/jvm.memory.used`
- `GET /actuator/circuitbreakers`
- `GET /actuator/circuitbreakerevents`
- `GET /api/v1/producto/instancia`
- `GET /api/v1/catalogo/instancia`
- `GET /api/v1/productos/detalle/1`

---

## Cambios implementados en esta sesion

### 1. Actuator estandarizado en DEV

Se configuraron estos endpoints en:

- `infra/config-repo/producto-dev.yml`
- `infra/config-repo/catalogo-dev.yml`
- `infra/config-repo/gateway-dev.yml`

Configuracion usada:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,circuitbreakers,circuitbreakerevents
  endpoint:
    health:
      show-details: always
```

Nota:

- `circuitbreakers` y `circuitbreakerevents` tienen mas sentido en `producto`, porque ahi esta Resilience4j.
- En `gateway` y `catalogo`, lo importante para clase es `health`, `info` y `metrics`.

---

### 2. Endpoint `/instancia` estandarizado

Se ajustaron los endpoints de:

- `producto`
- `catalogo`

Respuesta esperada:

```json
{
  "servicio": "producto",
  "instancia": "9091",
  "host": "DESKTOP-01",
  "traceId": "0f4d3f7e-7c1e-4bdb-9f18-2d6e615d9d43"
}
```

Y para `catalogo`:

```json
{
  "servicio": "catalogo",
  "instancia": "8081",
  "host": "DESKTOP-01",
  "traceId": "0f4d3f7e-7c1e-4bdb-9f18-2d6e615d9d43"
}
```

Esto ayuda a validar:

- registro en Eureka
- ruteo por Gateway
- balanceo entre instancias
- reconocimiento real de quien respondio
- correlacion de la peticion actual con logs

---

### 3. Logs de trazabilidad simple

Se agregaron logs didacticos para que el alumno vea el recorrido entre servicios.

En `producto`:

```text
[PRODUCTO] Buscando detalle de producto con ID: 1
[PRODUCTO] Consultando categoriaId=1 en catalogo
```

En `catalogo`:

```text
[CATALOGO] Buscando categoria id=1
```

Cuando el Circuit Breaker activa fallback en `producto`:

```text
[PRODUCTO] Fallback activado para producto ID 1. Motivo: ...
```

---

### 4. Propagacion de `X-Trace-ID`

Ya existian filtros de correlacion en `producto` y `catalogo`.

En esta sesion se completo la trazabilidad agregando:

- un filtro global en `gateway` para crear o reutilizar `X-Trace-ID`
- una configuracion Feign en `producto` para reenviar `X-Trace-ID` hacia `catalogo`

Con eso, el mismo identificador puede viajar en la ruta:

```text
Cliente -> Gateway -> Producto -> Catalogo
```

Esto permite relacionar logs de varios servicios sobre una misma peticion.

---

## Archivos trabajados

- `infra/config-repo/producto-dev.yml`
- `infra/config-repo/catalogo-dev.yml`
- `infra/config-repo/gateway-dev.yml`
- `infra/gateway/src/main/resources/logback-spring.xml`
- `infra/gateway/src/main/java/com/upeu/gateway/filter/TraceIdGlobalFilter.java`
- `services/producto/src/main/resources/logback-spring.xml`
- `services/catalogo/src/main/resources/logback-spring.xml`
- `services/producto/src/main/java/com/upeu/producto/config/FeignTraceConfig.java`
- `services/producto/src/main/java/com/upeu/producto/controller/GatewayInstanciasController.java`
- `services/catalogo/src/main/java/com/upeu/catalogo/controller/GatewayInstanciasController.java`
- `services/producto/src/main/java/com/upeu/producto/service/impl/ProductoServiceImpl.java`
- `services/catalogo/src/main/java/com/upeu/catalogo/service/impl/CategoriaServiceImpl.java`

---

## Practica guiada para alumnos

### Paso 1. Levantar infraestructura

Levantar:

- config-server
- registry-server
- gateway

### Paso 2. Levantar microservicios

Levantar:

- catalogo
- producto

### Paso 3. Verificar salud del sistema

Probar:

```text
http://localhost:7091/actuator/health
http://localhost:9091/actuator/health
http://localhost:8081/actuator/health
```

### Paso 3.1. Verificar metricas base

Primero listar metricas disponibles:

```text
http://localhost:7091/actuator/metrics
http://localhost:9091/actuator/metrics
http://localhost:8081/actuator/metrics
```

Luego consultar metricas especificas:

```text
http://localhost:7091/actuator/metrics/http.server.requests
http://localhost:9091/actuator/metrics/http.server.requests
http://localhost:8081/actuator/metrics/http.server.requests
http://localhost:9091/actuator/metrics/system.cpu.usage
http://localhost:9091/actuator/metrics/jvm.memory.used
```

Interpretacion basica:

- `http.server.requests`: cantidad y comportamiento de requests HTTP
- `system.cpu.usage`: uso actual de CPU del proceso/sistema
- `jvm.memory.used`: memoria usada por la JVM

### Paso 4. Verificar instancia de cada servicio

Probar:

```text
http://localhost:9091/api/v1/producto/instancia
http://localhost:8081/api/v1/catalogo/instancia
http://localhost:7091/api/v1/producto/instancia
http://localhost:7091/api/v1/catalogo/instancia
```

### Paso 5. Verificar detalle con Feign

Probar:

```text
http://localhost:9091/api/v1/productos/detalle/1
http://localhost:7091/api/v1/productos/detalle/1
```

El alumno debe revisar los logs y confirmar:

- `producto` recibio la peticion
- `producto` llamo a `catalogo`
- `catalogo` atendio la consulta

Donde ver los logs en DEV:

- en la consola donde se ejecuto cada servicio con `mvn spring-boot:run`
- en archivos locales generados por cada servicio:
  - `infra/gateway/logs/gateway.log`
  - `services/producto/logs/producto.log`
  - `services/catalogo/logs/catalogo.log`

### Paso 6. Verificar Circuit Breaker

1. Detener `catalogo`.
2. Repetir:

```text
http://localhost:9091/api/v1/productos/detalle/1
```

3. Revisar:

- respuesta con `categoria: null`
- logs de fallback
- endpoint `/actuator/circuitbreakers`
- endpoint `/actuator/circuitbreakerevents`

Endpoints utiles para esta validacion:

```text
http://localhost:9091/actuator/circuitbreakers
http://localhost:9091/actuator/circuitbreakerevents
http://localhost:9091/actuator/metrics/http.server.requests
http://localhost:9091/actuator/metrics/system.cpu.usage
http://localhost:9091/actuator/metrics/jvm.memory.used
```

---

## Preguntas para el alumno

1. Que diferencia hay entre `health` y `metrics`?
2. Que evidencia muestra que `producto` llamo a `catalogo`?
3. Para que sirve `X-Trace-ID`?
4. Que cambia en la respuesta cuando falla `catalogo`?
5. Como sabes que se activo el fallback?
6. Que metrica consultarias para verificar invocaciones HTTP del endpoint?

---

## Cierre de la fase manual

La fase manual de observabilidad se considera cerrada cuando el alumno puede demostrar, sin herramientas externas de centralizacion, estas capacidades:

- revisar salud del servicio con `health`
- consultar metricas base con `metrics`
- seguir una peticion entre `gateway`, `producto` y `catalogo`
- identificar `traceId`, `servicio`, `instancia` y `host`
- detectar visualmente cuando entra el fallback del Circuit Breaker
- recuperar logs desde consola o archivo en entorno `dev`

En esta etapa, la observabilidad es:

- manual
- humana
- basada en Actuator, logs y archivos locales

Todavia no incluye:

- dashboards centralizados
- consulta unificada de logs
- alertas
- trazas distribuidas completas

---

## Checklist de evaluacion

Para cerrar esta fase, el alumno debe presentar evidencia de:

- `GET /actuator/health` en `gateway`, `producto` y `catalogo`
- `GET /actuator/metrics` y al menos una metrica especifica como `http.server.requests`
- `GET /api/v1/producto/instancia` y `GET /api/v1/catalogo/instancia`
- una peticion `GET /api/v1/productos/detalle/1` con logs correlacionados
- recuperacion de logs desde consola o desde archivo local

Si se desea evaluacion completa de resiliencia, adicionalmente debe presentar:

- evidencia del fallback cuando `catalogo` no esta disponible
- consulta de `GET /actuator/circuitbreakers`
- consulta de `GET /actuator/circuitbreakerevents`

---

## Siguiente paso recomendado

Despues de esta sesion, el siguiente bloque natural es:

- observabilidad con herramientas
- centralizacion de metricas con Prometheus
- centralizacion de logs con Loki
- visualizacion unificada con Grafana
- multiples instancias para demostrar balanceo con evidencia visual

Si el curso prioriza seguridad, entonces el siguiente salto puede ser:

- Spring Security
- JWT
- proteccion del Gateway
