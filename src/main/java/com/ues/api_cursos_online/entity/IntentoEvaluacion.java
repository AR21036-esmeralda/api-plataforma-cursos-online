package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Registro de una evaluación realizada por un estudiante (CU10).
 * Tabla INTENTO_EVALUACION, añadida para cumplir el caso de uso
 * "Realizar evaluaciones" sin alterar el significado de Calificación.
 */
@Entity
@Table(name = "intento_evaluacion", uniqueConstraints = {
        @UniqueConstraint(name = "uq_intento_usuario_evaluacion",
                columnNames = {"idusuario", "idevaluacion"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntentoEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idintento")
    private Long idIntento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idevaluacion", nullable = false)
    private Evaluacion evaluacion;

    @Column(name = "fecharealizacion", nullable = false)
    private LocalDateTime fechaRealizacion;
}
