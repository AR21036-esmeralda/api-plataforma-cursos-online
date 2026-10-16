package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Curso de la plataforma. Tabla CURSO.
 * Pertenece a una Categoria (N:1) y es impartido por un Usuario con rol PROFESOR (N:1).
 */
@Entity
@Table(name = "curso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcurso")
    private Long idCurso;

    @Column(name = "nombrecurso", nullable = false, length = 200)
    private String nombreCurso;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcategoria", nullable = false)
    private Categoria categoria;

    /** Profesor asignado (en la BD la columna es idUsuario). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario profesor;
}
