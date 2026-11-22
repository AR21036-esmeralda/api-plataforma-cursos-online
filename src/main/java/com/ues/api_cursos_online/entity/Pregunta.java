package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Pregunta que un estudiante hace sobre una lección. Tabla PREGUNTA. */
@Entity
@Table(name = "pregunta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpregunta")
    private Long idPregunta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idleccion", nullable = false)
    private Leccion leccion;

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
