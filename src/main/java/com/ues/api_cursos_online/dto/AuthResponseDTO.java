package com.ues.api_cursos_online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos devueltos tras un inicio de sesión correcto (CU02). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private Long idUsuario;
    private String nombreUsuario;
    private String email;
    private String rol;
    private Boolean estado;
    private String mensaje;
}
