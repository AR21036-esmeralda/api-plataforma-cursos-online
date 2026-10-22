package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

/** Lección perteneciente a un curso de la plataforma. */
@Entity
@Table(name = "leccion", uniqueConstraints = {
        @UniqueConstraint(name = "uq_curso_orden_leccion", columnNames = {"idcurso", "orden"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idleccion")
    private Long idLeccion;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcurso", nullable = false)
    private Curso curso;
}
