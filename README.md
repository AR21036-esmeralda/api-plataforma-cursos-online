# API para una Plataforma de Cursos Online

API REST desarrollada con Java 21, Spring Boot 4.0.8, Gradle 9.7.1 (wrapper) y PostgreSQL para gestionar una plataforma educativa en línea mediante operaciones CRUD. Se ejecuta en el puerto **8081**.

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

- Java 21, Spring Boot 4.0.8, Gradle 9.7.1 (wrapper incluido).
- Spring Data JPA / Hibernate, Spring Boot Actuator, SpringDoc OpenAPI 3.
- PostgreSQL 16 (también H2 para pruebas).
- Lombok, DTOs y Jakarta Validation.
- Docker, Kubernetes (kind o Docker Desktop), GitHub Actions y `act` (opcional).
- Postman.

## Arquitectura del proyecto

| Componente | Responsabilidad |
|---|---|
| `controller` | Gestiona las solicitudes HTTP. |
| `service` | Contiene la lógica de negocio. |
| `repository` | Gestiona el acceso a la base de datos. |
| `entity` | Representa las entidades persistentes. |
| `dto` | Define los datos de entrada y salida. |
| `exception` | Gestiona los errores de la API. |

Entidades principales: `Usuario`, `Rol`, `Categoria`, `Curso`, `Leccion`, `Inscripcion`, `Evaluacion`, `Progreso`, `Calificacion`, `Pregunta`, `Respuesta` y `IntentoEvaluacion`.

## Requisitos previos

- **JDK 21** (para ejecutar la API localmente).
- **Docker** (para construir la imagen; también lo usan kind y Docker Desktop).
- **kubectl**.
- **kind** o **Docker Desktop con Kubernetes habilitado**.
- **`act` (opcional)** para ejecutar el pipeline de GitHub Actions en local.
- PostgreSQL solo si se ejecuta la API fuera de Kubernetes (en Kubernetes lo despliega el manifiesto).

> El wrapper de Gradle **no tiene permiso de ejecución** en el repositorio: usa siempre `bash gradlew ...` (no `./gradlew`).

## Comandos principales

```bash
# Pruebas unitarias
bash gradlew clean test

# Ejecutar la API en local (necesita PostgreSQL en localhost:5434 o variables DB_*)
bash gradlew bootRun

# Generar el JAR ejecutable
bash gradlew bootJar

# Construir la imagen Docker
docker build -t api-cursos:latest .

# Cargar la imagen en kind (sustituye <cluster> por el nombre de tu cluster)
kind load docker-image api-cursos:latest --name <cluster>

# Aplicar todos los manifiestos (ConfigMap, Secret, PVC, PostgreSQL y API)
kubectl apply -f k8s/

# Exponer la API en localhost:8081
kubectl port-forward svc/api-cursos-service 8081:8081

# Ejecutar el pipeline en local con act (job "build")
act -j build
```

Si se usa Docker Desktop Kubernetes en lugar de kind, no hace falta `kind load`: la imagen ya está en el daemon de Docker. El manifiesto usa `imagePullPolicy: Never`, así que la imagen debe existir en el nodo antes de `kubectl apply`.

## Configuración del kubeconfig (`.kube-local/config`)

La rúbrica exige el kubeconfig en `.kube-local/config` y el uso de la variable `KUBECONFIG`. El directorio está excluido en `.gitignore` (contiene credenciales del cluster; **no se sube a Git**).

```bash
mkdir -p .kube-local

# Opción A: cluster kind llamado, por ejemplo, cursos-local
kind export kubeconfig --name cursos-local --kubeconfig .kube-local/config

# Opción B: contexto actual de kubectl (kind, Docker Desktop, etc.)
kubectl config view --raw --minify --flatten > .kube-local/config

export KUBECONFIG=.kube-local/config
kubectl cluster-info
```

El workflow de GitHub Actions exporta `KUBECONFIG=.kube-local/config` a nivel de job, de modo que **todos** los pasos de `kubectl` lo usan. El nombre del nodo se detecta automáticamente (`<cluster>-control-plane` o `docker-desktop`) o se define con la variable de repositorio `K8S_NODE`; el cluster se llama por defecto `cursos-local` (`K8S_CLUSTER`).

### Nota de seguridad TLS

En GitHub Actions se usa un kubeconfig real con `certificate-authority-data` (`kubectl config view --raw --minify --flatten`), sin desactivar la verificación TLS. En la ruta `act` sobre Linux el API server de kind se alcanza como `host.docker.internal:<puerto>`, hostname que normalmente **no está en los SAN del certificado**, por lo que el workflow desactiva la verificación TLS solo ahí y lo documenta con un `TODO` en el YAML. Si tu cluster incluye ese SAN (o usas Docker Desktop), fuerza la verificación con la variable de repositorio `KUBE_INSECURE_SKIP_TLS=false`.

## CI/CD (GitHub Actions y act)

`.github/workflows/ci-cd.yml` define un único job **`build`**:

1. Checkout y configuración de Java 21.
2. `bash gradlew clean test` (JUnit).
3. `bash gradlew bootJar` y verificación del JAR.
4. `docker build -t api-cursos:latest .`.
5. Preparación del cluster, kubeconfig en `.kube-local/config`, carga de la imagen y despliegue.

Los pasos 1-4 se ejecutan siempre (push, pull request y `act`). El despliegue se ejecuta solo si `env.ACT == 'true'` o el evento es `push`; en `pull_request` se omite.

- En **GitHub Actions** (ubuntu-latest) el workflow instala `kubectl`/`kind` y crea el cluster efímero `cursos-local` si no existe.
- Con **`act`**, el cluster ya debe existir en el host: `kind create cluster --name cursos-local` (o Docker Desktop Kubernetes). `act` usa el socket de Docker del host, por lo que hay que ejecutarlo con acceso a dicho socket (comportamiento por defecto). Si usas Docker Desktop con `act`, monta además el kubeconfig del host: `act -j build -v "$HOME/.kube/config:/root/.kube/config"`.
- Si el cluster act tiene otro nombre, define `K8S_NODE=<container>-control-plane` o configura `K8S_CLUSTER`.
- En el pipeline se aplican los manifiestos de forma individual (no `kubectl apply -f k8s/`) para que la contraseña definida en la variable `DB_PASSWORD` no sea sobrescrita por `k8s/secret.yaml`.

Variables de repositorio opcionales: `K8S_CLUSTER`, `K8S_NODE`, `DB_PASSWORD` (contraseña del Secret; por defecto `cursos-local-2026`, debe coincidir con `k8s/secret.yaml`) y `KUBE_INSECURE_SKIP_TLS`.

## Credenciales de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@plataforma.com` | `admin123` |
| Profesor | `ana.martinez@profesor.com` | `profesor123` |
| Estudiante | `lucia.f@estudiante.com` | `estudiante123` |

El login se realiza con `POST /api/auth/login`.

## Endpoints principales

| Endpoint | Descripción |
|---|---|
| `/swagger-ui/index.html` | Documentación interactiva (Swagger UI). |
| `/v3/api-docs` | Especificación OpenAPI en JSON. |
| `/api/auth/login` | Autenticación de usuarios. |
| `/api/inscripciones` | CRUD de inscripciones. |
| `/api/intentos-evaluacion` | Registro de evaluaciones realizadas (CU10). |
| `/actuator/health` | Health check usado por las probes de Kubernetes. |

También están disponibles `/api/cursos`, `/api/lecciones`, `/api/usuarios`, `/api/roles`, `/api/categorias`, `/api/evaluaciones`, `/api/calificaciones`, `/api/preguntas`, `/api/respuestas` y `/api/progresos`.

Tras desplegar en Kubernetes y hacer el port-forward, la base URL es `http://localhost:8081`.

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

## Pruebas con Postman

Las colecciones están en `Postman/`. Importa `API Plataforma Cursos Online.postman_collection.json`, configura la URL base `http://localhost:8081` y ejecuta las solicitudes de cada controlador para verificar CRUD, relaciones, validaciones y códigos de respuesta.

## Base de datos y migración

- `Documentacion_BD/plataforma_cursos_online_db.sql`: esquema completo (ya incluye los cambios de la Entrega #3).
- `Documentacion_BD/datos_de_pruebas.sql`: datos iniciales.
- `k8s/db-init-configmap.yaml`: copia de los dos scripts anteriores (sin `CREATE DATABASE`) usada por PostgreSQL en Kubernetes. Se monta en `/docker-entrypoint-initdb.d` y se ejecuta solo la primera vez que se crea el volumen de datos.
- `k8s/postgres.yaml`: usa un `PersistentVolumeClaim` de 1 Gi para `/var/lib/postgresql/data`; si se borra el PVC se pierden los datos y se vuelve a ejecutar la inicialización.
- `k8s/secret.yaml`: contraseña de demostración (`cursos-local-2026`) referenciada por PostgreSQL y por la API. **Cámbiala antes de usar otro entorno.**

**BDs ya creadas con el script original:** los scripts de inicialización de Kubernetes NO se vuelven a ejecutar. Aplica una sola vez la migración `Documentacion_BD/migracion_entrega3.sql`:

```bash
# En una BD desplegada en Kubernetes:
POD=$(kubectl get pod -l app=postgres -o jsonpath='{.items[0].metadata.name}')
kubectl cp Documentacion_BD/migracion_entrega3.sql "$POD:/tmp/migracion_entrega3.sql"
kubectl exec "$POD" -- psql -U postgres -d plataforma_cursos_online -f /tmp/migracion_entrega3.sql
kubectl exec "$POD" -- rm -f /tmp/migracion_entrega3.sql

# En una BD externa ya existente:
psql -U postgres -d plataforma_cursos_online -f Documentacion_BD/migracion_entrega3.sql
```

> No ejecutes `migracion_entrega3.sql` sobre una BD creada con el esquema actual: algunos `ALTER TABLE ... ADD CONSTRAINT` ya existen y fallarían. La migración es solo para esquemas antiguos.

La API usa `spring.jpa.hibernate.ddl-auto=validate`: si el esquema no coincide con las entidades, la aplicación no arranca (las probes lo reflejarán).

## Control de versiones

```bash
git status
git log --oneline
git pull
```

## Entrega #3 — Defensa Final

La entrega final contempla la demostración de los endpoints GET, POST, PUT y DELETE, el manejo de relaciones entre entidades, las validaciones y los códigos de respuesta HTTP, además de Swagger, Postman, pruebas JUnit y el despliegue mediante Docker, Kubernetes y CI/CD.

---

Proyecto académico de Programación Orientada a Objetos, Ciclo II - 2026. Universidad de El Salvador, Facultad Multidisciplinaria de Occidente.
