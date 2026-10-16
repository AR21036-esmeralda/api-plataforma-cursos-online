package com.ues.api_cursos_online.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Usuario de la plataforma. Tabla USUARIO.
 * NOTA: versión mínima necesaria para que Curso pueda relacionarse con su profesor.
 * El CRUD completo de Usuario lo desarrolla otro integrante (parte 4).
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private Long idUsuario;

    @Column(name = "nombreusuario", nullable = false, length = 100)
    private String nombreUsuario;

    @Column(name = "apellidousuario", nullable = false, length = 100)
    private String apellidoUsuario;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idrol", nullable = false)
    private Rol rol;
}
