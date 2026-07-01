# 🧠 Arquitectura del Sistema

Este proyecto está basado en microservicios:

- Frontend: Angular
- Backend: Spring Boot
- Seguridad: Keycloak
- Gateway: Spring Cloud Gateway
- Service Discovery: Eureka
- Observabilidad: Grafana + Prometheus + Loki

---

## 🔄 Flujo

1. Usuario entra al frontend
2. Se autentica con Keycloak
3. Recibe JWT
4. Consume APIs del backend
5. Gateway valida el token
