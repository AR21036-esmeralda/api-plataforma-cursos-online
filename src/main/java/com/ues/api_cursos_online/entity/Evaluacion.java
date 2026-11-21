package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Evaluación de un curso. Tabla EVALUACION.
 * Cada curso puede tener como máximo una evaluación (relación 1:1 con Curso).
 */
@Entity
@Table(name = "evaluacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idevaluacion")
    private Long idEvaluacion;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcurso", nullable = false, unique = true)
    private Curso curso;
}
