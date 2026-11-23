package com.ues.api_cursos_online.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos de entrada para crear o actualizar una categoría (CU23). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombreCategoria;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    /** Opcional: si no se envía, la categoría se crea activa. */
    private Boolean estado;
}
