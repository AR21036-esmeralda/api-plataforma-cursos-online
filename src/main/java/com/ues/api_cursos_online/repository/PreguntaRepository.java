package com.ues.api_cursos_online.repository;

import com.ues.api_cursos_online.entity.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    /** Lista preguntas con filtros opcionales (nulo = sin filtrar). */
    @Query("""
            SELECT p FROM Pregunta p
            JOIN FETCH p.usuario u
            JOIN FETCH p.leccion l
            WHERE (:idLeccion IS NULL OR l.idLeccion = :idLeccion)
              AND (:idUsuario IS NULL OR u.idUsuario = :idUsuario)
            ORDER BY p.idPregunta
            """)
    List<Pregunta> buscar(@Param("idLeccion") Long idLeccion,
                          @Param("idUsuario") Long idUsuario);
}
