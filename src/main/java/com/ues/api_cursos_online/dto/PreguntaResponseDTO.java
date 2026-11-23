package com.ues.api_cursos_online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Datos de salida de una pregunta; no expone entidades JPA. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaResponseDTO {
    private Long idPregunta;
    private Long idUsuario;
    private String nombreEstudiante;
    private Long idLeccion;
    private String tituloLeccion;
    /** Curso al que pertenece la lección (CU14). */
    private Long idCurso;
    private String nombreCurso;
    private String contenido;
    private LocalDateTime fecha;
}
