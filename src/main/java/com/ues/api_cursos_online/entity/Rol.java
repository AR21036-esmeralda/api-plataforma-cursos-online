package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

/** Rol de un usuario (ADMINISTRADOR, PROFESOR, ESTUDIANTE). Tabla ROL. */
@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idrol")
    private Long idRol;

    @Column(name = "nombrerol", nullable = false, unique = true, length = 100)
    private String nombreRol;
}
