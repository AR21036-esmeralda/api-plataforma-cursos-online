package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Calificación de un estudiante en una evaluación. Tabla CALIFICACION.
 * Un estudiante solo puede tener una calificación por evaluación.
 */
@Entity
@Table(name = "calificacion", uniqueConstraints = {
        @UniqueConstraint(name = "uq_usuario_evaluacion", columnNames = {"idusuario", "idevaluacion"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcalificacion")
    private Long idCalificacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idevaluacion", nullable = false)
    private Evaluacion evaluacion;

    @Column(name = "nota", nullable = false, precision = 4, scale = 2)
    private BigDecimal nota;

    @Column(name = "fecharealizacion", nullable = false)
    private LocalDateTime fechaRealizacion;
}
