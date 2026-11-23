package com.ues.api_cursos_online.repository;

import com.ues.api_cursos_online.entity.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    /** Lista respuestas con filtros opcionales (nulo = sin filtrar). */
    @Query("""
            SELECT r FROM Respuesta r
            JOIN FETCH r.usuario u
            JOIN FETCH r.pregunta p
            WHERE (:idPregunta IS NULL OR p.idPregunta = :idPregunta)
              AND (:idUsuario IS NULL OR u.idUsuario = :idUsuario)
            ORDER BY r.idRespuesta
            """)
    List<Respuesta> buscar(@Param("idPregunta") Long idPregunta,
                           @Param("idUsuario") Long idUsuario);

    boolean existsByPregunta_IdPregunta(Long idPregunta);

    boolean existsByPregunta_IdPreguntaAndIdRespuestaNot(Long idPregunta, Long idRespuesta);
}
