package com.ues.api_cursos_online.repository;

import com.ues.api_cursos_online.entity.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    boolean existsByUsuario_IdUsuarioAndCurso_IdCurso(Long idUsuario, Long idCurso);

    boolean existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(Long idUsuario, Long idCurso);

    Optional<Inscripcion> findByUsuario_IdUsuarioAndCurso_IdCurso(Long idUsuario, Long idCurso);

    List<Inscripcion> findByUsuario_IdUsuario(Long idUsuario);

    List<Inscripcion> findByUsuario_IdUsuarioAndEstadoTrue(Long idUsuario);

    List<Inscripcion> findByCurso_IdCurso(Long idCurso);

    List<Inscripcion> findByCurso_IdCursoAndEstadoTrue(Long idCurso);
}
