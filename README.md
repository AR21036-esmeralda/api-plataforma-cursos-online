# API para una Plataforma de Cursos Online

API REST desarrollada con Java 21, Spring Boot, Gradle y PostgreSQL para gestionar una plataforma educativa en línea mediante operaciones CRUD.

Proyecto académico de la asignatura Programación Orientada a Objetos, Universidad de El Salvador, Facultad Multidisciplinaria de Occidente.

## Información académica

| Campo | Detalle |
|---|---|
| Universidad | Universidad de El Salvador |
| Facultad | Facultad Multidisciplinaria de Occidente |
| Carrera | Ingeniería en Desarrollo de Software |
| Asignatura | Programación Orientada a Objetos |
| Ciclo | II - 2026 |
| Proyecto | Tema 4: API para una Plataforma de Cursos Online |

## Descripción del proyecto

La plataforma permite gestionar cursos, lecciones, usuarios y otros recursos educativos. El modelo de datos contempla la relación entre estudiantes y cursos mediante inscripciones.

### Reglas de negocio

- Un profesor puede crear y administrar cursos.
- Un estudiante puede inscribirse en varios cursos.
- Un curso puede contener varias lecciones.
- La API debe validar los datos y aplicar las reglas de negocio.
- La relación de inscripciones permite consultar los cursos de un estudiante y los estudiantes de un curso.

## Tecnologías utilizadas

- Java 21
- Spring Boot
- Gradle
- Spring Data JPA y Hibernate
- PostgreSQL
- Lombok
- DTOs y Jakarta Validation
- Postman
- Swagger (OpenAPI 3)
- Git y GitHub

## Arquitectura del proyecto

El proyecto utiliza una arquitectura en capas para separar las responsabilidades.

| Componente | Responsabilidad |
|---|---|
| `controller` | Gestiona las solicitudes HTTP. |
| `service` | Contiene la lógica de negocio. |
| `repository` | Gestiona el acceso a la base de datos. |
| `entity` | Representa las entidades persistentes. |
| `dto` | Define los datos de entrada y salida. |
| `exception` | Gestiona los errores de la API. |

## Modelo de datos

Las principales entidades son:

`Usuario`, `Rol`, `Categoria`, `Curso`, `Leccion`, `Inscripcion`, `Evaluacion`, `Progreso`, `Calificacion`, `Pregunta` y `Respuesta`.

La entidad `Inscripcion` representa la relación entre estudiantes y cursos.

## Endpoints principales

La API utiliza el prefijo `/api` y se ejecuta, de forma predeterminada, en el puerto `8081`.

### Cursos — `/api/cursos`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/cursos` | Listar cursos. |
| GET | `/api/cursos/{id}` | Consultar un curso por identificador. |
| POST | `/api/cursos` | Crear un curso. |
| PUT | `/api/cursos/{id}` | Actualizar un curso. |
| DELETE | `/api/cursos/{id}` | Eliminar un curso. |

### Lecciones — `/api/lecciones`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/lecciones` | Listar lecciones. |
| GET | `/api/lecciones/{id}` | Consultar una lección por identificador. |
| POST | `/api/lecciones` | Crear una lección. |
| PUT | `/api/lecciones/{id}` | Actualizar una lección. |
| DELETE | `/api/lecciones/{id}` | Eliminar una lección. |

También se encuentran implementados los siguientes módulos:

- **Usuarios:** gestión de usuarios mediante GET, POST, PUT y DELETE. La creación devuelve `201 Created` y la eliminación `204 No Content`.
- **Roles:** administración de roles de usuario.
- **Categorías:** administración de categorías de cursos.
- **Evaluaciones:** CRUD de evaluaciones mediante `/api/evaluaciones`.
- **Calificaciones:** CRUD de calificaciones mediante `/api/calificaciones`.

Los endpoints implementados pueden consultarse en Swagger UI.

## Requisitos previos

- JDK 21.
- PostgreSQL.
- Git.
- IntelliJ IDEA u otro IDE compatible con Gradle.
- Postman para probar la API.

## Instalación y ejecución

### 1. Clonar el repositorio

git clone https://github.com/AR21036-esmeralda/api-plataforma-cursos-online.git
cd api-plataforma-cursos-online

### 2. Configurar PostgreSQL

Crea la base de datos y ejecuta los scripts SQL incluidos en la documentación del proyecto.

Verifica la conexión en `src/main/resources/application.properties`. Configura las credenciales mediante variables de entorno y evita publicar contraseñas en GitHub.

### 3. Ejecutar las pruebas

Desde la terminal integrada de IntelliJ IDEA, ejecuta:

.\gradlew.bat test

### 4. Iniciar la aplicación

.\gradlew.bat bootRun

La aplicación estará disponible en:

`http://localhost:8081`

## Documentación de la API con Swagger

La API incorpora Swagger UI y OpenAPI 3 para consultar y probar los endpoints REST de forma interactiva.

Con la aplicación en ejecución, acceder a:
- **Swagger UI:** http://localhost:8081/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8081/v3/api-docs
La documentación incluye los endpoints GET, POST, PUT y DELETE, organizados por controlador, junto con los parámetros y modelos de datos utilizados.
Si la aplicación se ejecuta en un puerto diferente, se debe reemplazar `8081` por el puerto correspondiente.


## Pruebas con Postman

La API cuenta con una colección de Postman para comprobar las operaciones REST y validar las respuestas HTTP.

La colección se encuentra en:

`Postman/API Plataforma Cursos Online.postman_collection.json`

### Ejecución de las pruebas

1. Iniciar la aplicación Spring Boot.
2. Abrir Postman e importar la colección JSON.
3. Configurar la URL base: `http://localhost:8081`.
4. Ejecutar las solicitudes de los diferentes controladores.
5. Verificar las respuestas y los códigos HTTP obtenidos.

### Operaciones a verificar

- GET: consultar y listar registros.
- POST: crear nuevos registros.
- PUT: actualizar registros existentes.
- DELETE: eliminar registros.
- Validar las relaciones entre entidades.
- Comprobar datos incorrectos y recursos inexistentes.

### Códigos de respuesta HTTP

| Código | Descripción |
|---|---|
| 200 OK | Solicitud procesada correctamente. |
| 201 Created | Recurso creado correctamente. |
| 204 No Content | Operación completada sin contenido de respuesta. |
| 400 Bad Request | Datos o parámetros inválidos. |
| 404 Not Found | Recurso no encontrado. |
| 409 Conflict | Conflicto con una regla de negocio o restricción. |
| 500 Internal Server Error | Error interno del servidor. |


### Respuestas HTTP del módulo Usuarios

El módulo Usuarios devuelve `201 Created` al registrar un usuario y `204 No Content` al eliminarlo correctamente. Las demás operaciones conservan sus respuestas correspondientes.

Estas operaciones fueron verificadas mediante Postman, junto con las validaciones de errores `400`, `404` y `409`.

## Despliegue con Docker y Kubernetes

El proyecto utiliza Docker para construir la imagen de la API y Kubernetes para desplegar la aplicación junto con PostgreSQL.

- `Dockerfile`: construcción de la imagen Docker.
- `k8s/postgres.yaml`: configuración de PostgreSQL.
- `k8s/api-deployment.yaml`: despliegue de la API.
- `.github/workflows/ci-cd.yml`: automatización de pruebas, construcción y despliegue.

### Comandos principales

Construir la imagen:

```bash
docker build -t api-cursos-online:latest .
```

Verificar los recursos desplegados:

```bash
kubectl get pods,svc
```

El despliegue requiere Docker, Kubernetes y kubectl configurados. El pipeline CI/CD puede ejecutarse localmente con `act`.

## Control de versiones

El proyecto utiliza Git y GitHub para gestionar el código fuente y registrar los cambios.

Comandos útiles:

```bash
git status
git log --oneline
git pull
```

## Entrega #3 — Defensa Final

La entrega final contempla la demostración de los endpoints GET, POST, PUT y DELETE, el manejo de relaciones entre entidades, las validaciones y los códigos de respuesta HTTP.

También incluye la documentación con Swagger y Postman, las pruebas unitarias con JUnit y la demostración del despliegue mediante Kubernetes y CI/CD.

---

Proyecto académico de Programación Orientada a Objetos, Ciclo II - 2026.

Universidad de El Salvador, Facultad Multidisciplinaria de Occidente.
