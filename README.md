# **PlaneStar API**

## **Descripción**

PlaneStar es una API RESTful que gestiona información sobre naves espaciales de diversas series y películas. Permite realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) sobre las naves, así como buscar naves por su nombre. Además, está integrada con **Apache Kafka** para la gestión de eventos asíncronos relacionados con la creación, actualización o eliminación de naves. El proyecto implementa seguridad con **JWT (JSON Web Tokens)** para proteger los endpoints y la autenticación de usuarios.

### **Características principales:**
- **Operaciones CRUD completas** sobre las naves espaciales.
- **Búsqueda paginada** de naves por nombre.
- **Eventos de Kafka**: Envío de eventos a Kafka cuando una nave es creada, actualizada o eliminada.
- **Seguridad JWT**: Proceso de autenticación y autorización para los endpoints.
- **Documentación interactiva** mediante **Swagger/OpenAPI**.

---

## **Índice**

1. [Tecnologías utilizadas](#1-tecnologías-utilizadas)
2. [Requisitos previos](#2-requisitos-previos)
3. [Instalación y ejecución](#3-instalación-y-ejecución)
4. [Uso de la API](#4-uso-de-la-api)
    - [Endpoints CRUD](#41-endpoints-crud)
    - [Búsqueda de naves](#42-búsqueda-de-naves-por-nombre-get-shipsearch)
    - [Autenticación JWT](#43-autenticación-jwt)
5. [Configuración](#5-configuración)
    - [Configuración de Kafka](#51-configuración-de-kafka)
    - [Configuración de la base de datos](#52-configuración-de-la-base-de-datos)
6. [Documentación API](#6-documentación-api)
7. [Estructura del proyecto](#7-estructura-del-proyecto)

---

## **1. Tecnologías utilizadas**

- **Java 17**
- **Spring Boot 3.1.4**
- **Spring Data JPA** para el manejo de la base de datos.
- **H2 Database** como base de datos en memoria para el entorno de desarrollo.
- **Apache Kafka** para la mensajería asíncrona.
- **Spring Security + JWT** para autenticación y autorización.
- **Swagger/OpenAPI** para la documentación y pruebas interactivas de la API.
- **Maven** para la gestión de dependencias y construcción del proyecto.

---

## **2. Requisitos previos**

- **Java 17** o superior.
- **Docker** y **Docker Compose** instalados.
- **Maven** para construir el proyecto.

---

## **3. Instalación y ejecución**

### **Clonar el repositorio:**

```bash
git clone https://github.com/juanda2984/plexus.git
cd planeStar
```

### **Construcción y ejecución con Docker:**

El proyecto incluye un archivo docker-compose.yml que configura los servicios necesarios, como Kafka, Zookeeper, la base de datos H2 y la propia API.

#### **1. Construir y ejecutar los contenedores:**

```bash
docker-compose up --build
```
Esto levantará todos los servicios necesarios para ejecutar la API.

#### **2. Acceder a la API:**

La API estará disponible en http://localhost:8080.

#### **3. Acceder a Swagger UI:**

La documentación interactiva estará disponible en http://localhost:8080/swagger-ui.html o http://localhost:8080/api-docs.

### **4. Uso de la API**
#### **4.1. Endpoints CRUD**
##### **1. Obtener todas las naves (GET /ships):**
Obtiene una lista paginada de todas las naves almacenadas en el sistema.

Ejemplo de solicitud:

```bash
curl -X GET "http://localhost:8080/ships?page=0&size=10" -H "Authorization: Bearer <JWT_TOKEN>"
```

Respuesta exitosa:

```json
{
  "content": [
    {
      "id": 1,
      "name": "Millennium Falcon",
      "series": "Star Wars"
    },
    {
      "id": 2,
      "name": "Enterprise",
      "series": "Star Trek"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalPages": 1,
  "totalElements": 2
}
```
##### **2. Crear una nueva nave (POST /ships):**
Crea una nueva nave espacial.

Ejemplo de solicitud:

```bash
curl -X POST "http://localhost:8080/ships" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
        "name": "TARDIS",
        "series": "Doctor Who"
      }'
```

Respuesta exitosa (201 Created):

```json
{
  "id": 3,
  "name": "TARDIS",
  "series": "Doctor Who"
}
```
##### **3. Obtener una nave por ID (GET /ships/{id}):**
Obtiene una nave específica por su ID.

Ejemplo de solicitud:

```bash
curl -X GET "http://localhost:8080/ships/1" -H "Authorization: Bearer <JWT_TOKEN>"
```

Respuesta exitosa (200 OK):

```json
{
  "id": 1,
  "name": "Millennium Falcon",
  "series": "Star Wars"
}
```

##### **4. Actualizar una nave (PUT /ships/{id}):**
Actualiza la información de una nave existente.

Ejemplo de solicitud:

```bash
curl -X PUT "http://localhost:8080/ships/1" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Millennium Falcon (Updated)",
        "series": "Star Wars"
      }'
```

Respuesta exitosa (200 OK):

```json
{
  "id": 1,
  "name": "Millennium Falcon (Updated)",
  "series": "Star Wars"
}
```

##### **5. Eliminar una nave (DELETE /ships/{id}):**
Elimina una nave del sistema por su ID.

Ejemplo de solicitud:

```bash
curl -X DELETE "http://localhost:8080/ships/1" -H "Authorization: Bearer <JWT_TOKEN>"
```

Respuesta exitosa (204 No Content):

#### **4.2. Búsqueda de naves por nombre (GET /ships/search):**
Este endpoint permite buscar naves por su nombre.

Ejemplo de solicitud:

```bash
curl -X GET "http://localhost:8080/ships/search?name=Enterprise" -H "Authorization: Bearer <JWT_TOKEN>"
```

Respuesta exitosa:

```json
{
  "content": [
    {
      "id": 2,
      "name": "Enterprise",
      "series": "Star Trek"
    }
  ]
}
```

#### **4.3. Autenticación JWT**
Para proteger los endpoints, debes autenticarte primero usando el endpoint /authenticate. Se requiere enviar las credenciales de usuario para obtener un token JWT.

Ejemplo de solicitud de autenticación:

```bash
Copiar código
curl -X POST "http://localhost:8080/authenticate" \
  -H "Content-Type: application/json" \
  -d '{
        "username": "user",
        "password": "password"
      }'
```
Respuesta exitosa (200 OK):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Este token JWT se debe incluir en los headers de todas las solicitudes subsecuentes:

```bash
-H "Authorization: Bearer <JWT_TOKEN>"
```
## **5. Configuración**
### **5.1. Configuración de Kafka**
En el archivo docker-compose.yml, se configura Kafka con los siguientes servicios:

Zookeeper: Necesario para la gestión de Kafka.
Kafka: Broker de Kafka para la mensajería.

### **5.2. Configuración de la base de datos**
La API usa una base de datos en memoria H2 para el desarrollo. La configuración se encuentra en el archivo application.properties:

```properties
spring.datasource.url=jdbc:h2:mem:planeStardb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

## **6. Documentación API**
La API está documentada usando Swagger/OpenAPI. Puedes acceder a la documentación interactiva desde:

Swagger UI: http://localhost:8080/swagger-ui.html
Documentación JSON: http://localhost:8080/api-docs

## **7. Estructura del proyecto**
```bash
planeStar/
│
├── src/
│   ├── main/
│   │   ├── java/com/org/plane/
│   │   │   ├── PlaneStarApplication.java
│   │   │   ├── aop/
│   │   │   │   └── ShipLoggingAspect.java
│   │   │   ├── controller/
│   │   │   │   └── ShipController.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── model/
│   │   │   │   └── Ship.java
│   │   │   ├── repository/
│   │   │   │   └── ShipRepository.java
│   │   │   ├── security/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── AuthRequest.java
│   │   │   │   ├── JwtRequestFilter.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── service/
│   │   │   │   ├── ShipService.java
│   │   │   │   └── impl/
│   │   │   │       └── ShipServiceImpl.java
│   │   │   └── util/
│   │   │       ├── KafkaConfig.java
│   │   │       ├── KafkaConsumer.java
│   │   │       ├── KafkaProducer.java
│   │   │       └── SwaggerConfig.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/changelog/
│   │           └── db.changelog-master.xml
│   └── test/java/com/org/plane/
│       ├── PlaneStarApplicationTests.java
│       └── ShipControllerIntegrationTest.java
├── target/
└── README.md                 									
```