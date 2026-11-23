package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos necesarios para registrar que un estudiante realizó una evaluación. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntentoEvaluacionRequestDTO {

    @NotNull(message = "El estudiante es obligatorio")
    @Positive(message = "El id del estudiante debe ser positivo")
    private Long idUsuario;
}
