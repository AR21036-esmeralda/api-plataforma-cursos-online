package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgresoRequestDTO {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El id de la leccion es obligatorio")
    private Long idLeccion;

    private Boolean estado;
}