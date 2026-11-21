package com.ues.api_cursos_online.repository;

import com.ues.api_cursos_online.entity.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    /** Lista calificaciones con filtros opcionales (nulo = sin filtrar). */
    @Query("""
            SELECT c FROM Calificacion c
            JOIN FETCH c.usuario u
            JOIN FETCH c.evaluacion e
            WHERE (:idUsuario IS NULL OR u.idUsuario = :idUsuario)
              AND (:idEvaluacion IS NULL OR e.idEvaluacion = :idEvaluacion)
            ORDER BY c.idCalificacion
            """)
    List<Calificacion> buscar(@Param("idUsuario") Long idUsuario,
                              @Param("idEvaluacion") Long idEvaluacion);

    boolean existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(Long idUsuario, Long idEvaluacion);

    boolean existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacionAndIdCalificacionNot(
            Long idUsuario, Long idEvaluacion, Long idCalificacion);
}
