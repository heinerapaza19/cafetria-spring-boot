Spring Boot + 🐳 Docker

⚙️ Spring Boot permite desarrollar microservicios y APIs REST de forma rápida usando Java y el ecosistema Spring.

📦 🐳 Docker facilita ejecutar las aplicaciones en contenedores ligeros y portables en cualquier entorno.

🔗 La combinación de Spring Boot y Docker permite crear sistemas escalables, organizados y fáciles de desplegar.

💻 Son tecnologías ampliamente usadas en arquitecturas modernas basadas en microservicios y aplicaciones empresariales.

## Config Server

Servidor encargado de la configuracion centralizada de los microservicios.  
Permite separar la configuracion del codigo y manejar entornos como `dev` y `prod`.

### Ejecucion DEV

```bash
cd infra/config-server
mvn spring-boot:run

Registry Server

Servidor Eureka encargado del registro y descubrimiento de microservicios.
Permite que los servicios se encuentren dinamicamente dentro de la arquitectura.

Ejecucion DEV
cd infra/registry-server
mvn spring-boot:run
API Gateway

Punto unico de entrada para los clientes.
Se encarga de redirigir las peticiones hacia los microservicios mediante balanceo y rutas lb://.

Ejecucion DEV
cd infra/gateway
mvn spring-boot:run



## Ejecucion en Produccion (PROD)

### Config Server

Servidor encargado de centralizar la configuracion externa de los microservicios.

```bash
cd infra
docker compose up -d config-server
```

---

### Registry Server

Servidor Eureka encargado del registro y descubrimiento dinamico de servicios.

```bash
cd infra
docker compose up -d registry-server
```

---

### API Gateway

Punto unico de acceso encargado de enrutar las peticiones hacia los microservicios.

```bash
cd infra
docker compose up -d gateway
```

## Microservicio Auth

Servicio encargado de la autenticacion del sistema mediante JWT.  
Se integra con MySQL, Config Server y Eureka.

### Levantar MySQL DEV

```bash
docker compose -f docker-compose-dev.yml up -d
Ejucutra Auth PROD
cd infra
docker compose up -d registry-server

Ejecutar Auth DEV
mvn spring-boot:run
Probar servicio

## Usuarios iniciales

Para pruebas de `S8 P1` se cargan usuarios base:

- `admin / admin123`
- `user / user123`

Roles iniciales:

- `ADMIN`
- `USER`

Las contrasenas se almacenan cifradas con `BCrypt`.

---
Swagger:

http://localhost:8041/swagger-ui/index.html

Login:

POST /auth/login
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

#Microservicio Producto

Servicio encargado de la gestion de productos dentro de la arquitectura distribuida.  
Se integra con MySQL, Config Server, Eureka, Gateway y `categoria` mediante Feign.

### Levantar MySQL DEV

```bash
docker compose -f docker-compose-dev.yml up -d
```

### Ejecutar Producto DEV

```bash
mvn spring-boot:run
```

### Probar servicio

Swagger:

```text
http://localhost:9091/swagger-ui/index.html
```
# Microservicio categoria

Microservicio Spring Boot para la gestion del categoria dentro de la arquitectura de microservicios 2026.

---
## 1. Levantar MySQL de desarrollo

Desde `services/categoria`:

```bash
docker compose -f docker-compose-dev.yml up -d
```

---

## 2. Ejecutar categoria en DEV

Desde `services/categoria`:

```bash
mvn spring-boot:run
```

---

## 3. Probar

Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```
# 💳 Pago Service - Cafetería Microservices

Servicio encargado de gestionar los pagos de pedidos en la cafetería.

Desde `services/pago`:

```bash
docker compose -f docker-compose-dev.yml up -d
```

---

## 2. Ejecutar pago en DEV

Desde `services/pago`:

```bash
mvn spring-boot:run
```

## 3. Probar

Swagger UI:

```text
http://localhost:6060/swagger-ui/index.html
```
# 📦 Pedidos Service - Cafetería Microservices

Servicio encargado de gestionar los pedidos de la cafetería.

Desde `services/pedidos`:

```bash
docker compose -f docker-compose-dev.yml up -d
```

---

## 2. Ejecutar pedidos en DEV

Desde `services/pedidos`:

```bash
mvn spring-boot:run
```

---

## 3. Probar

Swagger UI:

```text
http://localhost:9095/swagger-ui/index.html
```

# 📦 Pedidos Service - Cafetería Microservices

Servicio encargado de gestionar los pedidos de la cafetería.

## 1. Levantar MySQL en DEV

Desde `services/pedido`:

```bash
docker compose -f docker-compose-dev.yml up -d

2. Ejecutar pedido en DEV

Desde services/pedido:
mvn spring-boot:run

## 3. Probar

Swagger UI:

```text
http://localhost:9070/swagger-ui/index.html

```

# Observability Platform

Stack externo y compartido de observabilidad para la plataforma.

Incluye:

- Prometheus
- Loki
- Promtail
- Grafana
La idea pedagogica es:

1. primero tener un sistema distribuido funcional
2. luego tener interaccion real y fallos controlados
3. finalmente observar ese comportamiento con Prometheus, Loki y Grafana

Relacion entre modulos:

```text
infra -> expone gateway y logs
services -> exponen metricas y logs
observability -> consume metricas y logs desde infra y services
```
Importante:

- `infra` no depende de `observability`
- `observability` si depende de que `infra` y los microservicios esten levantados
- habilitar `/actuator/prometheus` en los servicios no obliga a tener Prometheus encendido
- en `dev`, `observability` no usa `ms-net`; usa una red propia porque los servicios corren en el host con `mvn`
- en `prod`, `observability` si usa `ms-net` porque todo corre en Docker

Finalmente la observabilidad:

```bash
cd observability
para crear
docker compose -f docker-compose-dev.yml up -d
lugo levatar
docker compose up -d
```
## Puertos

| Herramienta | DEV | PROD |
|---|---:|---:|
| Prometheus | 19090 | 29090 |
| Loki | 13100 | 23100 |
| Grafana | 13000 | 23000 |



Introducción a Angular

⚡ Angular es un framework moderno desarrollado por Google para crear aplicaciones web dinámicas y escalables.

🧩 Utiliza componentes y TypeScript para organizar mejor el código y facilitar el desarrollo frontend.

🔗 Permite consumir APIs REST y conectarse fácilmente con microservicios desarrollados en Spring Boot.

💻 Es ampliamente usado en sistemas web como inventarios, ventas, cafeterías y plataformas empresariales.

# en cmd de angualar ejuctar
ng serve:  4200 
y listo
---
