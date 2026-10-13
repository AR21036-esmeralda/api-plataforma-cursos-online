CREATE DATABASE plataforma_cursos_oline
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;




CREATE TABLE ROL (
    idRol BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombreRol VARCHAR(100) NOT NULL UNIQUE,
    CONSTRAINT chk_nombre_rol
    CHECK (nombreRol IN ('ADMINISTRADOR', 'ESTUDIANTE', 'PROFESOR'))
);




CREATE TABLE CATEGORIA (
    idCategoria BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombreCategoria VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    estado BOOLEAN NOT NULL DEFAULT TRUE
);


CREATE TABLE USUARIO (
    idUsuario BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombreUsuario VARCHAR(100) NOT NULL,
    apellidoUsuario VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    idRol BIGINT NOT NULL,
    CONSTRAINT fk_usuario_rol 
        FOREIGN KEY (idRol) 
        REFERENCES ROL (idRol) 
        ON DELETE RESTRICT

);


CREATE TABLE CURSO (
    idCurso BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombreCurso VARCHAR(200) NOT NULL,
    descripcion VARCHAR(1000),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    idCategoria BIGINT NOT NULL,
    idUsuario BIGINT NOT NULL, 
    CONSTRAINT fk_curso_categoria 
        FOREIGN KEY (idCategoria) 
        REFERENCES CATEGORIA (idCategoria) 
        ON DELETE RESTRICT,
    CONSTRAINT fk_curso_usuario 
        FOREIGN KEY (idUsuario) 
        REFERENCES USUARIO (idUsuario) 
        ON DELETE RESTRICT
);


CREATE TABLE INSCRIPCION (
    idInscripcion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idUsuario BIGINT NOT NULL,
    idCurso BIGINT NOT NULL,
    fechaInscripcion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_inscripcion_usuario 
        FOREIGN KEY (idUsuario) 
        REFERENCES USUARIO (idUsuario) 
        ON DELETE CASCADE,
    CONSTRAINT fk_inscripcion_curso 
        FOREIGN KEY (idCurso) 
        REFERENCES CURSO (idCurso) 
        ON DELETE CASCADE,
    CONSTRAINT uq_usuario_curso 
        UNIQUE (idUsuario, idCurso)   --- evita que un usuario se inscriba mas de una vez en el mismo curso 
);


CREATE TABLE LECCION (
    idLeccion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    contenido TEXT,
    orden INT NOT NULL CHECK(orden>0),
    idCurso BIGINT NOT NULL,
    CONSTRAINT fk_leccion_curso 
        FOREIGN KEY (idCurso) 
        REFERENCES CURSO (idCurso) 
        ON DELETE CASCADE,
	   CONSTRAINT uq_curso_orden_leccion
       UNIQUE (idCurso, orden)   --evita que dos lecciones del mismo curso tengan el mismo número de orden.
);


CREATE TABLE PROGRESO (
    idProgreso BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idUsuario BIGINT NOT NULL,
    idLeccion BIGINT NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT FALSE,
    fechaCompletada TIMESTAMP ,
    CONSTRAINT fk_progreso_usuario 
        FOREIGN KEY (idUsuario) 
        REFERENCES USUARIO (idUsuario) 
        ON DELETE CASCADE,
    CONSTRAINT fk_progreso_leccion 
        FOREIGN KEY (idLeccion) 
        REFERENCES LECCION (idLeccion) 
        ON DELETE CASCADE,
    CONSTRAINT uq_usuario_leccion 
        UNIQUE (idUsuario, idLeccion), --vita que un mismo usuario tenga más de un registro de progreso para la misma lección.

	CONSTRAINT chk_progreso_fecha CHECK 
	( (estado = FALSE AND fechaCompletada IS NULL) OR
	  (estado = TRUE AND fechaCompletada IS NOT NULL) )
);



CREATE TABLE PREGUNTA (
    idPregunta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idUsuario BIGINT NOT NULL,
    idLeccion BIGINT NOT NULL,
    contenido TEXT NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pregunta_usuario 
        FOREIGN KEY (idUsuario) 
        REFERENCES USUARIO (idUsuario) 
        ON DELETE CASCADE,
    CONSTRAINT fk_pregunta_leccion 
        FOREIGN KEY (idLeccion) 
        REFERENCES LECCION (idLeccion) 
        ON DELETE CASCADE
);


CREATE TABLE RESPUESTA (
    idRespuesta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idPregunta BIGINT NOT NULL,
    idUsuario BIGINT NOT NULL,
    contenido TEXT NOT NULL,
    fecha TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_respuesta_pregunta 
        FOREIGN KEY (idPregunta) 
        REFERENCES PREGUNTA (idPregunta) 
        ON DELETE CASCADE,
    CONSTRAINT fk_respuesta_usuario 
        FOREIGN KEY (idUsuario) 
        REFERENCES USUARIO (idUsuario) 
        ON DELETE CASCADE
);


CREATE TABLE EVALUACION (
    idEvaluacion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion VARCHAR(1000),
    idCurso BIGINT NOT NULL,

    CONSTRAINT fk_evaluacion_curso
        FOREIGN KEY (idCurso)
        REFERENCES CURSO (idCurso)
        ON DELETE CASCADE,

    CONSTRAINT uq_evaluacion_curso
        UNIQUE (idCurso)   --evita que curso tenga mas de una evaluacion registrada
);



CREATE TABLE CALIFICACION (
    idCalificacion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idUsuario BIGINT NOT NULL,
    idEvaluacion BIGINT NOT NULL,
    nota NUMERIC(4,2) NOT NULL,
    fechaRealizacion TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_calificacion_usuario
        FOREIGN KEY (idUsuario)
        REFERENCES USUARIO (idUsuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_calificacion_evaluacion
        FOREIGN KEY (idEvaluacion)
        REFERENCES EVALUACION (idEvaluacion)
        ON DELETE CASCADE,

    CONSTRAINT uq_usuario_evaluacion
        UNIQUE (idUsuario, idEvaluacion),  --evita que un estudiante tenga mas de una calificacion para la misma evaluacion 

    CONSTRAINT chk_calificacion_nota
        CHECK (nota BETWEEN 0 AND 10)
);







