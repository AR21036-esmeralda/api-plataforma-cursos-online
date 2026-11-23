package com.ues.api_cursos_online.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InscripcionResponseDTO {

    private Long idInscripcion;

    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;

    private Long idCurso;
    private String nombreCurso;

    private LocalDateTime fechaInscripcion;

    private Boolean estado;
}