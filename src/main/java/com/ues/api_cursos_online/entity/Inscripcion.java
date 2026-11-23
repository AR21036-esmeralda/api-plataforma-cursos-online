package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Inscripción de un estudiante a un curso.
 * Tabla INSCRIPCION.
 *
 * Un usuario solo puede estar inscrito una vez en un mismo curso.
 */
@Entity
@Table(
        name = "inscripcion",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_usuario_curso",
                        columnNames = {"idusuario", "idcurso"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idinscripcion")
    private Long idInscripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcurso", nullable = false)
    private Curso curso;

    @Builder.Default
    @Column(name = "fechainscripcion", nullable = false)
    private LocalDateTime fechaInscripcion = LocalDateTime.now();

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}