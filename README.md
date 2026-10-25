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

También existen controladores para categorías, usuarios y roles.

**Nota:** los endpoints de inscripciones deben documentarse cuando estén implementados y probados.

## Requisitos previos

- JDK 21.
- PostgreSQL.
- Git.
- IntelliJ IDEA u otro IDE compatible con Gradle.
- Postman para probar la API.

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/AR21036-esmeralda/api-plataforma-cursos-online.git
cd api-plataforma-cursos-online
```

### 2. Configurar PostgreSQL

Crea la base de datos y ejecuta los scripts SQL incluidos en la documentación del proyecto.

Verifica la conexión en `src/main/resources/application.properties`. Configura las credenciales mediante variables de entorno y evita publicar contraseñas en GitHub.

### 3. Ejecutar las pruebas

Desde la terminal integrada de IntelliJ IDEA, ejecuta:

```powershell
.\gradlew.bat test
```

### 4. Iniciar la aplicación

```powershell
.\gradlew.bat bootRun
```

La aplicación estará disponible en:

`http://localhost:8081`

## Pruebas con Postman

Las pruebas deben comprobar las operaciones CRUD y sus casos de éxito y error.

Para la Entrega #2 se priorizara la demostración de dos entidades relacionadas: Curso y Lección.

Se recomienda verificar:

- Consultas mediante GET.
- Creación mediante POST.
- Actualización mediante PUT.
- Eliminación mediante DELETE.
- Validaciones de datos.
- Identificadores y recursos inexistentes.
- Reglas de negocio y códigos de respuesta HTTP.

## Control de versiones

El proyecto utiliza Git y GitHub para gestionar el código fuente y registrar los cambios.

Comandos útiles:

```bash
git status
git log --oneline
git pull
```

## Entrega #2

La entrega contempla la presentación general del proyecto, el repositorio y el trabajo colaborativo, la explicación de la base de datos y una demostración funcional en Postman.

También se evalúan Gradle, Lombok, DTOs, la arquitectura en capas, las validaciones y las pruebas de los endpoints.

---

Proyecto académico de Programación Orientada a Objetos, Ciclo II - 2026.

Universidad de El Salvador, Facultad Multidisciplinaria de Occidente.
