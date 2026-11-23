package com.ues.api_cursos_online.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgresoResponseDTO {

    private Long idProgreso;

    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;

    private Long idLeccion;
    private String tituloLeccion;

    private Long idCurso;
    private String nombreCurso;

    private Boolean estado;

    private LocalDateTime fechaCompletada;
}