package com.ues.api_cursos_online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos de salida de una lección; no expone entidades JPA. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeccionResponseDTO {
    private Long idLeccion;
    private String titulo;
    private String contenido;
    private Integer orden;
    private Long idCurso;
    private String nombreCurso;
}
