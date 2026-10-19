package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RolRequestDTO {

    @NotBlank
    private String nombreRol;
}
