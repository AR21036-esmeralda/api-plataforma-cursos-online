package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Evaluación de un curso. Tabla EVALUACION.
 * Un curso puede contener cero o muchas evaluaciones (1 : 0..* según la consigna).
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

    @Column(name = "fechalimite")
    private LocalDateTime fechaLimite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcurso", nullable = false)
    private Curso curso;
}
