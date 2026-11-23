package com.ues.api_cursos_online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Resultado de registrar una evaluación realizada (CU10). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentoEvaluacionResponseDTO {

    private Long idIntento;
    private Long idUsuario;
    private String nombreEstudiante;
    private Long idEvaluacion;
    private String tituloEvaluacion;
    private LocalDateTime fechaRealizacion;
    private String mensaje;
}
