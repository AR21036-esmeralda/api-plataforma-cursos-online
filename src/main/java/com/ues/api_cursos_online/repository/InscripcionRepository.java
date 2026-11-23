package com.ues.api_cursos_online.repository;

import com.ues.api_cursos_online.entity.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    /**
     * Comprueba si un usuario ya está inscrito en un curso.
     */
    boolean existsByUsuario_IdUsuarioAndCurso_IdCurso(
            Long idUsuario,
            Long idCurso
    );

    /**
     * Obtiene las inscripciones de un estudiante.
     * Nos servirá para consultar los cursos de ese estudiante.
     */
    List<Inscripcion> findByUsuario_IdUsuario(Long idUsuario);

    /**
     * Obtiene las inscripciones de un curso.
     * Nos servirá para consultar los estudiantes inscritos.
     */
    List<Inscripcion> findByCurso_IdCurso(Long idCurso);
}