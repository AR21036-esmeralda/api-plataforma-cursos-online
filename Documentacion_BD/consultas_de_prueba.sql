
--Verificar Inscripciones
SELECT 
    c.nombreCurso,
    CONCAT(u.nombreUsuario, ' ', u.apellidoUsuario) AS estudiante,
    i.fechaInscripcion,
    i.estado
FROM INSCRIPCION i
INNER JOIN CURSO c ON i.idCurso = c.idCurso
INNER JOIN USUARIO u ON i.idUsuario = u.idUsuario
ORDER BY c.nombreCurso;



--verificar contenido de un curso

SELECT 
    c.nombreCurso,
    l.orden,
    l.titulo AS leccion,
    l.contenido
FROM LECCION l
INNER JOIN CURSO c ON l.idCurso = c.idCurso
ORDER BY c.idCurso, l.orden;


--verificar foro de dudas

SELECT 
    l.titulo AS leccion,
    CONCAT(u_pregunta.nombreUsuario, ' ', u_pregunta.apellidoUsuario) AS estudiante,
    p.contenido AS pregunta,
    p.fecha AS fecha_pregunta,
    CONCAT(u_respuesta.nombreUsuario, ' ', u_respuesta.apellidoUsuario) AS profesor,
    r.contenido AS respuesta,
    r.fecha AS fecha_respuesta
FROM PREGUNTA p
INNER JOIN LECCION l ON p.idLeccion = l.idLeccion
INNER JOIN USUARIO u_pregunta ON p.idUsuario = u_pregunta.idUsuario
LEFT JOIN RESPUESTA r ON p.idPregunta = r.idPregunta
LEFT JOIN USUARIO u_respuesta ON r.idUsuario = u_respuesta.idUsuario;


-- Calificaciones y Exámenes

SELECT 
    c.nombreCurso,
    e.titulo AS evaluacion,
    CONCAT(u.nombreUsuario, ' ', u.apellidoUsuario) AS estudiante,
    cal.nota,
    cal.fechaRealizacion
FROM CALIFICACION cal
INNER JOIN EVALUACION e ON cal.idEvaluacion = e.idEvaluacion
INNER JOIN CURSO c ON e.idCurso = c.idCurso
INNER JOIN USUARIO u ON cal.idUsuario = u.idUsuario
ORDER BY cal.nota DESC;



--Avance General por Estudiante
SELECT 
    CONCAT(u.nombreUsuario, ' ', u.apellidoUsuario) AS estudiante,
    c.nombreCurso,
    l.titulo AS leccion,
    CASE 
        WHEN pr.estado = TRUE THEN 'Completada'
        ELSE 'Pendiente'
    END AS estado_leccion,
    pr.fechaCompletada
FROM INSCRIPCION i
INNER JOIN USUARIO u ON i.idUsuario = u.idUsuario
INNER JOIN CURSO c ON i.idCurso = c.idCurso
INNER JOIN LECCION l ON c.idCurso = l.idCurso
LEFT JOIN PROGRESO pr ON l.idLeccion = pr.idLeccion AND u.idUsuario = pr.idUsuario
ORDER BY u.nombreUsuario, l.orden;
