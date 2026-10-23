package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;

@Getter
@Setter
public class UsuarioRequestDTO {

    @NotBlank
    private String nombreUsuario;

    @NotBlank
    private String apellidoUsuario;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Boolean estado;

    @NotNull
    private Long idRol;
}
