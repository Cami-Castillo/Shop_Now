# 🛒 ShopNow - Microservicios con Spring Boot

## Descripción

ShopNow es una aplicación desarrollada utilizando arquitectura de microservicios con Spring Boot.

El sistema permite administrar clientes y pedidos mediante APIs REST protegidas con autenticación JWT.

El proyecto fue desarrollado con fines académicos para la asignatura de Desarrollo Full Stack I de Duoc UC.

# 👥 Integrantes

- Camila Castillo
- Alejandrina Farías
- Anthony Romero
- Benjamín Romero

---

# 📁 Estructura del Proyecto

```
ShopNow
│
├── cliente/
│
├── pedido/
│
├── docs/
│   ├── Modelo Relacional
│   ├── Plan de Pruebas
│   ├── Matriz de Riesgos
│   ├── Plan de Seguridad
│   └── Documentación
│
└── README.md
```

---

# 📌 Características

- Arquitectura basada en microservicios.
- CRUD completo de clientes.
- CRUD completo de pedidos.
- Autenticación y autorización mediante JWT.
- Comunicación entre microservicios.
- Persistencia de datos con Spring Data JPA.
- Documentación automática con Swagger.
- Validación de datos.
- Pruebas unitarias utilizando JUnit y Mockito.

---

## Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Swagger OpenAPI
- JUnit 5
- Mockito

---

## Arquitectura

El proyecto está compuesto por dos microservicios:

- Cliente 
- Pedido 

Cada microservicio posee su propia base de datos y se comunican mediante llamadas REST.

---

## Funcionalidades

### Clientes

- Login
- Registrar cliente
- Buscar cliente
- Listar clientes
- Actualizar cliente
- Eliminar cliente
- Carga masiva

### Pedidos

- Registrar pedido
- Buscar pedido
- Listar pedidos
- Actualizar pedido
- Eliminar pedido
- Carga masiva

---

## Seguridad

El sistema utiliza:

- Spring Security
- Autenticación JWT
- Protección de endpoints
- Control de acceso mediante roles

---

