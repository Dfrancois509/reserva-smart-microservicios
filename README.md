# ReservaSmart - Sistema de Reservas de Salas

## Descripción del proyecto

ReservaSmart es una aplicación backend basada en arquitectura de microservicios que permite gestionar usuarios, salas y reservas de salas.

El sistema permite registrar usuarios, administrar salas disponibles y crear reservas validando reglas de negocio importantes, como verificar que el usuario exista y esté activo, que la sala esté disponible y que no exista otra reserva activa en el mismo horario.

Este proyecto fue desarrollado como evaluación parcial de la asignatura Desarrollo FullStack 1, aplicando arquitectura distribuida, patrón CSR, comunicación REST entre microservicios, API Gateway, Eureka, Swagger/OpenAPI, archivos YAML y pruebas unitarias con JUnit y Mockito.

---

## Estudiante

* Denizard François

---

## Arquitectura del sistema

El proyecto está organizado como un monorepo, agrupando varios microservicios dentro de una carpeta principal.

```text
reserva-smart/
│
├── discovery-server/
├── api-gateway/
├── ms-usuarios/
├── ms-salas/
└── ms-reservas/
```

---

## Microservicios implementados

| Microservicio    | Puerto | Descripción                                               |
| ---------------- | -----: | --------------------------------------------------------- |
| discovery-server |   8761 | Servidor Eureka para registrar y descubrir microservicios |
| api-gateway      |   8080 | Punto único de entrada para acceder a los microservicios  |
| ms-usuarios      |   8081 | Gestión de usuarios                                       |
| ms-salas         |   8082 | Gestión de salas                                          |
| ms-reservas      |   8083 | Gestión de reservas y comunicación con usuarios y salas   |

---

## Tecnologías utilizadas

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* MySQL
* Spring Cloud Netflix Eureka
* Spring Cloud Gateway
* OpenFeign
* Swagger / OpenAPI
* JUnit
* Mockito
* Maven
* Postman
* IntelliJ IDEA

---

## Bases de datos utilizadas

Cada microservicio de negocio utiliza su propia base de datos.

```sql
CREATE DATABASE reserva_usuarios_db;
CREATE DATABASE reserva_salas_db;
CREATE DATABASE reserva_reservas_db;
```

---

## Rutas principales del API Gateway

El API Gateway centraliza las solicitudes en el puerto 8080.

| Microservicio | Ruta por Gateway | Ruta interna |
| ------------- | ---------------- | ------------ |
| ms-usuarios   | /api/usuarios/** | /usuarios/** |
| ms-salas      | /api/salas/**    | /salas/**    |
| ms-reservas   | /api/reservas/** | /reservas/** |

Ejemplos:

```http
GET http://localhost:8080/api/usuarios
GET http://localhost:8080/api/salas
GET http://localhost:8080/api/reservas
```

---

## Endpoints principales

### ms-usuarios

```http
POST   /usuarios
GET    /usuarios
GET    /usuarios/{id}
PUT    /usuarios/{id}
DELETE /usuarios/{id}
GET    /usuarios/{id}/activo
```

### ms-salas

```http
POST   /salas
GET    /salas
GET    /salas/{id}
PUT    /salas/{id}
PUT    /salas/{id}/estado
DELETE /salas/{id}
GET    /salas/{id}/disponible
```

### ms-reservas

```http
POST /reservas
GET  /reservas
GET  /reservas/{id}
GET  /reservas/usuario/{usuarioId}
PUT  /reservas/{id}/cancelar
```

---

## Reglas de negocio principales

### Usuarios

* No se puede crear un usuario con un email repetido.
* Un usuario puede estar ACTIVO o INACTIVO.
* El microservicio permite consultar si un usuario está activo.

### Salas

* No se puede crear una sala con un nombre repetido.
* Una sala puede estar disponible o no disponible.
* El microservicio permite consultar si una sala está disponible.

### Reservas

* No se puede crear una reserva si el usuario no existe o no está activo.
* No se puede crear una reserva si la sala no existe o no está disponible.
* No se puede reservar una sala si ya existe una reserva activa en el mismo horario.
* La hora de fin debe ser posterior a la hora de inicio.
* Una reserva activa puede ser cancelada.
* Una reserva cancelada no bloquea el horario.

---

## Comunicación entre microservicios

El microservicio `ms-reservas` se comunica con otros microservicios mediante OpenFeign.

### Comunicación con ms-usuarios

```http
GET /usuarios/{id}/activo
```

Se utiliza para validar si el usuario existe y está activo antes de crear una reserva.

### Comunicación con ms-salas

```http
GET /salas/{id}/disponible
```

Se utiliza para validar si la sala existe y está disponible antes de crear una reserva.

---

## Documentación Swagger

Cada microservicio tiene documentación Swagger disponible localmente.

```text
ms-usuarios:
http://localhost:8081/swagger-ui.html

ms-salas:
http://localhost:8082/swagger-ui.html

ms-reservas:
http://localhost:8083/swagger-ui.html
```

---

## Eureka Dashboard

El servidor Eureka permite visualizar los microservicios registrados.

```text
http://localhost:8761
```

Microservicios esperados en Eureka:

```text
API-GATEWAY
MS-USUARIOS
MS-SALAS
MS-RESERVAS
```

---

## Orden de ejecución local

Para ejecutar correctamente el sistema, se recomienda iniciar los servicios en este orden desde IntelliJ IDEA:

```text
1. discovery-server
2. ms-usuarios
3. ms-salas
4. ms-reservas
5. api-gateway
```

---

## Pruebas unitarias implementadas

Se implementaron pruebas unitarias sobre la capa de servicio usando JUnit y Mockito.

### ms-usuarios

Clase:

```text
UsuarioServiceTest
```

Pruebas implementadas:

```text
- Crear usuario correctamente.
- Evitar creación con email repetido.
- Buscar usuario por ID.
- Desactivar usuario.
- Verificar usuario activo.
```

Resultado:

```text
5 tests passed
```

### ms-salas

Clase:

```text
SalaServiceTest
```

Pruebas implementadas:

```text
- Crear sala correctamente.
- Evitar creación con nombre repetido.
- Buscar sala por ID.
- Cambiar disponibilidad de sala.
- Verificar sala disponible.
```

Resultado:

```text
5 tests passed
```

### ms-reservas

Clase:

```text
ReservaServiceTest
```

Pruebas implementadas:

```text
- Crear reserva correctamente.
- Rechazar usuario inexistente o inactivo.
- Rechazar sala no disponible.
- Evitar choque de horario.
- Validar hora de fin posterior a hora de inicio.
- Cancelar reserva activa.
```

Resultado:

```text
6 tests passed
```

---

## Pruebas con Postman

El sistema fue probado usando Postman, consumiendo tanto los microservicios directamente como a través del API Gateway.

Ejemplos por Gateway:

```http
GET http://localhost:8080/api/usuarios
GET http://localhost:8080/api/salas
GET http://localhost:8080/api/reservas
```

Crear reserva:

```http
POST http://localhost:8080/api/reservas
```

Body:

```json
{
  "usuarioId": 1,
  "salaId": 1,
  "fecha": "2026-06-23",
  "horaInicio": "10:00:00",
  "horaFin": "11:00:00"
}
```

---

## Patrón de arquitectura aplicado

Cada microservicio de negocio utiliza el patrón CSR:

```text
Controller → recibe las solicitudes HTTP.
Service → contiene la lógica de negocio.
Repository → gestiona el acceso a datos.
Model → representa las entidades del sistema.
DTO → transporta datos de entrada y salida.
```

Esta separación permite mantener el código ordenado, mantenible y fácil de explicar durante la defensa técnica.

---

## Estado actual del proyecto

```text
- discovery-server funcionando.
- api-gateway funcionando.
- ms-usuarios funcionando.
- ms-salas funcionando.
- ms-reservas funcionando.
- Comunicación REST entre microservicios funcionando.
- Swagger habilitado.
- Pruebas unitarias funcionando.
- Rutas del Gateway funcionando.
```

---

## Próximos pasos posibles

```text
- Agregar Dockerfile para contenerizar los microservicios.
- Subir el proyecto completo a GitHub.
- Preparar colección Postman.
- Preparar guion de defensa técnica.
- Agregar despliegue remoto si el docente lo solicita.
```
