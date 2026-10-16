package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

/** Categoría que clasifica a los cursos. Tabla CATEGORIA. */
@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcategoria")
    private Long idCategoria;

    @Column(name = "nombrecategoria", nullable = false, length = 150)
    private String nombreCategoria;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
