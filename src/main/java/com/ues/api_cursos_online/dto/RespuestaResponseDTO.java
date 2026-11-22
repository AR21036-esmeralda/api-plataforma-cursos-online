package com.ues.api_cursos_online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Datos de salida de una respuesta; no expone entidades JPA. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaResponseDTO {
    private Long idRespuesta;
    private Long idPregunta;
    private Long idUsuario;
    private String nombreProfesor;
    private String contenido;
    private LocalDateTime fecha;
}
