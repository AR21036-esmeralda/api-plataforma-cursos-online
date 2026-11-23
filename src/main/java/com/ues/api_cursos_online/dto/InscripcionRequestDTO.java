package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InscripcionRequestDTO {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El id del curso es obligatorio")
    private Long idCurso;
}