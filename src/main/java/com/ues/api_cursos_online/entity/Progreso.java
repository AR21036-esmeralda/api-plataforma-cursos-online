package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Progreso de un estudiante en una lección.
 * Tabla PROGRESO.
 *
 * Un usuario solo puede tener un registro de progreso
 * para una misma lección.
 */
@Entity
@Table(
        name = "progreso",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_usuario_leccion",
                        columnNames = {"idusuario", "idleccion"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idprogreso")
    private Long idProgreso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idleccion", nullable = false)
    private Leccion leccion;

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private Boolean estado = false;

    @Column(name = "fechacompletada")
    private LocalDateTime fechaCompletada;
}