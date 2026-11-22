package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos de entrada para crear o actualizar una pregunta. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaRequestDTO {

    @NotNull(message = "El estudiante es obligatorio")
    @Positive(message = "El id del estudiante debe ser positivo")
    private Long idUsuario;

    @NotNull(message = "La lección es obligatoria")
    @Positive(message = "El id de la lección debe ser positivo")
    private Long idLeccion;

    @NotBlank(message = "El contenido de la pregunta es obligatorio")
    @Size(max = 2000, message = "El contenido no puede exceder 2000 caracteres")
    private String contenido;
}
