package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos de entrada para crear o actualizar una evaluación. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionRequestDTO {

    @NotBlank(message = "El título de la evaluación es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El curso es obligatorio")
    @Positive(message = "El id del curso debe ser positivo")
    private Long idCurso;
}
