package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos de entrada para crear o actualizar una respuesta. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaRequestDTO {

    @NotNull(message = "La pregunta es obligatoria")
    @Positive(message = "El id de la pregunta debe ser positivo")
    private Long idPregunta;

    @NotNull(message = "El profesor es obligatorio")
    @Positive(message = "El id del profesor debe ser positivo")
    private Long idUsuario;

    @NotBlank(message = "El contenido de la respuesta es obligatorio")
    @Size(max = 2000, message = "El contenido no puede exceder 2000 caracteres")
    private String contenido;
}
