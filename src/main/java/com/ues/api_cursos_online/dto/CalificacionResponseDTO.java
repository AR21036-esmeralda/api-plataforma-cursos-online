package com.ues.api_cursos_online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Datos de salida de una calificación; no expone entidades JPA. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionResponseDTO {
    private Long idCalificacion;
    private Long idUsuario;
    private String nombreEstudiante;
    private Long idEvaluacion;
    private String tituloEvaluacion;
    private BigDecimal nota;
    private LocalDateTime fechaRealizacion;
}
