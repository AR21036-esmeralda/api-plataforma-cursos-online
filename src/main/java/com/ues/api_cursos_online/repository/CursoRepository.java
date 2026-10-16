package com.ues.api_cursos_online.repository;

import com.ues.api_cursos_online.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    /**
     * Búsqueda con filtros opcionales (cualquier parámetro nulo se ignora).
     * Usa JOIN FETCH para traer categoría y profesor en una sola consulta.
     */
    @Query("""
            SELECT c FROM Curso c
            JOIN FETCH c.categoria cat
            JOIN FETCH c.profesor p
            WHERE (:estado IS NULL OR c.estado = :estado)
              AND (:idCategoria IS NULL OR cat.idCategoria = :idCategoria)
              AND (:idProfesor IS NULL OR p.idUsuario = :idProfesor)
            ORDER BY c.idCurso
            """)
    List<Curso> buscar(@Param("estado") Boolean estado,
                       @Param("idCategoria") Long idCategoria,
                       @Param("idProfesor") Long idProfesor);
}
