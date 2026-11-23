package com.ues.api_cursos_online.repository;

import com.ues.api_cursos_online.entity.Progreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgresoRepository extends JpaRepository<Progreso, Long> {

    /**
     * Comprueba si ya existe un registro de progreso
     * para un usuario en una lección determinada.
     */
    boolean existsByUsuario_IdUsuarioAndLeccion_IdLeccion(
            Long idUsuario,
            Long idLeccion
    );

    /**
     * Busca el progreso de un usuario
     * en una lección concreta.
     */
    Optional<Progreso> findByUsuario_IdUsuarioAndLeccion_IdLeccion(
            Long idUsuario,
            Long idLeccion
    );

    /**
     * Obtiene todos los registros de progreso
     * de un usuario.
     */
    List<Progreso> findByUsuario_IdUsuario(
            Long idUsuario
    );

    /**
     * Obtiene el progreso de un usuario
     * correspondiente a las lecciones de un curso.
     */
    List<Progreso> findByUsuario_IdUsuarioAndLeccion_Curso_IdCurso(
            Long idUsuario,
            Long idCurso
    );
}