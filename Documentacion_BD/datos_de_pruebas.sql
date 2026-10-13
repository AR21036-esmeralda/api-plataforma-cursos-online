
-- INSERCIÓN DE DATOS DE PRUEBA

-- 1. ROLES 
INSERT INTO ROL (nombreRol) VALUES 
('ADMINISTRADOR'),
('PROFESOR'),
('ESTUDIANTE');

-- 2. CATEGORIAS 
INSERT INTO CATEGORIA (nombreCategoria, descripcion) VALUES 
('Desarrollo Web', 'Cursos sobre tecnologías frontend y backend.'),
('Bases de Datos', 'Cursos sobre diseño y administración SQL.'),
('Diseño UX/UI', 'Cursos sobre experiencia de usuario e interfaces.');

-- 3. USUARIOS 
INSERT INTO USUARIO (nombreUsuario, apellidoUsuario, email, password, idRol) VALUES 
('Carlos', 'Mendoza', 'admin@plataforma.com', '$2b$10$_admin', 1),
('Ana', 'Martínez', 'ana.martinez@profesor.com', '$2b$10$d_ana', 2),
('Roberto', 'Gómez', 'roberto.gomez@profesor.com', '$2b$10$_roberto', 2),
('Lucía', 'Fernández', 'lucia.f@estudiante.com', '$2b$10$_lucia', 3),
('Diego', 'Torres', 'diego.t@estudiante.com', '$2b$10$_diego', 3),
('Elena', 'Ramírez', 'elena.r@estudiante.com', '$2b$10$_elena', 3);

-- 4. CURSOS 

INSERT INTO CURSO (nombreCurso, descripcion, idCategoria, idUsuario) VALUES 
('Desarrollo Web Fullstack con Node.js', 'Aprende a crear aplicaciones web completas desde cero.', 1, 2),
('PostgreSQL para Administradores de BD', 'Domina el diseño, optimización y consultas complejas en PostgreSQL.', 2, 3);

-- 5. INSCRIPCIONES 
-- Estudiantes 4, 5 y 6 se inscriben
INSERT INTO INSCRIPCION (idUsuario, idCurso) VALUES 
(4, 1), -- Lucía en Curso 1
(5, 1), -- Diego en Curso 1
(4, 2), -- Lucía en Curso 2
(6, 2); -- Elena en Curso 2

-- 6. LECCIONES 
INSERT INTO LECCION (titulo, contenido, orden, idCurso) VALUES 
('Introducción a HTML y CSS', 'Conceptos básicos de maquetación.', 1, 1),
('Fundamentos de JavaScript', 'Variables, funciones y estructuras.', 2, 1),
('Modelado Relacional', 'Principios de diseño de BD.', 1, 2),
('Consultas Avanzadas y Joins', 'Uso de JOINs y subconsultas.', 2, 2);

-- 7. PROGRESO
INSERT INTO PROGRESO (idUsuario, idLeccion, estado, fechaCompletada) VALUES 
(4, 1, TRUE, CURRENT_TIMESTAMP),
(4, 2, FALSE, NULL),
(5, 1, TRUE, CURRENT_TIMESTAMP);

-- 8. PREGUNTAS
INSERT INTO PREGUNTA (idUsuario, idLeccion, contenido) VALUES 
(4, 1, '¿Cuál es la diferencia entre un elemento block e inline en CSS?'),
(6, 3, '¿Cuándo conviene aplicar la Tercera Forma Normal (3FN)?');

-- 9. RESPUESTAS
INSERT INTO RESPUESTA (idPregunta, idUsuario, contenido) VALUES 
(1, 2, 'Los elementos block ocupan todo el ancho disponible, inline solo el de su contenido.'),
(2, 3, 'La 3FN se aplica para eliminar dependencias transitivas entre campos no clave.');

-- 10. EVALUACIONES 
INSERT INTO EVALUACION (titulo, descripcion, idCurso) VALUES 
('Evaluación Final de Desarrollo Web', 'Examen teórico y práctico sobre HTML, CSS y JS.', 1),
('Evaluación Final de PostgreSQL', 'Examen práctico de diseño de tablas y SQL.', 2);

-- 11. CALIFICACIONES
INSERT INTO CALIFICACION (idUsuario, idEvaluacion, nota) VALUES 
(4, 1, 9.50),
(5, 1, 7.80),
(4, 2, 10.00);




