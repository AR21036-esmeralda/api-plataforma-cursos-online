package com.ues.api_cursos_online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Datos de salida de una evaluación; no expone entidades JPA. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionResponseDTO {
    private Long idEvaluacion;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaLimite;
    private Long idCurso;
    private String nombreCurso;
}
