package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos de entrada para crear o actualizar una lección. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeccionRequestDTO {

    @NotBlank(message = "El título de la lección es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;

    private String contenido;

    @NotNull(message = "El orden de la lección es obligatorio")
    @Positive(message = "El orden debe ser un número positivo")
    private Integer orden;

    @NotNull(message = "El curso es obligatorio")
    @Positive(message = "El id del curso debe ser positivo")
    private Long idCurso;
}
