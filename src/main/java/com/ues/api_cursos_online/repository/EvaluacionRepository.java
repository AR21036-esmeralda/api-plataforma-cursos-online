package com.ues.api_cursos_online.repository;

import com.ues.api_cursos_online.entity.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    /** Lista evaluaciones; si idCurso es nulo no filtra. Trae el curso en la misma consulta. */
    @Query("""
            SELECT e FROM Evaluacion e
            JOIN FETCH e.curso c
            WHERE (:idCurso IS NULL OR c.idCurso = :idCurso)
            ORDER BY e.idEvaluacion
            """)
    List<Evaluacion> buscar(@Param("idCurso") Long idCurso);

    boolean existsByCurso_IdCurso(Long idCurso);

    boolean existsByCurso_IdCursoAndIdEvaluacionNot(Long idCurso, Long idEvaluacion);
}
