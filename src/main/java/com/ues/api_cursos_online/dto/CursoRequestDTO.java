package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos de entrada para crear (POST) o actualizar (PUT) un curso. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoRequestDTO {

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 200, message = "El nombre del curso no puede exceder 200 caracteres")
    private String nombreCurso;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    /** Opcional: si no se envía, el curso se crea activo (true). */
    private Boolean estado;

    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "El id de la categoría debe ser positivo")
    private Long idCategoria;

    @NotNull(message = "El profesor es obligatorio")
    @Positive(message = "El id del profesor debe ser positivo")
    private Long idProfesor;
}
