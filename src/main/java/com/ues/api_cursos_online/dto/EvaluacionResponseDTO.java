package com.ues.api_cursos_online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos de salida de una evaluación; no expone entidades JPA. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionResponseDTO {
    private Long idEvaluacion;
    private String titulo;
    private String descripcion;
    private Long idCurso;
    private String nombreCurso;
}
