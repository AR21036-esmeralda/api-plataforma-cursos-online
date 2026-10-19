package com.ues.api_cursos_online.dto;

import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String email;
    private Boolean estado;
    private String rol;
}
