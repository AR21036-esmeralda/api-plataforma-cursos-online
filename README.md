# API para una Plataforma de Cursos Online

API REST desarrollada con Spring Boot para gestionar una plataforma educativa en línea, donde estudiantes, profesores y administradores interactúan con cursos, lecciones, evaluaciones e inscripciones.

Proyecto de ciclo de la asignatura Programación Orientada a Objetos, Universidad de El Salvador, Facultad Multidisciplinaria de Occidente.

---

## Tabla de contenidos

- [Información académica](#información-académica)
- [Equipo de desarrollo](#equipo-de-desarrollo)
- [Descripción del proyecto](#descripción-del-proyecto)
- [Alcance funcional](#alcance-funcional)
- [Modelo de datos](#modelo-de-datos)
- [Cómo clonar el repositorio](#cómo-clonar-el-repositorio)

---

## Información académica

| Campo | Detalle |
|---|---|
| Universidad | Universidad de El Salvador |
| Facultad | Facultad Multidisciplinaria de Occidente |
| Departamento | Departamento de Ingeniería y Arquitectura |
| Carrera | Ingeniería en Desarrollo de Software |
| Asignatura | Programación Orientada a Objetos |
| Ciclo | II - 2026 |
| Coordinador de cátedra | Ing. Erick Adiel Trigueros Jerez |
| Tutor asignado (GT03) | Ing. Francisco Javier Morales Ayala |

## Equipo de desarrollo

| Nombre | Carnet |
|---|---|
| Esmeralda Isabel Álvarez Rivas | AR21036 |
| Raquel Abigail Hernández Martínez | HM21008 |
| Marco Josué Orellana Cortez | OC23006 |
| Javier Edgardo Vásquez Galeano | VG25001 |
| América de La Paz Medrano | MM00130 |

---

## Descripción del proyecto

La plataforma organiza la gestión de cursos, lecciones, evaluaciones e inscripciones de un entorno educativo en línea. Un profesor puede crear y administrar los cursos que le pertenecen, mientras que un estudiante puede inscribirse en varios cursos, seguir su progreso y ser evaluado.

El sistema reconoce tres tipos de usuario:

- **Estudiante.** Explora cursos, se inscribe, avanza en las lecciones, resuelve evaluaciones y consulta sus calificaciones.
- **Profesor.** Administra el contenido de los cursos que tiene asignados: lecciones, evaluaciones, calificaciones y respuestas a preguntas.
- **Administrador.** Gestiona usuarios, cursos y categorías a nivel general de la plataforma.

La API expone estas operaciones mediante los métodos GET, POST, PUT y DELETE, siguiendo una arquitectura en capas con separación entre controladores, servicios, repositorios y DTOs.

## Alcance funcional

**Estudiante**
Registro e inicio de sesión, consulta de cursos disponibles, inscripción y baja de cursos, consulta de lecciones y progreso, resolución de evaluaciones, consulta de calificaciones y preguntas al profesor.

**Profesor**
Consulta de sus cursos asignados, gestión de lecciones y evaluaciones, calificación de actividades, consulta de estudiantes inscritos y respuesta a preguntas.

**Administrador**
Gestión de usuarios (registro, actualización, activación y baja), gestión de cursos (creación, actualización, asignación de profesor) y gestión de categorías.

El detalle completo de requerimientos, casos de uso y reglas de negocio está documentado en el PDF de la Entrega 1, entregado por campus virtual.

## Modelo de datos

Entidades principales del dominio:

`Usuario`, `Rol`, `Categoria`, `Curso`, `Leccion`, `Evaluacion`, `Inscripcion`, `Progreso`, `Calificacion`, `Pregunta`, `Respuesta`.

La relación entre `Usuario` y `Curso` a través de `Inscripcion` permite listar los cursos de un estudiante y los estudiantes de un curso, que es la regla de negocio central del proyecto.

## Cómo clonar el repositorio

```
git clone https://github.com/AR21036-esmeralda/api-plataforma-cursos-online.git
cd api-plataforma-cursos-online
```

---

Proyecto académico desarrollado para la asignatura Programación Orientada a Objetos, Ciclo II - 2026, Universidad de El Salvador.
