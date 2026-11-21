package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Datos de entrada para crear o actualizar una calificación. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionRequestDTO {

    @NotNull(message = "El estudiante es obligatorio")
    @Positive(message = "El id del estudiante debe ser positivo")
    private Long idUsuario;

    @NotNull(message = "La evaluación es obligatoria")
    @Positive(message = "El id de la evaluación debe ser positivo")
    private Long idEvaluacion;

    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota no puede ser menor que 0")
    @DecimalMax(value = "10.0", message = "La nota no puede ser mayor que 10")
    @Digits(integer = 2, fraction = 2, message = "La nota admite como máximo 2 decimales")
    private BigDecimal nota;
}
