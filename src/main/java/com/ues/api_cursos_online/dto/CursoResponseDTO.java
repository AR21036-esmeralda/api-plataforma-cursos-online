package com.ues.api_cursos_online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos de salida de un curso. No expone las entidades JPA. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoResponseDTO {

    private Long idCurso;
    private String nombreCurso;
    private String descripcion;
    private Boolean estado;
    private Long idCategoria;
    private String nombreCategoria;
    private Long idProfesor;
    private String nombreProfesor;
}
